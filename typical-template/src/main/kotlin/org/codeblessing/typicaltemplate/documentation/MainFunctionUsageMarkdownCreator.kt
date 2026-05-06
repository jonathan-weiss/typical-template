package org.codeblessing.typicaltemplate.documentation

import org.codeblessing.typicaltemplate.MAIN_FUNCTION_USAGE

object MainFunctionUsageMarkdownCreator {

    fun createMarkdownDocumentation(): String {
        val sb = StringBuilder()
        sb.appendLine("""
# Main function usage

Call the main function ```org.codeblessing.typicaltemplate.TypicalTemplateKt``` with the following parameter:

```
$MAIN_FUNCTION_USAGE
```
""".trimIndent())
        return sb.toString()
    }

}
