package my.example.businessproject.dto

/* @tt{{{

  @template-renderer [
      templateRendererClassName="EntityDtoTemplateRenderer"
      templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
  ]
  @template-model [
      modelClassName="DtoEntityRenderModel"
      modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
      modelName="model"
  ]
  @slbc @slac
}}}@ */


/* @tt{{{
  @slbc
  @replace-value-by-expression [ searchValue="ProductDto" replaceByExpression="model.kotlinDtoClassName" ]
  @slac
}}}@ */
data class ProductDto( // @tt{{{ @end-replace-value-by-expression }}}@
/* @tt{{{
  @slbc
  @foreach [ iteratorExpression="model.fields" loopVariable="field" ]
  @replace-value-by-expression [ searchValue="productCode" replaceByExpression="field.fieldName" ] [ searchValue="String" replaceByExpression="field.fieldTypeName" ]
}}}@ */
    val productCode: String,
/* @tt{{{
  @slbc
  @end-replace-value-by-expression
  @end-foreach
  @ignore-text
  @slac
}}}@ */
    val productName: String,
/* @tt{{{
  @slbc
  @end-ignore-text
  @slac
}}}@ */
)
