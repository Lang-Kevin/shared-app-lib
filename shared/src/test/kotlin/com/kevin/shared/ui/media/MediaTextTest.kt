package com.kevin.shared.ui.media

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kevin.shared.ui.theme.Brand
import com.kevin.shared.ui.theme.toAppColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MediaTextTest {
    private val media = Brand.Default.toAppColors()

    @Test
    fun mediaTextStyle_keepsTypeScale_addsTokenColorAndShadow() {
        val base = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.2.sp)
        val style = mediaTextStyle(base, media.onMedia, media.mediaTextShadow)
        assertEquals(13.sp, style.fontSize)
        assertEquals(FontWeight.Medium, style.fontWeight)
        assertEquals(0.2.sp, style.letterSpacing)
        assertEquals(media.onMedia, style.color)
        assertEquals(media.mediaTextShadow, style.shadow)
    }

    @Test
    fun emphasizedShadow_changesOnlyAlpha() {
        val token = media.mediaTextShadow
        val strong = emphasizedShadow(token)
        // Color stores alpha in 8 bits for sRGB, hence the tolerance.
        assertEquals(EmphasizedShadowAlpha, strong.color.alpha, 0.005f)
        assertEquals(token.color.copy(alpha = 1f), strong.color.copy(alpha = 1f))
        assertEquals(token.offset, strong.offset)
        assertEquals(token.blurRadius, strong.blurRadius, 0f)
    }

    @Test
    fun readingText_missingValue_showsPlaceholderWithoutUnit() {
        assertEquals(ReadingText("–", null), readingText(null, "cm"))
        assertEquals(ReadingText("–", null), readingText("  ", "cm"))
    }

    @Test
    fun readingText_presentValue_keepsUnit_dropsBlankUnit() {
        assertEquals(ReadingText("42.5", "cm"), readingText("42.5", "cm"))
        assertNull(readingText("42.5", " ").unit)
        assertNull(readingText("42.5", null).unit)
    }
}
