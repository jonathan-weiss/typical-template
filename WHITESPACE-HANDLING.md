# Whitespace Handling

typical-template automatically removes whitespace around every template comment
(the thing like `<-- @tt{{{ ... }}}@ -->`) so the generated output stays clean. This document explains
the default behavior, the *why* behind it, and how to override the default behavior it with the
`no-default-whitespace-remove` command and the `remove-*` commands.

> Legend for the visualizations:
> 
> - `·` = a single space/tab
> 
> - `⏎` = a line break,
> - `<-- @tt{{{ ... }}}@ -->` = the template comment.
> -  Parts marked with `╳` are removed, the rest is kept. The template comment itself always disappears. 


Note that the whitespace removal only applies to typical-template-comments 
containing the ``@tt{{{ ... }}}@`` — plain comments 
without typical-template instructions are left untouched.

## What is looked at

The comment itself (```<-- @tt{{{ ... }}}@ -->```) is always removed in the output.
The decision considers **only** the text *directly before* the comment (up to
the start of its line) and *directly after* the comment (up to the end of its
line):

```
... preceding text⏎·····<-- @tt{{{ ... }}}@ -->····⏎ following text ...
                   └─┬─┘└──────comment────────┘└─┬─┘
              before the comment           after the comment
```

## Default behaviour


There are four cases, depending on whether the text before and after the comment
(on the same line) is only whitespace (spaces and tabs) or also contains real text:

### Case 1 — text before, only whitespace after (trailing comment)

Everything before the comment is kept; the blanks after it are removed, but the
line break is kept.

```
END</li>····<-- @tt{{{ ... }}}@ -->····⏎     -->   END</li>····⏎
            ^^^^^^^^^^^^^^^^^^^^^^^╳╳╳╳
```

### Case 2 — only whitespace before, text after (leading comment)

Nothing is removed — the indentation and the following text are preserved.

```
····<-- @tt{{{ ... }}}@ -->START</li>     -->   ····START</li>
    ^^^^^^^^^^^^^^^^^^^^^^^
```

### Case 3 — only whitespace before *and* after (comment alone on its line)

The whole comment line is removed: the blanks before it are removed (the
preceding line break is kept), and the blanks together with the following line
break are removed as well.

```
prevText⏎····<-- @tt{{{ ... }}}@ -->····⏎nextText     -->   prevText⏎nextText
         ╳╳╳╳^^^^^^^^^^^^^^^^^^^^^^^╳╳╳╳╳
```

### Case 4 — text before *and* after

Nothing is removed (apart from the comment itself).

```
A····<-- @tt{{{ ... }}}@ -->····B     -->   A····B
     ^^^^^^^^^^^^^^^^^^^^^^^
```

## Why this behaviour?

- **So you don't constantly end up with doubled blank lines or wrong
  indentation.** A comment standing alone on its line (case 3) would otherwise
  leave an empty, indented line behind in the output.
- **Because comments in the source file usually have to be indented** — whether
  for readability or because of the linter/editor. That indentation belongs to
  the source, not the output.
- **When there is only whitespace before/after the comment, probably none of it
  needs to be kept** (it only served to position the comment). If there is real
  text next to it instead (cases 1, 2, 4), the indentation is likely
  intentional and is preserved.
- **Only a single comment is ever considered on its own**, never in combination
  with neighbouring comments — this keeps the behaviour local and predictable.

## Overriding with the `remove-*` commands

When the default behaviour isn't enough, it can be overridden per side. There
are two sets of commands — one for the side *before* and one for the side
*after* the comment — each in two strengths:

- remove only the **blanks**, or
- remove the **blanks and the line break**.

```
preceding text⏎····<-- @tt{{{ ... }}}@ -->····⏎following text
               ├──┤                       ├──┤
               │  └ remove-blanks-after-comment ────────► ····  removed
               │    remove-blanks-and-linebreak-after ──► ····⏎ removed
               │
               └ remove-blanks-before-comment ──────────► ····  removed
                 remove-blanks-and-linebreak-before ────► ⏎···· removed
```

Rules:

- A `remove-*` command overrides the default decision **for its own side
  only** and leaves the other side untouched.
- `no-default-whitespace-remove` switches the default behaviour off for **both**
  sides; then only the comment itself disappears. Explicit `remove-*` commands
  still take effect (and override the now-disabled default for their side).
- A command that asks for exactly what the default would already do is a no-op.
  Conflicting commands on the same side are rejected by the validator.

The individual commands including their aliases are documented in
[COMMAND-REFERENCE.md](./COMMAND-REFERENCE.md) (sections
`remove-blanks-*-comment`, `remove-blanks-and-linebreak-*-comment`, and
`no-default-whitespace-remove`).
