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

### Keywords

The following keywords are supported:


####  @@tt-template

Defines that the enclosed content is considered to be a template. In most cases, this content spans over the whole file.

Attributes:
* *templateName*: The name of the template.
* *templatePackage*: The kotlin package where the template resides in.
* *modelName*: The name of the model (root object passed to the template).

#### @@tt-placeholder

Defines that the enclosed content has to be replaced by a placeholder.

Attributes:
* *placeholder*: The name of the field to insert.
* *replaceOnlyPart*: The name of the token, that has to be replaced by the placeholder. If not defined, the whole content is replaced.

#### @@tt-list

Defines that the enclosed code is a repeated block of code.

Attributes:
* *collection*: The name of the field with the entries.
* *entry*: The name of the variable where the current entry is stored.

#### @@tt-list-no-elements

Defines that the enclosed code is rendered, if the list has no entries.
This block must always be nested inside a `@@tt-list`.

Attributes:
* *collection*: The name of the field with the entries.
* *entry*: The name of the variable where the current entry is stored.

#### @tt-ignore

Defines that the enclosed code will not be part of the template and is ignored.

#### @tt-if

Defines that the enclosed code will be only rendered if the condition field evaluates to `true`.

Attributes:
* *conditionField*: The name of the boolean field on the model to use for evaluation.

#### Other keyword
TODO specify those keywords
* list hasNoElements, isFirst/isLast-Element, Index
* if
* elseif
* else
* (when)

