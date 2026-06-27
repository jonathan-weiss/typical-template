package org.codeblessing.typicaltemplate.example.renderer.model

/**
 * Model for the nested-if blackbox test. The boolean flags let the template select an `if` / `else-if`
 * / `else` branch (via [isBook] / [isMovie]) and, inside the `book` branch, a nested `if` / `else`
 * branch (via [highlighted]) without needing string literals in the condition expressions.
 */
data class IfModel(
    val isBook: Boolean,
    val isMovie: Boolean,
    val highlighted: Boolean,
)
