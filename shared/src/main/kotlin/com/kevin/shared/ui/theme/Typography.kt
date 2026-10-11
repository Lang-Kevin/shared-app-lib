package com.kevin.shared.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.kevin.shared.R

private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.shared_com_google_android_gms_fonts_certs
)

private val spaceGroteskFont = GoogleFont("Space Grotesk")

val SpaceGroteskFamily: FontFamily = FontFamily(
    Font(googleFont = spaceGroteskFont, fontProvider = fontProvider, weight = FontWeight.Light),
    Font(googleFont = spaceGroteskFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = spaceGroteskFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = spaceGroteskFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = spaceGroteskFont, fontProvider = fontProvider, weight = FontWeight.Bold),
)

private val base = Typography()

// Night Drive: hero numbers Light, labels small/uppercase/tracked (uppercase at the call site).
val AppTypography: Typography = Typography(
    displayLarge   = base.displayLarge.copy(fontFamily = SpaceGroteskFamily, fontWeight = FontWeight.Light,
                         fontSize = 64.sp, lineHeight = 68.sp, letterSpacing = 0.sp),
    displayMedium  = base.displayMedium.copy(fontFamily = SpaceGroteskFamily, fontWeight = FontWeight.Light,
                         fontSize = 52.sp, lineHeight = 56.sp, letterSpacing = 0.sp),
    displaySmall   = base.displaySmall.copy(fontFamily   = SpaceGroteskFamily),
    headlineLarge  = base.headlineLarge.copy(fontFamily  = SpaceGroteskFamily),
    headlineMedium = base.headlineMedium.copy(fontFamily = SpaceGroteskFamily),
    headlineSmall  = base.headlineSmall.copy(fontFamily  = SpaceGroteskFamily, fontWeight = FontWeight.Normal),
    titleLarge     = base.titleLarge.copy(fontFamily     = SpaceGroteskFamily),
    titleMedium    = base.titleMedium.copy(fontFamily    = SpaceGroteskFamily, fontWeight = FontWeight.Medium,
                         letterSpacing = 0.2.em),
    titleSmall     = base.titleSmall.copy(fontFamily     = SpaceGroteskFamily),
    bodyLarge      = base.bodyLarge.copy(fontFamily      = SpaceGroteskFamily),
    bodyMedium     = base.bodyMedium.copy(fontFamily     = SpaceGroteskFamily),
    bodySmall      = base.bodySmall.copy(fontFamily      = SpaceGroteskFamily),
    labelLarge     = base.labelLarge.copy(fontFamily     = SpaceGroteskFamily),
    labelMedium    = base.labelMedium.copy(fontFamily    = SpaceGroteskFamily),
    labelSmall     = base.labelSmall.copy(fontFamily     = SpaceGroteskFamily, fontWeight = FontWeight.Normal,
                         fontSize = 9.5.sp, lineHeight = 14.sp, letterSpacing = 0.16.em),
)
