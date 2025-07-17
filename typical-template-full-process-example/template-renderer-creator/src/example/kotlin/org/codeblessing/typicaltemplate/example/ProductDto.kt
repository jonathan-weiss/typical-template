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

/* @@tt-replace-value-by-expression [ searchValue="ProductDto" replaceByExpression="model.kotlinDtoClassName" ] */
data class ProductDto( // @@tt-end-replace-value-by-expression
/* @@tt-replace-value-by-expression [ searchValue="product" replaceByExpression="model.entityPrimaryField" ] */
    val productCode: String,// @@tt-end-replace-value-by-expression
    val productName: String,
)
