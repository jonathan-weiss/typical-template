package org.codeblessing.typicaltemplate.contentparsing.linenumbers

import org.codeblessing.typicaltemplate.contentparsing.tokenizer.ContentPartWithMetadata

object LineNumberCalculator {

    fun calculateLineNumbers(
        contentPart: ContentPartWithMetadata,
        allContentParts: List<ContentPartWithMetadata>
    ): LineNumbers {

        var previousContentPartEndLineNumber = 0

        for(currentContentPart in allContentParts) {
            if(currentContentPart != contentPart) {
                previousContentPartEndLineNumber += currentContentPart.fullContent.countLines()
            } else {
                break
            }
        }

        val contentPartStartLineNumber = previousContentPartEndLineNumber + 1
        val contentPartEndLineNumber = (previousContentPartEndLineNumber + contentPart.fullContent.countLines())
            .coerceAtLeast(contentPartStartLineNumber)

        return LineNumbers(
            startLineNumber = contentPartStartLineNumber,
            endLineNumber = contentPartEndLineNumber,
            context = contentPart.fullContent,
            formattedDescription = "Lines ${contentPartStartLineNumber}-${contentPartEndLineNumber}: '${contentPart.fullContent}'",
        )
    }

    private fun String.countLines(): Int {
        // TODO add support for "\r", "\r\n"
        return this.count { it == '\n' }
    }
}
