# Keyword/Command reference

The following keywords/commands are supported:
* [template-renderer](#template-renderer)
* [end-template-renderer](#end-template-renderer)
* [replace-value-by-expression](#replace-value-by-expression)
* [end-replace-value-by-expression](#end-replace-value-by-expression)
* [replace-value-by-value](#replace-value-by-value)
* [end-replace-value-by-value](#end-replace-value-by-value)
* [if](#if)
* [else-if](#else-if)
* [else](#else)
* [end-if](#end-if)
* [foreach](#foreach)
* [end-foreach](#end-foreach)
* [ignore-text](#ignore-text)
* [end-ignore-text](#end-ignore-text)
* [print-text](#print-text)
* [modify-provided-filename-by-replacements](#modify-provided-filename-by-replacements)
* [render-template](#render-template)

Commands always starts with a `$`.

The following _preprocessing_ keywords/commands are supported:
* [move-comment](#move-comment)
* [expand-comment](#expand-comment)

Preprocessing commands always starts with a `#`.



## template-renderer

Syntax: ```@template-renderer [ templateRendererClassName="..." templateRendererPackageName="..." templateRendererInterfaceName="..." templateRendererInterfacePackageName="..." ] [ modelClassName="..." modelName="..." modelPackageName="..." isList="yes|no" ] [ ... ] .... @end-template-renderer```

Defines the template renderer kotlin class in which the content of the given file is put into. Optionally declares model instances (kotlin function parameters) passed to the renderer. 

The first attribute group specifies the renderer class; subsequent repeating groups each define one model parameter.

Additional template-renderer commands can be nested inside the top-level one; each nested template-renderer produces an independent renderer class and is closed with end-template-renderer.A nested template renderer is completely independent (and its content therefore removed from) the parent template renderer.Also all other commands defined in the parent template (models, if..else, replacements, etc.) will not affect the child template renderer, as each template renderer resides in its own class.

Varia:
* This command must be closed using the [end-template-renderer](#end-template-renderer) command.
* This command supports to be auto-closed. The corresponding [end-template-renderer](#end-template-renderer) command can be skipped.
* This command has a primary group of attributes optionally followed by zero or more groups of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Primary Attributes:
* *templateRendererClassName*: The name of the template class that will generate this template.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none
* *templateRendererPackageName*: The name of the package where the class defined with ```templateRendererClassName``` resides in.
  * Required attribute: _No_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none
* *templateRendererInterfaceName*: The name of an optional interface class name that is added to the class defined with the ```templateRendererClassName```.
  * Required attribute: _No_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none
* *templateRendererInterfacePackageName*: The name of the package where the interface defined with ```templateRendererInterfaceName``` resides in.
  * Required attribute: _No_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none

Repeatable Group Attributes:
* *modelName*: The name of the model variable. The variable can later be used to access fields and functions on the model e.g. in conditions or as replacement values.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none
* *modelClassName*: The name of the model class. This class provides all the fields in the template.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none
* *modelPackageName*: The name of the package where the model class defined with ```modelClassName``` resides in.
  * Required attribute: _No_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none
* *isList*: When set to ```yes```, the model parameter is declared as a list of the model class defined with ```modelClassName```, i.e. ```List<ModelClass>``` instead of ```ModelClass```. Defaults to ```no```.
  * Required attribute: _No_
  * Required not empty: _Yes_
  * Allowed values: ```yes```,```no```
  * Mutually exclusive with: none

## end-template-renderer

Syntax: ```@end-template-renderer```

Varia:
* This command is closing the [template-renderer](#template-renderer) command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## replace-value-by-expression

Syntax: ```@replace-value-by-expression [ searchValue="..." replaceByExpression="..." ] [ ... ] .... @end-replace-value-by-expression```

Replaces a value by a kotlin expression in a multiline string. The expression is often accessing properties or functions on a model instance declared with the template-renderer command.

Varia:
* This command must be closed using the [end-replace-value-by-expression](#end-replace-value-by-expression) command.
* This command supports to be auto-closed. The corresponding [end-replace-value-by-expression](#end-replace-value-by-expression) command can be skipped.
* This command can have many groups of attributes
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Repeatable Group Attributes:
* *searchValue*: The token that has to be searched in the enclosed block of content. The search is case-sensitive.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none
* *replaceByExpression*: The expression accessing the model class with which the token defined with the attribute ```searchValue``` is replaced.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none

## end-replace-value-by-expression

Syntax: ```@end-replace-value-by-expression```

Varia:
* This command is closing the [replace-value-by-expression](#replace-value-by-expression) command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## replace-value-by-value

Syntax: ```@replace-value-by-value [ searchValue="..." replaceByValue="..." ] [ ... ] .... @end-replace-value-by-value```

Replaces a value by another (fixed) value.

Varia:
* This command must be closed using the [end-replace-value-by-value](#end-replace-value-by-value) command.
* This command supports to be auto-closed. The corresponding [end-replace-value-by-value](#end-replace-value-by-value) command can be skipped.
* This command can have many groups of attributes
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Repeatable Group Attributes:
* *searchValue*: The token that has to be searched in the enclosed block of content. The search is case-sensitive.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none
* *replaceByValue*: The plain value the attribute ```searchValue``` is replaced.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none

## end-replace-value-by-value

Syntax: ```@end-replace-value-by-value```

Varia:
* This command is closing the [replace-value-by-value](#replace-value-by-value) command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## if

Syntax: ```@if [ conditionExpression="..." ] .... @end-if```

Render the enclosed content only if the condition expression evaluates to true.

Varia:
* This command must be closed using the [end-if](#end-if) command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword must have exactly one group of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *conditionExpression*: The condition returning a boolean value that is used for the if statement or else-if statement.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none

## else-if

Syntax: ```@else-if [ conditionExpression="..." ]```

Render the enclosed content only if the condition expression evaluates to true and all previous conditions of the if/else-if conditions evaluates to false.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword must have exactly one group of attributes.
* This command/keyword must reside as directly nested element in the parent element [if](#if).

Attributes:
* *conditionExpression*: The condition returning a boolean value that is used for the if statement or else-if statement.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none

## else

Syntax: ```@else```

Render the enclosed content only if all previous if/else-if conditions evaluates to false

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword does not support groups and has no attributes.
* This command/keyword must reside as directly nested element in the parent element [if](#if).

## end-if

Syntax: ```@end-if```

Varia:
* This command is closing the [if](#if) command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## foreach

Syntax: ```@foreach [ iteratorExpression="..." loopVariable="..." ] .... @end-foreach```

Iterates/Loops over a collection of items (=iterable). In each loop, the current item is hold in a loop variable.

Varia:
* This command must be closed using the [end-foreach](#end-foreach) command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword must have exactly one group of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *iteratorExpression*: The condition returning a boolean value that is used for the if statement.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none
* *loopVariable*: The name of the loop variable, similar to the model variable from ```modelName```. The variable holds the current instance of the loop iterable defined with ```iteratorExpression```.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none

## end-foreach

Syntax: ```@end-foreach```

Varia:
* This command is closing the [foreach](#foreach) command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## ignore-text

Syntax: ```@ignore-text .... @end-ignore-text```

Ignores the text and does not output it in the template renderer.

Varia:
* This command must be closed using the [end-ignore-text](#end-ignore-text) command.
* This command supports to be auto-closed. The corresponding [end-ignore-text](#end-ignore-text) command can be skipped.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## end-ignore-text

Syntax: ```@end-ignore-text```

Varia:
* This command is closing the [ignore-text](#ignore-text) command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## print-text

Syntax: ```@print-text [ text="..." ]```

Print additional text as output of the template renderer.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword must have exactly one group of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *text*: Text that is to print as-is into the template renderer.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none

## modify-provided-filename-by-replacements

Syntax: ```@modify-provided-filename-by-replacements```

Each template renderer provides the path of the source file as string. By using this command, the path can be modified with all replacements provided by ```replace-value-by-expression``` and ```replace-value-by-value``` the ```modify-provided-filename-by-replacements``` command is currently nested in.

The intention of this command is that the filename and path can also take part of the replacements and this has not to be handled separately and outside of the template renderer; the replacements for the filename follow often the same patterns as for the file content.If you change in your template every ```foo``` to ```bar```, it is likely that you also want to change the path of the file e.g. from ```src/foo/foo.txt``` to ```src/bar/bar.txt``` to generate dynamic file paths.

You can use this command multiple times per template renderer. The replacements are done one after another in the order of the command usage.

If you create multiple template renderers from one file (multiple template-renderer), you can (and have to) call ```modify-provided-filename-by-replacements``` for each template renderer individually.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## render-template

Syntax: ```@render-template [ templateRendererClassName="..." templateRendererPackageName="..." ] [ modelName="..." modelExpression="..." ] [ ... ]```

Calls another template renderer and embeds its output. The first attribute group specifies the renderer class; subsequent groups map model parameters to expressions.

This command's syntax has a lot of similarity to template-renderer, as it calls a template renderer defined by the template-renderer block.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command has a primary group of attributes followed by one or more groups of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Primary Attributes:
* *templateRendererClassName*: The name of the template class that will generate this template.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none
* *templateRendererPackageName*: The name of the package where the class defined with ```templateRendererClassName``` resides in.
  * Required attribute: _No_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none

Repeatable Group Attributes:
* *modelName*: The name of the model variable. The variable can later be used to access fields and functions on the model e.g. in conditions or as replacement values.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none
* *modelExpression*: The expression that provides the value for the model parameter specified by ```modelName``` when calling the template renderer.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: none

## move-comment

Syntax: ```#move-comment [ direction="backward|forward" beforeFirstOccurrenceOf="..." afterFirstOccurrenceOf="..." beforeLastOccurrenceOf="..." afterLastOccurrenceOf="..." ]```

Moves the whole comment in which this command is written in the specified direction. Optionally positions it relative to the first or last occurrence of a given text in the surrounding content. The comment will be moved at most to the previous/next comment or to the beginning or end of the file.

This is useful as some file formats do not allow to put a comment as first line of the file.

Example:  XML starts with a preamble like ```<?xml version="1.0" encoding="iso-8859-1"?>``` and this text should be part of the template renderer's output. But it is not possible to write a XML comment before this preamble. To still span the template from the beginning of the file, you can move the comment to the beginning of the file using this command (```#move-comment[direction=backward]```)

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword must have exactly one group of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *direction*: The direction in which the comment is moved.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: ```backward```,```forward```
  * Mutually exclusive with: none
* *beforeFirstOccurrenceOf*: Positions the comment before the first occurrence of the given text in the surrounding content.
  * Required attribute: _No_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: ```afterFirstOccurrenceOf```, ```beforeLastOccurrenceOf```, ```afterLastOccurrenceOf```
* *afterFirstOccurrenceOf*: Positions the comment after the first occurrence of the given text in the surrounding content.
  * Required attribute: _No_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: ```beforeFirstOccurrenceOf```, ```beforeLastOccurrenceOf```, ```afterLastOccurrenceOf```
* *beforeLastOccurrenceOf*: Positions the comment before the last occurrence of the given text in the surrounding content.
  * Required attribute: _No_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: ```beforeFirstOccurrenceOf```, ```afterFirstOccurrenceOf```, ```afterLastOccurrenceOf```
* *afterLastOccurrenceOf*: Positions the comment after the last occurrence of the given text in the surrounding content.
  * Required attribute: _No_
  * Required not empty: _Yes_
  * Allowed values: _\<unrestricted\>_
  * Mutually exclusive with: ```beforeFirstOccurrenceOf```, ```afterFirstOccurrenceOf```, ```beforeLastOccurrenceOf```

## expand-comment

Syntax: ```#expand-comment [ expandDirection="backward|forward" strip="blanks|linebreak" ]```

Expands the comment into the adjacent text in the specified direction by stripping leading or trailing whitespace (blanks and optionally a line-ending) from the neighboring text part.

This is useful if you don't want to have empty lines in your template output due to the typical templates comments or dangling spaces/idents if the typical template comments itself have to follow some ident rules (e.g. by your linter).

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword must have exactly one group of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *expandDirection*: The direction in which the comment expands into the adjacent text (```forward``` or ```backward```).
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: ```backward```,```forward```
  * Mutually exclusive with: none
* *strip*: Controls how much whitespace is stripped from the adjacent text. ```blanks``` removes only spaces and tabs; ```linebreak``` also removes the immediately adjacent line-ending.
  * Required attribute: _Yes_
  * Required not empty: _Yes_
  * Allowed values: ```blanks```,```linebreak```
  * Mutually exclusive with: none
