package org.codeblessing.typicaltemplate.template

import org.codeblessing.typicaltemplate.contentparsing.Template
import java.nio.file.Path
import java.nio.file.Paths

fun Template.kotlinTemplateClassname(): String = this.templateClassName
fun Template.kotlinTemplatePackage(): String = this.templateClassPackage

fun Template.kotlinTemplateClassFilename(): String = "${kotlinTemplateClassname()}.kt"

fun Template.kotlinTemplateClassFilePath(basePath: Path): Path {
    val packagePath = Paths.get(kotlinTemplatePackage().replace(".", "/"))
    return basePath.resolve(packagePath).resolve(kotlinTemplateClassFilename())
}

fun Template.kotlinModelClassname(): String = this.modelClassName
fun Template.kotlinModelPackage(): String = this.modelClassPackage

fun Template.kotlinModelFullQualifiedName(): String {
    return if(kotlinModelPackage().isBlank()) this.modelClassName
    else "${kotlinModelPackage()}.${kotlinModelClassname()}"
}
