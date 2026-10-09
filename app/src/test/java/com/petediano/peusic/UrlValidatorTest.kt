package com.petediano.peusic

import com.petediano.peusic.data.download.UrlValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlValidatorTest {

    @Test
    fun acceptsDirectHttpsMp3() {
        val r = UrlValidator.validate("https://example.com/music/track.mp3")
        assertTrue(r.isValid)
        assertTrue(r.normalizedUrl!!.startsWith("https://"))
    }

    @Test
    fun rejectsYoutube() {
        val r = UrlValidator.validate("https://www.youtube.com/watch?v=abc")
        assertFalse(r.isValid)
    }

    @Test
    fun rejectsEmpty() {
        assertFalse(UrlValidator.validate("").isValid)
    }

    @Test
    fun rejectsSpotify() {
        assertFalse(UrlValidator.validate("https://open.spotify.com/track/123").isValid)
    }

    @Test
    fun sanitizesFileName() {
        val name = UrlValidator.sanitizeFileName("my song../evil.mp3")
        assertFalse(name.contains(".."))
    }
}
