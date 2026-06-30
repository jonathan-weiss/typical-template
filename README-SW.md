# typical-template — Software Documentation

This document explains how **typical-template** works conceptually: what it does, how it
treats your source files internally, and how its commands nest, scope, and finally
disappear from the generated output.

It is the high-level companion to:

- [README.md](README.md) — quick start and build integration
- [COMMAND-REFERENCE.md](COMMAND-REFERENCE.md) — the exhaustive, auto-generated list of all commands
- [WHITESPACE-HANDLING.md](WHITESPACE-HANDLING.md) — the detailed rules for removing whitespace around comments
- [MAIN-FUNCTION-USAGE.md](MAIN-FUNCTION-USAGE.md) — calling typical-template from the command line

---

## 1. What is typical-template?

typical-template is a **reverse template engine** for Kotlin. Instead of writing a template
first and deriving real output from it, you do it the other way around:

1. You write **real, working source code** — HTML, Kotlin, TypeScript, SCSS, XML, or anything
   else. The file stays valid and editable in its native tooling.
2. You **annotate that source file with typical-template commands**, written *inside ordinary
   source-code comments* (`<!-- ... -->`, `/* ... */`, `// ...`, depending on the language).
3. You **run typical-template**. It reads those annotated files and **generates a Kotlin
   renderer class** for each one — a class whose `renderTemplate(...)` function reproduces the
   file's content as a multiline string, with your dynamic parts (loops, conditions, value
   replacements) woven in.

### Why this approach?

The painful part of classic templating is keeping two things in sync by hand: the real
component (which you keep editing, restyling, refactoring) and the template that is supposed
to reproduce it. Every change to the source forces a manual change to the template.

typical-template removes that manual step. When your real source file changes, you simply
**re-run typical-template** and the renderer class is regenerated. The source file is the
single source of truth; the renderer is always derived from it.

```
   edit real source file  ─────────────►  re-run typical-template  ─────────────►  renderer is up to date
   (HTML, Kotlin, SCSS…)                   (no manual editing of                    automatically
                                            the renderer needed)
```

### Two important boundaries

- **Any file format that supports comments can be a source file.** typical-template does not
  care about the language — it only needs a comment syntax it can recognize so it can find the
  `@tt{{{ ... }}}@` command blocks. Supported comment styles are defined in a
  [configuration file](typical-template/src/main/resources/typical-template-config.properties)
  and can be extended.

- **typical-template only generates renderers — nothing more.** It is a tool whose single job
  is to turn an annotated source file into a Kotlin renderer class. It does **not** write
  output files for you, it does **not** create your model classes, and it does **not** run the
  renderers. Building the model, calling the renderer, and writing the produced string to disk
  are all *your* responsibility in *your* application. Keeping that boundary sharp is what keeps
  typical-template small and predictable.

---

## 2. How a file is decomposed internally

To understand how commands behave, it helps to see how typical-template *reads* a file.

Internally, a source file is **tokenized into a flat chain of two kinds of parts**:

- **Plain text** — everything that is *not* a typical-template command block. Crucially, this
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

typical-template tokenizes it into an alternating chain of plain-text and template-comment
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

A file that contains **no** template comments at all is ignored entirely — typical-template
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

## 3. Nesting, scope, and closing commands

Most commands do not act on a single point in the file — they act on a **region** of the chain.
That region is the command's **scope** (its area of effect): everything from the command's
opening up to its matching close.

### Scope = "the plain text I influence"

Consider `@foreach`. It does not just mark a spot; it says *"repeat everything between here and
my close for each element of the collection"*. The plain text inside that region is its scope.
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
   `@end-…` omitted; typical-template closes them automatically. Auto-closing is triggered when
   an enclosing scope closes, or at the end of the file. (`@if` and `@foreach` do **not**
   support auto-close — they must be closed explicitly.)

Some commands are **self-contained** and open no scope at all: `@else`, `@else-if`,
`@print-text`, `@remark`, `@render-template`, `@add-import-to-renderer`, the move-comment
commands, and the whitespace `remove-*` commands. They act at their own position and need no
closing command.

### Nesting and how scopes stack

Commands nest like brackets: a scope opened later must be closed before a scope opened earlier.
typical-template tracks open scopes on a stack. When a closing command appears, it must match
the **most recently opened** scope, otherwise parsing fails with a clear error
(`MISMATCHED_CLOSING_COMMAND`).

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
directly nested in an `@if`. typical-template validates this and rejects a misplaced `@else`.

### Nested template renderers are independent

`@template-renderer` is special: you may nest one `@template-renderer` inside another, but a
nested renderer is **fully independent**. Its content is removed from the parent renderer and
placed in its own class, and none of the parent's models, conditions, loops, or replacements
apply to it. Each renderer class stands on its own.

---

## 4. How template comments disappear from the output

Template comments are instructions, not content — so the generated renderer must never print
them. typical-template guarantees this in two steps.

### Step 1 — the comment itself is always removed

The full comment carrier, including the host language's comment delimiters and the magic
brackets, is always dropped:

```
<-- @tt{{{ @foreach [...] }}}@ -->          ◄── the entire comment is removed from the output
```

What the comment *does* (open a scope, replace a value, …) is applied to the surrounding plain
text; what the comment *looks like* never appears.

> Note: this only applies to comments that actually contain `@tt{{{ ... }}}@`. A plain comment
> with no typical-template instructions is just plain text and is reproduced verbatim in the
> output.

### Step 2 — the whitespace around the comment is cleaned up

