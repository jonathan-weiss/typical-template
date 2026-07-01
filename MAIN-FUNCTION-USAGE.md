# Main function usage

Call the main function ```org.codeblessing.tavnit.TavnitKt``` with the following parameter:

```
Usage: <tavnit> --template-renderer <path> --search <path>:<pattern> [--search <path>:<pattern> ...]

Options:
  --template-renderer <path>   Target base directory for generated renderer classes (required)
  --search <path>:<pattern>    Source directory and filename glob to search, e.g. ./src:*.kt (required, repeatable)
  --help                       Show this help message

Examples:
  <tavnit> --template-renderer ./src/generated --search ./src/main/kotlin:*.kt
  <tavnit> --template-renderer ./src/generated --search ./src/main/kotlin:*.kt --search ./src/main/resources:*.html
  
Where <tavnit> is:    
    java -cp ./tavnit-api.jar:./tavnit.jar:$KOTLIN_HOME/lib/kotlin-stdlib.jar org.codeblessing.tavnit.TavnitKt
or 
    kotlin -classpath ./tavnit-api.jar:./tavnit.jar org.codeblessing.tavnit.TavnitKt
```
