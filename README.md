# @typical-template reverse template engine

Typical template is a reverse template engine. Your write your real source code components and classes and add (with source code comments) metadata. With help of this metadata, typical-template can create kotlin multi-line templates for you.

The advantage of this approach is, that when you extend your real source code components and classes, the synchronization of the template is done by only run again typical-template and you do not have to keep your templates and your source code in sync manually.

## Example

Here is an example of a real source code enriched with typical-template metadata:

```
<!-- @@tt-template templateName="SearchResultPage" modelName="model"  -->
<html>
<head><title><!-- @@tt-placeholder field="model.title" -->News<!-- @@$tt-placeholder --></title></head>
<body>
<p>Here are the search results:</p>
<ul><!-- @@tt-list collection="model.searchResults" entry="searchResult" -->
  <li><!-- @@tt-placeholder placeholderField="searchResult" -->How to set up your garden in the spring<!-- @@$tt-placeholder --></li><!-- @@$tt-list -->
<!-- @@tt-ignore -->
  <li>Five keys to become rich in one year</li>
  <li>What's up with prince charles?</li><!-- @@$tt-ignore -->
</ul>
</body>
</html>
<!-- @@$tt-template  -->
```

## Syntax

Write block comments (e.g. `/* ... */`, `<!-- ... -->`) or line comments (e.g. `// ...`) in your source file. All comments containing the magic word "@@tt-" will be considered as syntax for typical template.

#### Source Code Enclosing

All keywords surround source code with an open and a closing part.
The opening part is always the beginning with `@@tt-<keyword>` and this comment can have additional attributes.
The closing part is always the same keyword like the corresponding beginning keyword, but with `@@$-<keyword>`.

Here an example:

```
Hallo, this is <!-- @@tt-placeholder field="model.firstname" -->Jonas<!-- @@$tt-placeholder -->.
```

The word "Jonas" will be replaced by a placeholder accessing the field "model.firstname".

### Keywords/Commands

The following keywords/commands are supported:

#### @@tt-template-renderer

Syntax: @@tt-template-renderer [ templateRendererClassName="..." templateRendererPackageName="..." ]

Defines in which template the content of the given file is put into. This command must be the first command and can only occur one time per file.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command/keyword must have exactly one group of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *templateRendererClassName*: The name of the template class that will generate this template.
    * Required attribute: Yes
    * Required not empty: Yes
    * Allowed values: <unrestricted>
* *templateRendererPackageName*: The name of the package where the class defined with 'templateRendererClassName' resides in.
    * Required attribute: No
    * Required not empty: Yes
    * Allowed values: <unrestricted>

#### @@tt-replace-value-by-expression

Syntax: @@tt-replace-value-by-expression [ searchValue="..." replaceByExpression="..." ][ ... ] .... @@tt-end-replace-value-by-expression

Defines in which template the content of the given file is put into. This command must be the first command and can only occur one time per file.

Varia:
* This command must be closed using the @@tt-end-replace-value-by-expression command.
* This command can have many groups of attributes
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *searchValue*: The token that has to be searched in the enclosed block of content. The search is case-sensitive.
    * Required attribute: Yes
    * Required not empty: Yes
    * Allowed values: <unrestricted>
* *replaceByExpression*: The expression accessing the model class with which the token defined with the attribute 'searchValue' is replaced.
    * Required attribute: Yes
    * Required not empty: Yes
    * Allowed values: <unrestricted>

#### @@tt-end-replace-value-by-expression

Syntax: @@tt-end-replace-value-by-expression

Varia:
* This command is closing the @@tt-replace-value-by-expression command.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

#### @@tt-if-condition

Syntax: @@tt-if-condition [ conditionExpression="..." ] .... @@tt-end-if-condition

Render the enclosed content only if the condition is true.

Varia:
* This command must be closed using the @@tt-end-if-condition command.
* This command/keyword must have exactly one group of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *conditionExpression*: The condition returning a boolean value that is used for the if statement or else-if statement.
    * Required attribute: Yes
    * Required not empty: Yes
    * Allowed values: <unrestricted>

#### @@tt-else-if-condition

Syntax: @@tt-else-if-condition [ conditionExpression="..." ]

Render the enclosed content only if the condition inside a previously defined if block is true.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command/keyword must have exactly one group of attributes.
* This command/keyword must reside as directly nested element in the parent element @@tt-if-condition.

Attributes:
* *conditionExpression*: The condition returning a boolean value that is used for the if statement or else-if statement.
    * Required attribute: Yes
    * Required not empty: Yes
    * Allowed values: <unrestricted>

#### @@tt-else-of-if-condition

Syntax: @@tt-else-of-if-condition

Render the enclosed content only if not any of the if/else-if clauses evaluates to true.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command/keyword does not support groups and has no attributes.
* This command/keyword must reside as directly nested element in the parent element @@tt-if-condition.

#### @@tt-end-if-condition

Syntax: @@tt-end-if-condition

Varia:
* This command is closing the @@tt-if-condition command.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

#### @@tt-foreach

Syntax: @@tt-foreach [ iteratorExpression="..." loopVariable="..." ] .... @@tt-end-foreach

Iterates/Loops over a collection of items (=iterable). In each loop, the current item is hold in a loop variable.

Varia:
* This command must be closed using the @@tt-end-foreach command.
* This command/keyword must have exactly one group of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *iteratorExpression*: The condition returning a boolean value that is used for the if statement.
    * Required attribute: Yes
    * Required not empty: Yes
    * Allowed values: <unrestricted>
* *loopVariable*: The name of the loop variable, similar to the model variable from 'modelName'. The variable holds the current instance of the loop iterable defined with 'iteratorExpression'.
    * Required attribute: Yes
    * Required not empty: Yes
    * Allowed values: <unrestricted>

#### @@tt-end-foreach

Syntax: @@tt-end-foreach

Varia:
* This command is closing the @@tt-foreach command.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

#### @@tt-ignore-text

Syntax: @@tt-ignore-text .... @@tt-end-ignore-text

Ignores the text from the content and does not output it in the template renderer.

Varia:
* This command must be closed using the @@tt-end-ignore-text command.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

#### @@tt-end-ignore-text

Syntax: @@tt-end-ignore-text

Varia:
* This command is closing the @@tt-ignore-text command.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

