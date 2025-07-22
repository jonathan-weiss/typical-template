package my.example.businessproject.dto

/* @tt{{{ @slbc

  @template-renderer [
      templateRendererClassName="EntityDtoTemplateRenderer"
      templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
  ]
  @template-model [
      modelClassName="DtoEntityRenderModel"
      modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
      modelName="model"
  ]
  @replace-value-by-expression
    [ searchValue="ProductDto" replaceByExpression="model.kotlinDtoClassName" ]
    [ searchValue="Product" replaceByExpression="model.entityName" ]
    [ searchValue="productCode" replaceByExpression="field.fieldName" ]
    [ searchValue="String" replaceByExpression="field.fieldTypeNameWithNullability" ]
  @slac }}}@ */

/**
 * The Product DTO (Data Transfer Object) class.
 */
data class ProductDto(
/* @tt{{{   @foreach [ iteratorExpression="model.fields" loopVariable="field" ] }}}@  */
    val productCode: String,
/* @tt{{{ @slbc  @end-foreach  @ignore-text  @slac }}}@ */
    val productName: String,
/* @tt{{{ @slbc  @end-ignore-text  @end-replace-value-by-expression }}}@ */
)
