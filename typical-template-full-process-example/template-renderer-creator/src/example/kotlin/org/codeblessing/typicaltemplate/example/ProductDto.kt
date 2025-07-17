/*
@@tt-template [
    templateClassName="EntityDtoTemplateRenderer"
    templateClassPackageName="examples"
    templateModelClassName="DtoEntityRenderModel"
    templateModelClassPackageName="examples.model"
]
*/

package org.codeblessing.typicaltemplate.example

data class ProductDto(
    val productCode: String,
    val productName: String,
)
