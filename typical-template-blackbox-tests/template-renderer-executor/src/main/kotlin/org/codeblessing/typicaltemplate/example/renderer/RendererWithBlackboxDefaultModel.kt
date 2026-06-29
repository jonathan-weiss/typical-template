package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.BlackboxDefaultModel

interface RendererWithBlackboxDefaultModel {

    fun renderTemplate(model: BlackboxDefaultModel): String
    fun filePath(model: BlackboxDefaultModel): String
}
