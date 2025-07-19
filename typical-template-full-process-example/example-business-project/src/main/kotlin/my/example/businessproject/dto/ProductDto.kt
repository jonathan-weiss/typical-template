package my.example.businessproject.dto

/* @@tt{{

@@tt-template-renderer [
    templateRendererClassName="EntityDtoTemplateRenderer"
    templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
] 
@@tt-template-model [
    modelClassName="DtoEntityRenderModel"
    modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
    modelName="model"
] 

}}tt@@ */


/* @@tt{{
  @@tt-replace-value-by-expression [ searchValue="ProductDto" replaceByExpression="model.kotlinDtoClassName" ]
}}tt@@ */
data class ProductDto( // @@tt{{ @@tt-end-replace-value-by-expression }}tt@@
/* @@tt{{

  @@tt-foreach [ iteratorExpression="model.fields" loopVariable="field" ]
  @@tt-replace-value-by-expression [ searchValue="productCode" replaceByExpression="field.fieldName" ] [ searchValue="String" replaceByExpression="field.fieldTypeName" ]

}}tt@@ */
    val productCode: String,
/* @@tt{{

  @@tt-end-replace-value-by-expression
  @@tt-end-foreach
  @@tt-ignore-text

}}tt@@ */
    val productName: String,
/* @@tt{{
  @@tt-end-ignore-text
}}tt@@ */
)
