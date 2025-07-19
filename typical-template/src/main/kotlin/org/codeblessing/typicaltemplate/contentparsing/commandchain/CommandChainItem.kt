package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand

data class CommandChainItem(
    val keywordCommand: KeywordCommand
): ChainItem
