/*
@@tt-template-renderer [
    templateRendererClassName="EntityDtoTemplateRenderer"
    templateRendererPackageName="examples"
]
*/

/*
@@tt-template-model [
    modelClassName="DtoEntityRenderModel"
    modelPackageName="examples.model"
    modelName="model"
]
*/

package org.codeblessing.typicaltemplate.example

/* @@tt-replace-value-by-field [ searchValue="ProductDto" replaceByFieldName="kotlinDtoClassName" ] */
data class ProductDto( // @@tt-end-replace-value-by-field
/* @@tt-replace-value-by-field [ searchValue="product" replaceByFieldName="entityPrimaryField" ] */
    val productCode: String,// @@tt-end-replace-value-by-field
    val productName: String,
)
