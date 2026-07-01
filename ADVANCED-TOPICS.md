# Advanced Topics

This document explains how **tavnit** works conceptually: what it does, how it
treats your source files internally, and how its commands nest, scope, and finally
disappear from the generated output.

## 1. How a file is decomposed internally

To understand how commands behave, it helps to see how tavnit *reads* a file.

Internally, a source file is **tokenized into a flat chain of two kinds of parts**:

- **Plain text** — everything that is *not* a tavnit command block. Crucially, this
  includes ordinary comments. A normal `<!-- a real comment -->` that does **not** contain the
  magic `@tt{{{ ... }}}@` brackets is treated as plain text like any other character in the
  file.
- **Template comments** — the comment blocks that *do* contain the magic brackets
  `@tt{{{ ... }}}@`. Only the part **inside** the brackets is interpreted as commands.

### Visualizing the chain

Take this small HTML source fragment:

```html
<ul><!-- @tt{{{ @foreach [iteratorExpression="model.items" loopVariable="item"] }}}@ -->
  <li>An item</li><!-- @tt{{{ @end-foreach }}}@ -->
</ul>
```

tavnit tokenizes it into an alternating chain of plain-text and template-comment
parts:

```
┌─────────────┐  ┌───────────────────────────┐  ┌──────────────────┐  ┌─────────────────┐  ┌──────────┐
│ PLAIN TEXT  │→ │ TEMPLATE COMMENT          │→ │ PLAIN TEXT       │→ │ TEMPLATE COMMENT│→ │ PLAIN    │
│ "<ul>"      │  │ @foreach [iterator…]      │  │ "\n  <li>An      │  │ @end-foreach    │  │ TEXT     │
│             │  │                           │  │  item</li>"      │  │                 │  │ "\n</ul>"│
└─────────────┘  └───────────────────────────┘  └──────────────────┘  └─────────────────┘  └──────────┘
   (kept as         (an instruction —             (kept as output,        (an instruction —    (kept as
    output)          NOT output)                   may be transformed)      NOT output)          output)
```

The key mental model:

- **Plain-text parts are the material of the output.** They are what the generated renderer
  prints (possibly transformed — a value replaced, a region repeated, a region omitted).
