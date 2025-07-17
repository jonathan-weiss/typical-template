package org.codeblessing.typicaltemplate.filemapping

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ContentMapperTest {

    @Test
    fun `assert that files ending with html are matching HTML_FILENAME_REGEX`() {
        assertEquals(true, ContentMapper.HTML_FILENAME_REGEX.matches("asdf.html"))
        assertEquals(true, ContentMapper.HTML_FILENAME_REGEX.matches("my-address-form.html"))
        assertEquals(true, ContentMapper.HTML_FILENAME_REGEX.matches("asdf.xhtml"))
        assertEquals(true, ContentMapper.HTML_FILENAME_REGEX.matches("my-address-form.component.xhtml"))
    }

    @Test
    fun `assert that files not ending with html are not matching HTML_FILENAME_REGEX`() {
        assertEquals(false, ContentMapper.HTML_FILENAME_REGEX.matches("asdf.scss"))
    }

}
