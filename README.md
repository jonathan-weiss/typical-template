# Tavnit - Reverse Template Engine

## What is tavnit?

tavnit is a **reverse template engine** for Kotlin. Instead of writing a template
first and deriving real output from it, you do it the other way around:

1. You write **real, working source code** — HTML, Kotlin, TypeScript, SCSS, XML, or anything
   else. The file stays valid and editable in its native tooling.
2. You **annotate that source file with tavnit commands**, written *inside ordinary
   source-code comments* (`<!-- ... -->`, `/* ... */`, `// ...`, depending on the language).
3. You **run tavnit**. It reads those annotated files and **generates a Kotlin
   renderer class** for each one — a class whose `renderTemplate(...)` function reproduces the
   file's content as a multiline string, with your dynamic parts (loops, conditions, value
   replacements) woven in.

### Why this approach?

The painful part of classic templating is keeping two things in sync by hand: the real
component (which you keep editing, restyling, refactoring) and the template that is supposed
to reproduce it. Every change to the source forces a manual change to the template.

tavnit removes that manual step. When your real source file changes, you simply
**re-run tavnit** and the renderer class is regenerated. The source file is the
single source of truth; the renderer is always derived from it.

```
   edit real source file  ─────────────►  re-run tavnit  ─────────────►  renderer is up to date
   (HTML, Kotlin, SCSS…)                   (no manual editing of                    automatically
                                            the renderer needed)
```

### Two important boundaries

- **Any file format that supports comments can be a source file.** tavnit does not
  care about the language — it only needs a comment syntax it can recognize so it can find the
  `@tt{{{ ... }}}@` command blocks inside the source code comment, e.g. 
  `<!-- @tt{{{ ... }}}@ -->` in HTML. (see [SUPPORTED-FILE-FORMATS.md](SUPPORTED-FILE-FORMATS.md)).

- **tavnit only generates renderers — nothing more.** It is a tool whose single job
  is to turn an annotated source file into a Kotlin renderer class. It does **not** write
  output files for you, it does **not** create your model classes, and it does **not** run the
  renderers. Building the model, calling the renderer, and writing the produced string to disk
  are all *your* responsibility in *your* application. Keeping that boundary sharp is what keeps
  tavnit small and predictable.

---

## Syntax

Write block comments (e.g. `/* ... */`, `<!-- ... -->`) or line comments (e.g. `// ...`) in your source file.
Of course, the comment style depends on the language of the source file.
All comments containing the magic brackets ```@tt{{{``` ... ```}}}@``` will be considered as syntax
for tavnit.
Inside the magic brackets, write one or many tavnit commands. For a list of all command, look at the 
[COMMAND-REFERENCE.md](COMMAND-REFERENCE.md).

## Example

Here is an example HTML source code file (the file is named ```news.html```) enriched with tavnit commands:

```html
<html lang="en">

<!-- 

@tt{{{
  @move-comment-backward
  
  @template-renderer 
     [ templateRendererClassName="HtmlListPageRenderer" templateRendererPackageName="org.codeblessing.tavnit.example.renderer" ]
     [ modelName="listPageModel" modelClassName="HtmlListModel" modelPackageName="org.codeblessing.tavnit.example.renderer.model" ]
  
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
Based on that given HTML input, tavnit will generate a kotlin renderer like this:
```kotlin
/*
 * This file is generated using tavnit.
 */
package org.codeblessing.tavnit.example.renderer

import org.codeblessing.tavnit.example.renderer.model.HtmlListModel

/**
 * Generate the content for the template `HtmlListPageRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `news.html`
 * - path: `documentation/news.html`
 */
object HtmlListPageRenderer {