- **Template-comment parts are instructions.** They are never printed. They tell the renderer
  *how* to treat the plain-text parts around them, and they are deleted from the final output
  (see [section 4](#4-how-template-comments-disappear-from-the-output)).

A file that contains **no** template comments at all is ignored entirely — tavnit
generates nothing for it.

### How the chain is processed

The chain runs through a fixed pipeline (see `ContentParser`):

1. **Tokenize** the raw file into the plain-text / template-comment chain shown above.
2. **Resolve** each template-comment part into its individual commands and attributes.
3. **Preprocess** — validate mutually-exclusive commands, then apply comment-moving
   (`@move-comment-backward` / `@move-comment-forward`) and comment-expansion adjustments.
4. **Resolve the nesting structure** — match opening and closing commands, insert auto-close
   commands, and validate that everything is properly nested (see [section 3](#3-nesting-scope-and-closing-commands)).
5. **Custom validation** of the now well-formed command chain.
6. **Split into renderer descriptions** — one per `@template-renderer` — and generate the
   Kotlin renderer class(es).

---

## 2. Nesting, scope, and closing commands

Most commands do not act on a single point in the file — they act on a **region** of the chain.
That region is the command's **scope** (its area of effect): everything from the command's
opening up to its matching close.

### Scope = "the plain text I influence"

Consider `@foreach`. It does not just mark a spot; it says *"repeat everything between here and
my close foreach element of the collection"*. The plain text inside that region is its scope.
The same is true for `@if` (render the scope only when the condition holds),
`@replace-value-by-expression` (apply the replacement to all plain text in the scope),
`@ignore-text` (drop the scope from the output), and `@template-renderer` (the scope is the
body of the generated renderer class).

```
<!-- @tt{{{ @foreach [...] }}}@ -->
         ┌──────────────────────────────────┐
         │  ...plain text...                 │   ◄── scope of @foreach
         │  ...more plain text...            │       (repeated once per element)
         └──────────────────────────────────┘
<!-- @tt{{{ @end-foreach }}}@ -->
```

### Closing a command

A scope-opening command is closed in one of two ways:

1. **Explicitly**, with its `@end-…` command (e.g. `@end-foreach`, `@end-if`,
   `@end-template-renderer`). Some have aliases — `@fi` closes `@if`.
2. **By auto-close**, for commands that support it. `@template-renderer`,
   `@replace-value-by-expression`, `@replace-value-by-value`, and `@ignore-text` may have their
   `@end-…` omitted; tavnit closes them automatically. Auto-closing is triggered when
   an enclosing scope closes, or at the end of the file. (`@if` and `@foreach` do **not**
   support auto-close — they must be closed explicitly.)

Some commands are **self-contained** and open no scope at all: 
`@print-text`, `@remark`, `@render-template`, `@add-import-to-renderer`, the `move-comment-*`
commands, and the whitespace `remove-*` commands. They act at their own position and need no
closing command.

### Nesting and how scopes stack

Commands nest like brackets: a scope opened later must be closed before a scope opened earlier.
tavnit tracks open scopes on a stack. When a closing command appears, it must match
the **most recently opened** scope, otherwise parsing fails with a clear error.

```
<!-- @tt{{{ @if [conditionExpression="model.showList"] }}}@ -->     ┐
  <ul><!-- @tt{{{ @foreach [...] }}}@ -->                           │ ┐
    <li><!-- @tt{{{ @replace-value-by-expression [...] }}}@ -->X    │ │ ┐
        </li><!-- @tt{{{ @end-replace-value-by-expression }}}@ -->  │ │ ┘  scope: replace-value-by-expression
  </ul><!-- @tt{{{ @end-foreach }}}@ -->                            │ ┘    scope: foreach
<!-- @tt{{{ @end-if }}}@ -->                                        ┘      scope: if
```

Reading the area of effect:

- The **`@if` scope** spans the whole block — nothing inside it is rendered unless
  `model.showList` is true.
- The **`@foreach` scope** lives *inside* the `@if` scope — it repeats the `<li>` region for
  every element, but only when the surrounding `@if` already decided to render.
- The **`@replace-value-by-expression` scope** is the innermost — its value replacement applies
  only to the `<li>` text it encloses.

Each inner scope is fully contained in its parent. A command's effect reaches exactly the plain
text inside its own scope and the scopes nested within it — never the plain text of a sibling
or an ancestor outside its range.

### Auto-close in action

Because closing commands also **trigger auto-close of any auto-closeable scopes still open
inside them**, you can often omit the inner `@end-…`. For example:

```
<!-- @tt{{{ @foreach [...] @replace-value-by-expression [...] }}}@ -->
  ...content...
<!-- @tt{{{ @end-foreach }}}@ -->     ◄── closes @foreach AND auto-closes the still-open
                                          @replace-value-by-expression inside it
```

Here `@end-foreach` closes the `@foreach` scope and, on the way, auto-closes the
`@replace-value-by-expression` that was opened inside it (because that command supports
auto-closing). Likewise, any auto-closeable scope still open at the **end of the file** is
closed automatically. A scope that does **not** support auto-closing (like `@if` or `@foreach`)
and is left open will instead raise an `UNCLOSED_OPENING_COMMAND` error.

### Directly-nested commands

A few commands must sit **directly inside** a specific parent. `@else` and `@else-if` must be
directly nested in an `@if`. tavnit validates this and rejects a misplaced `@else`.

### Nested template renderers are independent

`@template-renderer` is special: you may nest one `@template-renderer` inside another, but a
nested renderer is **fully independent**. Its content is removed from the parent renderer and
placed in its own class, and none of the parent's models, conditions, loops, or replacements
apply to it. Each renderer class stands on its own.

# 3. Varia

* No external runtime dependencies beyond Kotlin stdlib — pure Kotlin implementation.
* The API [tavnit-api](tavnit-api) and the implementation [tavnit](tavnit) are decoupled via ServiceLoader (see ``META-INF/services/`` in the tavnit module).
* All supported comment styles are defined in a [configuration file](tavnit/src/main/resources/tavnit-config.properties)
  that can be extended/overwritten by providing a resource file ```tavnit-config-overwrite.properties``` in your JVM.
