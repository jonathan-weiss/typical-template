package my.example.businessproject.domain

/* @tt{{{
  #move-comment [direction="backward"]
  @template-renderer [
      templateRendererClassName="StatusEnumRenderer"
      templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
  ][
      modelClassName="StatusEnumRenderModel"
      modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
      modelName="model"
  ]
  @replace-value-by-expression
    [ searchValue="OrderStatus" replaceByExpression="model.enumName" ]
  @modify-provided-filename-by-replacements
}}}@ */

enum class OrderStatus {
/* @tt{{{   @foreach [ iteratorExpression="model.statusValues" loopVariable="statusValue" ]
             @replace-value-by-expression [ searchValue="PENDING" replaceByExpression="statusValue" ] }}}@ */
    PENDING,
/* @tt{{{ @end-foreach  @ignore-text  }}}@ */
    COMPLETED,
    CANCELLED,
/* @tt{{{ @end-ignore-text  @end-replace-value-by-expression }}}@ */
}
