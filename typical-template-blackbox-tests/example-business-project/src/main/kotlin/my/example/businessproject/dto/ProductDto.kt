package my.example.businessproject.dto

/* @tt{{{
  #move-comment [direction="backward"]
  @template-renderer [
      templateRendererClassName="EntityDtoTemplateRenderer"
      templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
  ][
      modelClassName="DtoEntityRenderModel"
      modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
      modelName="model"
  ]
  @replace-value-by-expression
    [ searchValue="ProductDto" replaceByExpression="model.kotlinDtoClassName" ]
    [ searchValue="Product" replaceByExpression="model.entityName" ]
    [ searchValue="productCode" replaceByExpression="field.fieldName" ]
    [ searchValue="String" replaceByExpression="field.fieldTypeNameWithNullability" ]
  }}}@ */

/**
 * The Product DTO (Data Transfer Object) class.
 */
data class ProductDto(
/* @tt{{{
    #expand-comment [ expandDirection="backward" strip="linebreak" ]
    @foreach [ iteratorExpression="model.fields" loopVariable="field" ]
}}}@  */
    val productCode: String,
/* @tt{{{
    #expand-comment [ expandDirection="backward" strip="linebreak" ]
    @end-foreach
    @ignore-text
}}}@ */
    val productName: String,
/* @tt{{{
    #expand-comment [ expandDirection="backward" strip="linebreak" ]
    @end-ignore-text
    @end-replace-value-by-expression
}}}@ */
)
