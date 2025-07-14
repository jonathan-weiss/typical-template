package org.codeblessing.typicaltemplate

fun main(args: Array<String>) {
    println("Typical Templater: [${args.joinToString(",")}]")

    TypicalTemplateProcessor.processTypicalTemplate()
}
