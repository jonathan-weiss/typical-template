package my.example.businessproject.dto

/* @tt{{{

  @@tt-template-renderer [
      templateRendererClassName="EntityDtoTemplateRenderer"
      templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
  ]
  @@tt-template-model [
      modelClassName="DtoEntityRenderModel"
      modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
      modelName="model"
  ]
  @@tt-slbc @@tt-slac
}}}@ */


/* @tt{{{
  @@tt-slbc
  @@tt-replace-value-by-expression [ searchValue="ProductDto" replaceByExpression="model.kotlinDtoClassName" ]
  @@tt-slac
}}}@ */
data class ProductDto( // @tt{{{ @@tt-end-replace-value-by-expression }}}@
/* @tt{{{
  @@tt-slbc
  @@tt-foreach [ iteratorExpression="model.fields" loopVariable="field" ]
  @@tt-replace-value-by-expression [ searchValue="productCode" replaceByExpression="field.fieldName" ] [ searchValue="String" replaceByExpression="field.fieldTypeName" ]
}}}@ */
    val productCode: String,
/* @tt{{{
  @@tt-slbc
  @@tt-end-replace-value-by-expression
  @@tt-end-foreach
  @@tt-ignore-text
  @@tt-slac
}}}@ */
    val productName: String,
/* @tt{{{
  @@tt-slbc
  @@tt-end-ignore-text
  @@tt-slac
}}}@ */
)
