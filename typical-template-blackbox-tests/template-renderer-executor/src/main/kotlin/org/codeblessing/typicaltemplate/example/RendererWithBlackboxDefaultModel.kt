package org.codeblessing.typicaltemplate.example

import org.codeblessing.typicaltemplate.example.renderer.model.BlackboxDefaultModel

interface RendererWithBlackboxDefaultModel {

    fun renderTemplate(model: BlackboxDefaultModel): String
}
