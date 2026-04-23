package org.codeblessing.typicaltemplate.contentparsing.linenumbers

import org.codeblessing.typicaltemplate.contentparsing.tokenizer.RawContentPart

object LineNumberCalculator {

    fun calculateLineNumbers(
        contentPart: RawContentPart,
        allContentParts: List<RawContentPart>
    ): LineNumbers {

        var previousContentPartEndLineNumber = 0

        for(currentContentPart in allContentParts) {
            if(currentContentPart != contentPart) {
                previousContentPartEndLineNumber += currentContentPart.pristineContent.countLines()
            } else {
                break
            }
        }

        val contentPartStartLineNumber = previousContentPartEndLineNumber + 1
        val contentPartEndLineNumber = (previousContentPartEndLineNumber + contentPart.pristineContent.countLines())
            .coerceAtLeast(contentPartStartLineNumber)

        return LineNumbers(
            startLineNumber = contentPartStartLineNumber,
            endLineNumber = contentPartEndLineNumber,
            context = contentPart.pristineContent,
            formattedDescription = "Lines ${contentPartStartLineNumber}-${contentPartEndLineNumber}: '${contentPart.pristineContent}'",
        )
    }

    private fun String.countLines(): Int {
        // TODO add support for "\r", "\r\n"
        return this.count { it == '\n' }
    }
}