    fun renderTemplate(listPageModel: HtmlListModel): String {
        return """
          |<html lang="en">
          |
          |
          |<head><title>${listPageModel.pageTitle}</title></head>
          |<body>
          |<p>Here are the ${listPageModel.pageTitle.lowercase()}:</p>
          |<ul>${ listPageModel.allListEntries.joinToString("") { pageArticleTitle ->  """
              |    <li>${pageArticleTitle}</li>""" } }
          |</ul>
          |
          |</body>
          |</html>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(listPageModel: HtmlListModel): String {
        return "documentation/news.html"
    }
}
```

Notice how the tavnit commands in the HTML have been transferred into the template:

- Every `@tt{{{ ... }}}@` comment is gone, and the whitespace around it has been cleaned up.
- The `@replace-value-by-expression` scope turned the literal `News`/`news` text into model
  expressions.
- The `@foreach` scope became a `joinToString` loop, repeating the single `<li>` line.
- The `@ignore-text` scope removed the two extra sample `<li>` items, which existed only so the
  raw `news.html` looked complete in a browser.

You then use `HtmlListPageRenderer.renderTemplate(model)` in your own code to produce HTML.
If your base source file change, you re-run tavnit and the kotlin template renderer class will be updated/rewritten.

## Setup

To let tavnit generate the renderer classes, include the dependencies in your build (shown here for [Gradle](https://gradle.org/), 
but similar in [Maven](https://maven.apache.org/)):
```kotlin
// ...

repositories {
    mavenCentral()
}

dependencies {
    implementation(project("org.codeblessing.tavnit:tavnit-api:0.0.19"))
    runtimeOnly(project("org.codeblessing.tavnit:tavnit:0.0.19"))
}
// ...
```

> Tavnit includes no additional external runtime dependencies beyond Kotlin stdlib — pure Kotlin implementation.
> The API [tavnit-api](tavnit-api) and the implementation [tavnit](tavnit) are decoupled.

Then, call the tavnit main method ```org.codeblessing.tavnit.TavnitKt``` (see [MAIN-FUNCTION-USAGE.md](MAIN-FUNCTION-USAGE.md)) or
call tavnit directly with a code snippet like the following in your kotlin code:
```kotlin

import org.codeblessing.tavnit.FileSearchLocation
import org.codeblessing.tavnit.TemplateRendererConfiguration
import org.codeblessing.tavnit.TemplatingConfiguration
import org.codeblessing.tavnit.TavnitApi

// ...

fun executeTavnitAndCreateRenderers() {
    val config = TemplatingConfiguration(
        // a list, where to search for your real source code like Kotlin files or HTML files 
        // that are enriched with tavnit commands
        fileSearchLocations = listOf(
            FileSearchLocation(
                rootDirectoryToSearch = "/Users/thatsme/myproject/src/main/kotlin",
                filenameMatchingPattern = Regex(".*\\.kt"),
            ),
            FileSearchLocation(
                rootDirectoryToSearch = "/Users/thatsme/myproject/src/webapp",
                filenameMatchingPattern = Regex(".*\\.html"),
            ),
        ),
        // the base directory, where the Kotlin renderers should be generated
        templateRendererConfiguration = TemplateRendererConfiguration(
            templateRendererTargetSourceBasePath = "/Users/thatsme/myproject/src/generated/kotlin",
        ),
    )
    TavnitApi.runTavnit(listOf(config))    
}

```
When the function ``executeTavnitAndCreateRenderers`` is called, tavnit will search for templates and create appropriate template renderers.

## License

The source code is licensed under the MIT license, which you can find in
the [LICENSE](LICENSE) file.

## Where to go next

- The complete list of commands, their attributes, and their closing/auto-close behavior:
  [COMMAND-REFERENCE.md](COMMAND-REFERENCE.md)
- Advanced topics like nesting, scopes and autoclosing : [ADVANCED-TOPICS.md](ADVANCED-TOPICS.md)
- The exact whitespace rules and override commands: [WHITESPACE-HANDLING.md](WHITESPACE-HANDLING.md)
- Running tavnit from the command line: [MAIN-FUNCTION-USAGE.md](MAIN-FUNCTION-USAGE.md)
- All supported file formats and its comment formats: [SUPPORTED-FILE-FORMATS.md](SUPPORTED-FILE-FORMATS.md)
- A full, runnable example project: the Gradle subproject
  [tavnit-blackbox-tests](tavnit-blackbox-tests)

