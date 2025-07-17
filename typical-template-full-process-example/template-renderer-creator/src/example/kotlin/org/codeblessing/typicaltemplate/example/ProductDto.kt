/*
@@tt-template [
    templateClassName="EntityDtoTemplateRenderer"
    templateClassPackageName="examples"
    templateModelClassName="DtoEntityRenderModel"
    templateModelClassPackageName="examples.model"
]
*/

package org.codeblessing.typicaltemplate.example

/* @@tt-replace-value-by-field [ searchValue="ProductDto" replaceByFieldName="kotlinDtoClassName" ] */
data class ProductDto( // @@tt-end-replace-value-by-field
/* @@tt-replace-value-by-field [ searchValue="product" replaceByFieldName="entityPrimaryField" ] */
    val productCode: String,// @@tt-end-replace-value-by-field
    val productName: String,
)
