package org.codeblessing.typicaltemplate.config

import org.codeblessing.typicaltemplate.CommentStyle
import org.codeblessing.typicaltemplate.CommentType
import java.util.Properties

object TypicalTemplateConfigReader {

    private val classLoader = TypicalTemplateConfig::class.java.classLoader
    private const val CONFIG_RESOURCE = "typical-template-config.properties"
    private const val CONFIG_OVERWRITE_RESOURCE = "typical-template-config-overwrite.properties"
    private const val COMMENT_STYLE_PREFIX = "commentStyle."
    private const val EXTENSION_PREFIX = "extension."
    private const val START_SUFFIX = ".startOfComment"
    private const val END_SUFFIX = ".endOfComment"
    private const val TYPE_SUFFIX = ".commentType"

    fun readConfiguration(): TypicalTemplateConfig {
        val properties = loadAllProperties()
        val namedCommentStyles = parseCommentStyles(properties)
        val fileExtensionCommentStyles = parseFileExtensionMappings(properties, namedCommentStyles)
        return TypicalTemplateConfig(
            namedCommentStyles = namedCommentStyles,
            fileExtensionCommentStyles = fileExtensionCommentStyles,
        )
    }

    private fun loadAllProperties(): Properties {
        val defaultProperties = loadProperties(CONFIG_RESOURCE, failOnError = true)
        val overwriteProperties = loadProperties(CONFIG_OVERWRITE_RESOURCE, failOnError = false)
        val properties = Properties()
        properties.putAll(defaultProperties)
        properties.putAll(overwriteProperties)
        return properties
    }

    private fun loadProperties(classpathResource: String, failOnError: Boolean): Properties {
        val properties = Properties()
        val stream = classLoader.getResourceAsStream(classpathResource)
            ?: if (failOnError) {
                error("Resource not found on classpath: $CONFIG_RESOURCE")
            } else {
                return properties
            }

        stream.use { properties.load(it) }
        return properties
    }

    private fun parseCommentStyles(properties: Properties): Map<String, CommentStyle> {
        val identifiers = properties.stringPropertyNames()
            .filter { it.startsWith(COMMENT_STYLE_PREFIX) }
            .mapNotNull { key ->
                val withoutPrefix = key.removePrefix(COMMENT_STYLE_PREFIX)
                val dotIndex = withoutPrefix.indexOf('.')
                if (dotIndex < 0) null else withoutPrefix.substring(0, dotIndex)
            }
            .toSet()

        return identifiers.associateWith { id ->
            val start = properties.getProperty("$COMMENT_STYLE_PREFIX$id$START_SUFFIX")
                ?: error("Missing startOfComment for comment style: $id")
            val end = properties.getProperty("$COMMENT_STYLE_PREFIX$id$END_SUFFIX")
                ?.takeIf { it.isNotEmpty() }
            val commentType = CommentType.valueOf(
                properties.getProperty("$COMMENT_STYLE_PREFIX$id$TYPE_SUFFIX")
                    ?: error("Missing commentType for comment style: $id")
            )
            CommentStyle(startOfComment = start, endOfComment = end, commentType = commentType)
        }
    }

    private fun parseFileExtensionMappings(
        properties: Properties,
        namedCommentStyles: Map<String, CommentStyle>,
    ): List<FileExtensionCommentStyles> {
        return properties.stringPropertyNames()
            .filter { it.startsWith(EXTENSION_PREFIX) }
            .map { key ->
                val extension = key.removePrefix(EXTENSION_PREFIX)
                val identifiers = properties.getProperty(key).split(",").map { it.trim() }
                val commentStyles = identifiers.map { id ->
                    namedCommentStyles[id] ?: error("Unknown comment style identifier '$id' for extension: $extension")
                }
                FileExtensionCommentStyles(extension = extension, commentStyles = commentStyles)
            }
    }
}
