package org.codeblessing.typicaltemplate.contentparsing.commandchain

data class ModelDescription(
    val modelClassDescription: ClassDescription,
    val modelName: String,
    val isList: Boolean,
)
