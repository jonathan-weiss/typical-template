# Main function usage

Call the main function ```org.codeblessing.typicaltemplate.TypicalTemplateKt``` with the following parameter:

```
Usage: <typical-template> --template-renderer <path> --search <path>:<pattern> [--search <path>:<pattern> ...]

Options:
  --template-renderer <path>   Target base directory for generated renderer classes (required)
  --search <path>:<pattern>    Source directory and filename glob to search, e.g. ./src:*.kt (required, repeatable)
  --help                       Show this help message

Examples:
  <typical-template> --template-renderer ./src/generated --search ./src/main/kotlin:*.kt
  <typical-template> --template-renderer ./src/generated --search ./src/main/kotlin:*.kt --search ./src/main/resources:*.html
  
Where <typical-template> is:    
    java -cp ./typical-template-api.jar:./typical-template.jar:$KOTLIN_HOME/lib/kotlin-stdlib.jar org.codeblessing.typicaltemplate.TypicalTemplateKt
or 
    kotlin -classpath ./typical-template-api.jar:./typical-template.jar org.codeblessing.typicaltemplate.TypicalTemplateKt
```
