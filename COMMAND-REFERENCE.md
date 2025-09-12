# Keyword/Command reference

The following keywords/commands are supported:
* [template-renderer](#template-renderer)
* [template-model](#template-model)
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
* [slac](#slac)
* [slbc](#slbc)
* [modify-provided-filename-by-replacements](#modify-provided-filename-by-replacements)



## template-renderer

Syntax: ```@template-renderer [ templateRendererClassName="..." templateRendererPackageName="..." ]```

Defines in which template the content of the given file is put into. This command must be the first command and can only occur one time per file.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword must have exactly one group of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *templateRendererClassName*: The name of the template class that will generate this template.
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>
* *templateRendererPackageName*: The name of the package where the class defined with ```templateRendererClassName``` resides in.
  * Required attribute: No
  * Required not empty: Yes
  * Allowed values: <unrestricted>

## template-model

Syntax: ```@template-model [ modelClassName="..." modelName="..." modelPackageName="..." ][ ... ]```

Defines model instances that are passed to the template renderer. You can access these instances in your template render to fill data into your template.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command can have many groups of attributes
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *modelName*: The name of the model variable. The variable can later be used to access fields and functions on the model e.g. in conditions or as replacement values.
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>
* *modelClassName*: The name of the model class. This class provides all the fields in the template.
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>
* *modelPackageName*: The name of the package where the model class defined with ```modelClassName``` resides in.
  * Required attribute: No
  * Required not empty: Yes
  * Allowed values: <unrestricted>

## replace-value-by-expression

Syntax: ```@replace-value-by-expression [ searchValue="..." replaceByExpression="..." ][ ... ] .... @end-replace-value-by-expression```

Replaces a value by a kotlin expression in a multiline string.

Varia:
* This command must be closed using the [end-replace-value-by-expression](#end-replace-value-by-expression) command.
* This command supports to be auto-closed. The corresponding [end-replace-value-by-expression](#end-replace-value-by-expression) command can be skipped.
* This command can have many groups of attributes
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *searchValue*: The token that has to be searched in the enclosed block of content. The search is case-sensitive.
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>
* *replaceByExpression*: The expression accessing the model class with which the token defined with the attribute ```searchValue``` is replaced.
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>

## end-replace-value-by-expression

Syntax: ```@end-replace-value-by-expression```

Varia:
* This command is closing the [replace-value-by-expression](#replace-value-by-expression) command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## replace-value-by-value

Syntax: ```@replace-value-by-value [ searchValue="..." replaceByValue="..." ][ ... ] .... @end-replace-value-by-value```

Replaces a value by another value.

Varia:
* This command must be closed using the [end-replace-value-by-value](#end-replace-value-by-value) command.
* This command supports to be auto-closed. The corresponding [end-replace-value-by-value](#end-replace-value-by-value) command can be skipped.
* This command can have many groups of attributes
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *searchValue*: The token that has to be searched in the enclosed block of content. The search is case-sensitive.
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>
* *replaceByValue*: The plain value the attribute ```searchValue``` is replaced.
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>

## end-replace-value-by-value

Syntax: ```@end-replace-value-by-value```

Varia:
* This command is closing the [replace-value-by-value](#replace-value-by-value) command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## if

Syntax: ```@if [ conditionExpression="..." ] .... @end-if```

Render the enclosed content only if the condition is true.

Varia:
* This command must be closed using the [end-if](#end-if) command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword must have exactly one group of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *conditionExpression*: The condition returning a boolean value that is used for the if statement or else-if statement.
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>

## else-if

Syntax: ```@else-if [ conditionExpression="..." ]```

Render the enclosed content only if the condition inside a previously defined if block is true.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword must have exactly one group of attributes.
* This command/keyword must reside as directly nested element in the parent element [if](#if).

Attributes:
* *conditionExpression*: The condition returning a boolean value that is used for the if statement or else-if statement.
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>

## else

Syntax: ```@else```

Render the enclosed content only if not any of the if/else-if clauses evaluates to true.

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
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>
* *loopVariable*: The name of the loop variable, similar to the model variable from ```modelName```. The variable holds the current instance of the loop iterable defined with ```iteratorExpression```.
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>

## end-foreach

Syntax: ```@end-foreach```

Varia:
* This command is closing the [foreach](#foreach) command.
* This command triggers to close all nested commands that support auto-closing.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## ignore-text

Syntax: ```@ignore-text .... @end-ignore-text```

Ignores the text from the content and does not output it in the template renderer.

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

Print text as output of the template renderer.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword must have exactly one group of attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

Attributes:
* *text*: Text that is to print as-is into the template renderer.
  * Required attribute: Yes
  * Required not empty: Yes
  * Allowed values: <unrestricted>

## slac

Syntax: ```@slac```

slac (=**s**trip **l**ine **a**fter **c**omment) removes all characters and the line break (newline) after the comment. This is useful if you don't want to have empty lines in your template result due to the typical templates comments.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## slbc

Syntax: ```@slbc```

slbc (=**s**trip **l**ine **b**efore **c**omment) removes all characters and the line break (newline) before the comment. This is useful if you don't want to have empty lines in your template result due to the typical templates comments.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.

## modify-provided-filename-by-replacements

Syntax: ```@modify-provided-filename-by-replacements```

Each template provide the path of the source file. By using this command, the name will be modified with all replacements provided by ```replace-value-by-expression``` and ```replace-value-by-value```.

Varia:
* This command stands for itself and does not need to be closed by another command.
* This command neither triggers an auto-closing of nested commands nor will it be auto-closed.
* This command/keyword does not support groups and has no attributes.
* This command/keyword is NOT forced to reside as nested element in a certain parent element.
