# typical-template - reverse template engine

Typical template is a reverse template engine to create template renderer for kotlin. 
Your write your real source code (HTML, Java, kotlin, CSS, etc.). 
Then you add (with source code comments) typical template commands to your source code. 
With help of these commands, typical-template can create kotlin multi-line templates for you.

The advantage of this approach is, that when you extend your real source code components and classes, 
the synchronization of the template is done by only run again typical-template. 
You do not have to keep your templates and your source code in sync, manually.

## Example

Here is an example of a real source code (here it is HTML) enriched with typical-template commands:

```html
<html lang="en">

<!-- @@tt{{

  @@tt-template-renderer [ templateRendererClassName="HtmlListPageRenderer" templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer" ]
  @@tt-template-model [ modelName="listPageModel" modelClassName="HtmlListModel" modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model" ]
  @@tt-replace-value-by-expression
    [ searchValue="News" replaceByExpression="listPageModel.pageTitle" ]
    [ searchValue="news" replaceByExpression="listPageModel.pageTitle.lowercase()" ]

}}tt@@ -->

<head><title>News</title></head>
<body>
<p>Here are the news:</p>
<ul><!-- @@tt{{

  @@tt-foreach [iteratorExpression="listPageModel.allListEntries" loopVariable="pageArticleTitle"]
  @@tt-replace-value-by-expression [ searchValue="How to make your garden ready in the spring" replaceByExpression="pageArticleTitle" ]

}}tt@@ -->
    <li>How to make your garden ready in the spring</li><!-- @@tt{{ @@tt-end-replace-value-by-expression @@tt-end-foreach @@tt-ignore-text }}tt@@ -->
    <li>Five keys to become rich in one year</li>
    <li>What's up with Prince Charles?</li><!-- @@tt{{ @@tt-end-ignore-text }}tt@@ -->
</ul>

</body>
<!-- @@tt{{ @@tt-end-replace-value-by-expression }}tt@@ -->
</html>

```
Based on that given HTML input, typical-template will generate a kotlin renderer like this:
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
}
```

## Syntax

Write block comments (e.g. `/* ... */`, `<!-- ... -->`) or line comments (e.g. `// ...`) in your source file.
Of course, the comment style depends on the language of the source file.
All comments containing the magic brackets ```@@tt{{``` ... ```}}tt@@``` will be considered as syntax 
for typical template.
Inside the magic brackets, write one or many typical-template commands (see [COMMAND-REFERENCE.md](COMMAND-REFERENCE.md)).

## Let typical template generate the renderer classes

To let typical template generate the renderer classes, include the dependencies in your build (shown here for [Gradle](https://gradle.org/), 
but similar in [Maven](https://maven.apache.org/)):
```kotlin
// ...

repositories {
    mavenCentral()
}

dependencies {
    implementation(project("org.codeblessing.typical-template:typical-template-api:1.0.0"))
    runtimeOnly(project("org.codeblessing.typical-template:typical-template:1.0.0"))
}
// ...
```

Then, call typical-template with a code snippet like the following in your kotlin or java code:
```kotlin

import org.codeblessing.typicaltemplate.FileSearchLocation
import org.codeblessing.typicaltemplate.TemplateRendererConfiguration
import org.codeblessing.typicaltemplate.TemplatingConfiguration
import org.codeblessing.typicaltemplate.TypicalTemplateApi

// ...

fun executeTypicalTemplateAndCreateRenderers() {
    val config = TemplatingConfiguration(
        // a list, where to search for your real source code like Kotlin files or HTML files 
        // that are enriched with typical template commands
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
    TypicalTemplateApi.runTypicalTemplate(listOf(config))    
}

```
Typical template will search for templates and create renderers if the function ``executeTypicalTemplateAndCreateRenderers`` is called.

For a full example, see the Gradle subproject [typical-template-full-process-example](typical-template-full-process-example).
