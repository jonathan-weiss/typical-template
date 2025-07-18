package my.example.businessproject.dto

/* @@<#
@@tt-template-renderer [
    templateRendererClassName="EntityDtoTemplateRenderer"
    templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
] @@>#
*/
/* @@<#
@@tt-template-model [
    modelClassName="DtoEntityRenderModel"
    modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
    modelName="model"
] @@>#
*/


/* @@<# @@tt-replace-value-by-expression [ searchValue="ProductDto" replaceByExpression="model.kotlinDtoClassName" ] @@># */
data class ProductDto( // @@tt-end-replace-value-by-expression
/* @@<# @@tt-replace-value-by-expression [ searchValue="product" replaceByExpression="model.entityPrimaryField" ] @@># */
    val productCode: String,// @@tt-end-replace-value-by-expression
    val productName: String,
)
