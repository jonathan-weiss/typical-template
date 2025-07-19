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
/* @@<# @@tt-foreach [ iteratorExpression="model.fields" loopVariable="field" ] @@># */
/* @@<# @@tt-replace-value-by-expression [ searchValue="productCode" replaceByExpression="field.fieldName" ] [ searchValue="String" replaceByExpression="field.fieldTypeName" ] @@># */
    val productCode: String/* @@tt-if-condition[conditionExpression="field.isNullable"] *//* @@tt-print-text[text="asdf"] *//* @@tt-end-if-condition */,
/* @@<# @@tt-end-replace-value-by-expression @@># */
/* @@<# @@tt-end-foreach @@># */
/* @@<# @@tt-ignore-text @@># */
    val productName: String,
/* @@<# @@tt-end-ignore-text @@># */
)
