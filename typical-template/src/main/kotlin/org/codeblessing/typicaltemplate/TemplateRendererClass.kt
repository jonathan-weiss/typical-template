package org.codeblessing.typicaltemplate

import org.codeblessing.typicaltemplate.contentparsing.commandchain.TemplateRendererDescription
import java.nio.file.Path

data class TemplateRendererClass(
    val templateRendererDescription: TemplateRendererDescription,
    val templateRendererClassContent: String,
    val templateRendererClassFilePath: Path
)
