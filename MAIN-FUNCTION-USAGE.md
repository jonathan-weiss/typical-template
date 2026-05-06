# Main function usage

Call the main function ```org.codeblessing.typicaltemplate.TypicalTemplateKt``` with the following parameter:

```
Usage: typical-template --template-render <path> --search <path>:<pattern> [--search <path>:<pattern> ...]

Options:
  --template-render <path>     Target base directory for generated renderer classes (required)
  --search <path>:<pattern>    Source directory and filename glob to search, e.g. ./src:*.kt (required, repeatable)
  --help                       Show this help message

Examples:
  typical-template --template-render ./src/generated --search ./src/main/kotlin:*.kt
  typical-template --template-render ./src/generated --search ./src/main/kotlin:*.kt --search ./src/main/resources:*.html
```
