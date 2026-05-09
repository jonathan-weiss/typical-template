package my.example.businessproject.summary

/* @tt{{{
  @move-comment-backward
  @template-renderer [
      templateRendererClassName="SummaryClassRenderer"
      templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
  ][
      modelClassName="SummaryRenderModel"
      modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
      modelName="model"
  ]
  @replace-value-by-expression
    [ searchValue="OrderSummary" replaceByExpression="model.summaryClassName" ]
  @replace-value-by-value
    [ searchValue="SUPPRESS_VALUE" replaceByValue="unused" ]
  @modify-provided-filename-by-replacements
}}}@ */
/* @tt{{{
  @template-renderer [
      templateRendererClassName="SummaryExtensionRenderer"
      templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
  ][
      modelClassName="SummaryRenderModel"
      modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
      modelName="model"
  ]
  @print-text [ text="package my.example.businessproject.summary" ]
  @replace-value-by-expression
    [ searchValue="OrderSummary" replaceByExpression="model.summaryClassName" ]
}}}@ */
/* @tt{{{ @print-text [ text="// Auto-generated extensions - do not modify" ] }}}@ */
/* @tt{{{ @expand-comment [expandDirection="forward" strip="linebreak"] }}}@ */

fun OrderSummary.label(): String = "Summary: OrderSummary"
/* @tt{{{ @end-template-renderer }}}@ */
/* @tt{{{ @print-text [ text="// Auto-generated summary class - do not modify" ] }}}@ */
/* @tt{{{ @expand-comment [expandDirection="forward" strip="linebreak"] }}}@ */

@Suppress("SUPPRESS_VALUE")
data class OrderSummary(
/* @tt{{{   @foreach [ iteratorExpression="model.fields" loopVariable="field" ] }}}@ */
/* @tt{{{   @foreach [ iteratorExpression="field.validationRules" loopVariable="rule" ] @replace-value-by-expression [ searchValue="ruleText" replaceByExpression="rule" ] }}}@ */
    // ruleText
/* @tt{{{   @end-replace-value-by-expression @end-foreach }}}@ */
/* @tt{{{   @replace-value-by-expression [ searchValue="nullableFieldName" replaceByExpression="field.fieldName" ] [ searchValue="listFieldName" replaceByExpression="field.fieldName" ] [ searchValue="regularFieldName" replaceByExpression="field.fieldName" ] [ searchValue="String" replaceByExpression="field.fieldType" ] }}}@ */
/* @tt{{{   @if [ conditionExpression="field.isNullable" ] }}}@ */
    val nullableFieldName: String?,
/* @tt{{{   @else-if [ conditionExpression="field.isList" ] }}}@ */
    val listFieldName: List<String>,
/* @tt{{{   @else }}}@ */
    val regularFieldName: String,
/* @tt{{{   @end-if }}}@ */
/* @tt{{{   @end-replace-value-by-expression @end-foreach @ignore-text }}}@ */
    val exampleId: String,
/* @tt{{{ @end-ignore-text @end-replace-value-by-value @end-replace-value-by-expression }}}@ */
)
