package com.petediano.peusic

import com.petediano.peusic.data.download.UrlValidator
import org.junit.Assert.*
import org.junit.Test

class UrlValidatorTest {

    @Test
    fun validHttpsMp3() {
        val result = UrlValidator.validate("https://example.com/music/track.mp3")
        assertTrue(result.isValid)
        assertNotNull(result.normalizedUrl)
        assertTrue(result.suggestedFileName!!.endsWith(".mp3"))
    }

    @Test
    fun rejectsYoutube() {
        val result = UrlValidator.validate("https://www.youtube.com/watch?v=abc123")
        assertFalse(result.isValid)
        assertTrue(result.reason!!.contains("does not permit"))
    }

    @Test
    fun rejectsEmpty() {
        assertFalse(UrlValidator.validate("").isValid)
        assertFalse(UrlValidator.validate("   ").isValid)
    }

    @Test
    fun rejectsFileScheme() {
        assertFalse(UrlValidator.validate("file:///sdcard/music.mp3").isValid)
    }

    @Test
    fun sanitizesDangerousNames() {
        val clean = UrlValidator.sanitizeFileName("../../etc/passwd")
        assertFalse(clean.contains(".."))
        assertFalse(clean.contains("/"))
    }

    @Test
    fun acceptsHttpWithWarningPath() {
        val result = UrlValidator.validate("http://cdn.example.org/audio/song.m4a")
        assertTrue(result.isValid)
    }
}
