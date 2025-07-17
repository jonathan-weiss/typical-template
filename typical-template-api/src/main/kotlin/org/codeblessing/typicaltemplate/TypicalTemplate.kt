package org.codeblessing.typicaltemplate

fun main(args: Array<String>) {
    println("Typical Templater: [${args.joinToString(",")}]")

    // TODO add little ability to configure via parameters
    val templatingConfigurations: List<TemplatingConfiguration> = emptyList()
    TypicalTemplateApi.runTypicalTemplate(templatingConfigurations)
}
