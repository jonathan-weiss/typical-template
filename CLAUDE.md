# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**typical-template** is a reverse template engine for Kotlin. It parses real source files (HTML, Kotlin, TypeScript, SCSS) annotated with special comment-based commands (`@tt{{{ ... }}}@`) and generates Kotlin renderer classes that produce output using multiline string templates.

## Build Commands

```bash
# Build the project
./gradlew build

# Run tests (JUnit 5)
./gradlew test

# Run a single test class
./gradlew :typical-template:test --tests "org.codeblessing.typicaltemplate.ExampleTemplateTest"

# Clean build
./gradlew clean build
```

## Project Structure

Multi-module Gradle project (Kotlin DSL, Kotlin 2.2.0):

- **`typical-template-api/`** — Public API module. Entry point is `TypicalTemplateApi` (uses Java `ServiceLoader` to load the processor). Contains configuration data classes (`TemplatingConfiguration`, `FileSearchLocation`, `TemplateRendererConfiguration`) and the `TypicalTemplateProcessorApi` interface.
- **`typical-template/`** — Core implementation. Implements the processing pipeline.
- **`typical-template-full-process-example/`** — Three example subprojects demonstrating end-to-end usage (creator CLI, executor CLI, annotated business project).
- **`buildSrc/`** — Custom Gradle convention plugins for Maven Central publishing and repository config.

## Processing Pipeline

The `TypicalTemplateProcessor` orchestrates the entire flow:

```
FileTraversal.searchFiles()          — find source files matching patterns
  → ContentMapper.mapContent()       — determine comment style from file extension
  → ContentParser.parseContent()     — parse into template renderer descriptions
      → FileContentTokenizer         — tokenize into plain text + command tokens
      → Fragmenter                   — group tokens into logical fragments
      → CommandChainCreator          — validate and interpret command chains
  → TemplateRendererWriter           — write generated Kotlin classes
      → TemplateRendererContentCreator      — build multiline string content
      → TemplateRendererClassContentCreator — wrap in Kotlin class/object
```

Key packages under `org.codeblessing.typicaltemplate`:
- `contentparsing/` — tokenizer, fragmenter, comment parser
- `commandchain/` — command chain creation and validation
- `filemapping/` — file extension → comment style mapping (HTML, Kotlin, TypeScript, SCSS)
- `filesearch/` — recursive file discovery
- `templaterenderer/` — Kotlin code generation
- `documentation/` — markdown command reference generation

## Key Conventions

- The API and implementation are decoupled via `ServiceLoader` (see `META-INF/services/` in the typical-template module).
- No external runtime dependencies beyond Kotlin stdlib — pure Kotlin implementation.
- Template commands are documented in `COMMAND-REFERENCE.md` (auto-generated).
- Test resources in `typical-template/src/test/resources/` contain HTML fixtures and expected output `.txt` files.