If only the comment text vanished but its surrounding indentation and line break stayed, the
output would be littered with blank, indented lines and stray spaces. So typical-template also
trims the whitespace **directly around** each comment, looking only at the text on the
comment's own line — what comes before it (up to the start of the line) and after it (up to the
end of the line).

The default behavior distinguishes four cases. Using the legend `·` = space/tab, `⏎` = line
break, `╳` = removed:

```
Case 1 — text before, only whitespace after (trailing comment):
  END</li>····<-- @tt{{{ … }}}@ -->····⏎   →   END</li>⏎
          ╳╳╳╳                     ╳╳╳╳         (blanks before & after gone; line break kept)

Case 2 — only whitespace before, text after (leading comment):
  ····<-- @tt{{{ … }}}@ -->START</li>      →   ····START</li>
                                               (nothing removed but the comment — indent kept)

Case 3 — whitespace on both sides (comment alone on its line):
  prev⏎····<-- @tt{{{ … }}}@ -->····⏎next  →   prev⏎next
       ╳╳╳╳                     ╳╳╳╳╳            (the whole comment line collapses)

Case 4 — text on both sides:
  A····<-- @tt{{{ … }}}@ -->····B          →   A····B
                                               (nothing removed but the comment)
```

This default is deliberately conservative: where the neighboring text is real content (cases 2
and 4), the indentation is assumed intentional and preserved; where the comment only sat on its
own padded line (case 3), the whole line collapses so you do not get an empty indented line in
the output.

When the default is not what you want, you can override it **per side** with the
`@remove-blanks-*` / `@remove-blanks-and-linebreak-*` commands, or switch the default off
entirely for one comment with `@no-default-whitespace-remove`. The full rules, including how the
before-side and after-side are decided independently, are documented in
[WHITESPACE-HANDLING.md](WHITESPACE-HANDLING.md).

---

## 5. End-to-end example

The following annotated `news.html` source file pulls the pieces together — a renderer
definition, a value replacement spanning most of the file, a `@foreach` loop, and an
`@ignore-text` block that hides sample list items that exist only to make the source render
nicely in a browser:

```html
<html lang="en">

<!--

@tt{{{
  @move-comment-backward

  @template-renderer
     [ templateRendererClassName="HtmlListPageRenderer" templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer" ]
     [ modelName="listPageModel" modelClassName="HtmlListModel" modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model" ]

  @replace-value-by-expression
    [ searchValue="News" replaceByExpression="listPageModel.pageTitle" ]
    [ searchValue="news" replaceByExpression="listPageModel.pageTitle.lowercase()" ]

}}}@
-->

<head><title>News</title></head>
<body>
<p>Here are the news:</p>
<ul><!--
@tt{{{

  @foreach [iteratorExpression="listPageModel.allListEntries" loopVariable="pageArticleTitle"]
  @replace-value-by-expression [ searchValue="How to make your garden ready in the spring" replaceByExpression="pageArticleTitle" ]

}}}@
-->
    <li>How to make your garden ready in the spring</li><!-- @tt{{{ @end-replace-value-by-expression @end-foreach @ignore-text }}}@ -->
    <li>Five keys to become rich in one year</li>
    <li>What's up with Prince Charles?</li><!-- @tt{{{ @end-ignore-text }}}@ -->
</ul>

</body>
<!-- @tt{{{ @end-replace-value-by-expression }}}@ -->
</html>
```

Running typical-template on this file produces a Kotlin renderer:

```kotlin
/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.HtmlListModel

/**
 * Generate the content for the template HtmlListPageRenderer filled up
 * with the content of the passed models.
 */
object HtmlListPageRenderer {

    fun renderTemplate(listPageModel: HtmlListModel): String {
        return """
          |<html lang="en">
          |
          |
          |
          |<head><title>${listPageModel.pageTitle}</title></head>
          |<body>
          |<p>Here are the ${listPageModel.pageTitle.lowercase()}:</p>
          |<ul>${ listPageModel.allListEntries.joinToString("") { pageArticleTitle ->  """
              |    <li>${pageArticleTitle}</li>
          """ } }
          |</ul>
          |
          |</body>
          |
          |</html>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(listPageModel: HtmlListModel): String {
        return "news.html"
    }
}
```

Notice how the picture from the previous sections plays out:

- Every `@tt{{{ ... }}}@` comment is gone, and the whitespace around it has been cleaned up.
- The `@replace-value-by-expression` scope turned the literal `News`/`news` text into model
  expressions.
- The `@foreach` scope became a `joinToString` loop, repeating the single `<li>` line.
- The `@ignore-text` scope removed the two extra sample `<li>` items, which existed only so the
  raw `news.html` looked complete in a browser.

You then use `HtmlListPageRenderer.renderTemplate(model)` in your own code to produce HTML.
Creating the `HtmlListModel`, calling the renderer, and writing the result to a file are all up
to your application — typical-template's job ends at generating the renderer class.

---

## 6. Where to go next

- The complete list of commands, their attributes, and their closing/auto-close behavior:
  [COMMAND-REFERENCE.md](COMMAND-REFERENCE.md)
- The exact whitespace rules and override commands: [WHITESPACE-HANDLING.md](WHITESPACE-HANDLING.md)
- Build setup and the quick-start example: [README.md](README.md)
- Running typical-template from the command line: [MAIN-FUNCTION-USAGE.md](MAIN-FUNCTION-USAGE.md)
- A full, runnable example project: the Gradle subproject
  [typical-template-blackbox-tests](typical-template-blackbox-tests)
```
