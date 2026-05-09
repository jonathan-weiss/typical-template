# typical-template - reverse template engine

Typical template is a reverse template engine to create template renderer kotlin classes.
The procedure is as follows:
* Your write your real source code (HTML, Java, kotlin, CSS, etc.).
* Then you add (wrapped into source code comments) typical template commands to your source code.
* With help of these commands, typical-template can create kotlin multi-line templates for you.

The advantage of this approach is, that when you extend your real source code components and classes, 
the synchronization of the template is done by only run again typical-template. 
You do not have to keep your templates and your source code in sync manually.

## Example

Here is an example source code file (here it is HTML, the file is named ```news.html```) enriched with typical-template commands:

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

    fun filePath(listPageModel: HtmlListModel): String {
        return "news.html"
    }
}
```
You can use this class to generate dynamic HTML files.
If your base source file change, you re-run typical template and the kotlin template renderer class will be updated/rewritten.

## Syntax

Write block comments (e.g. `/* ... */`, `<!-- ... -->`) or line comments (e.g. `// ...`) in your source file.
Of course, the comment style depends on the language of the source file.
All comments containing the magic brackets ```@tt{{{``` ... ```}}}@``` will be considered as syntax 
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
    implementation(project("org.codeblessing.typical-template:typical-template-api:0.0.8"))
    runtimeOnly(project("org.codeblessing.typical-template:typical-template:0.0.8"))
}
// ...
```

Then, call the typical-template main method ```org.codeblessing.typicaltemplate.TypicalTemplateKt``` (see [MAIN-FUNCTION-USAGE.md](MAIN-FUNCTION-USAGE.md)) or
call typical-template directly with a code snippet like the following in your kotlin or java code:
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
When the function ``executeTypicalTemplateAndCreateRenderers`` is called, typical template will search for templates and create appropriate template renderers.

## Varia

* No external runtime dependencies beyond Kotlin stdlib — pure Kotlin implementation.
* The API [typical-template-api](typical-template-api) and the implementation [typical-template](typical-template) are decoupled via ServiceLoader (see ``META-INF/services/`` in the typical-template module).
* All supported comment styles are defined in a [configuration file](typical-template/src/main/resources/typical-template-config.properties)
  that can be extended/overwritten by providing a resource file ```typical-template-config-overwrite.properties``` in your JVM.
* For a full example, see the Gradle subproject [typical-template-blackbox-tests](typical-template-blackbox-tests).

## License

The source code is licensed under the MIT license, which you can find in
the [LICENSE](LICENSE) file.
