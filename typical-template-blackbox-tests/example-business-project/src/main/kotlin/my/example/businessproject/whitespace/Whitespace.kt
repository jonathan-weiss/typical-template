package my.example.businessproject.whitespace

/*
 * Template that demonstrates the whitespace-handling commands in a Kotlin (block-comment) file.
 * It is the Kotlin counterpart of `src/webapp/whitespace/whitespace.html` and exercises exactly the
 * same commands so the parameterized blackbox test can show that the behaviour is independent of the
 * comment style. The file is valid Kotlin on purpose (it lives in a compiled source set); the
 * `"keepA" + "keepB"` concatenations are only carriers that make the effect of each command visible.
 */
/* @tt{{{ @template-renderer [ templateRendererClassName="WhitespaceKotlinRenderer" templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer" ] }}}@ */
val default = "keepA" +
   /* @tt{{{ @print-text [ text="" ] }}}@ */
   "keepB"
val rbb = "keepA" +   /* @tt{{{ @remove-blanks-before-comment }}}@ */"keepB"
val rba = "keepA"/* @tt{{{ @remove-blanks-after-comment }}}@ */   + "keepB"
val rlb = "keepA" +
   /* @tt{{{ @remove-blanks-and-linebreak-before-comment }}}@ */"keepB"
val rla = "keepA" + /* @tt{{{ @remove-blanks-and-linebreak-after-comment }}}@ */
   "keepB"
val klb = "keepA" +
   /* @tt{{{ @keep-blanks-and-linebreak-before-comment }}}@ */
   "keepB"
val kla = "keepA" +
   /* @tt{{{ @keep-blanks-and-linebreak-after-comment }}}@ */
   "keepB"
