# Design System Foundation (Sub-project 1) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship lib 0.4.0: Night Drive tokens (Brand, AppColors, AppSpacing, shapes, typography), the rewired `AppTheme`, deprecated `SharedColors` vals, lib components on tokens, `DESIGN.md`, and the `check_design_tokens.py` CI checker.

**Architecture:** Pure token builders (`Brand.toColorScheme()`, `Brand.toAppColors()`) are unit-tested on the JVM. `AppTheme(brand)` provides them via `MaterialTheme` plus `LocalAppColors`/`LocalAppSpacing`, with an `object AppTheme` accessor. A Python checker enforces token use and runs in lib CI.

**Tech Stack:** Kotlin 2.4.20, Compose BOM 2026.05.01 (Material3), JUnit 4, Python 3 (stdlib only).

**Spec:** `docs/superpowers/specs/2026-10-11-design-system-foundation-design.md` (§4, §5). Visual reference: `docs/design/mockups/00-final-screens.html`.

## Global Constraints

- Branch: `design-system-foundation` (lib repo). Never commit to `master`. No push without the main session's go-ahead.
- Package: `com.kevin.shared.ui.theme`; dir `shared/src/main/kotlin/com/kevin/shared/ui/theme/`. Tests: `shared/src/test/kotlin/com/kevin/shared/ui/theme/`.
- Dark only. Default accent `0xFFE8202E`, `onAccent` `0xFFFFFFFF`.
- Neutrals: background `0xFF000000`, surface `0xFF0B0B0D`, outline/surfaceVariant `0xFF1F1F23`, outlineVariant `0xFF2A2A2F`, onSurface/onBackground `0xFFF2F2F4`, onSurfaceVariant `0xFF8D8D94`, textDisabled `0xFF55555C`, secondary `0xFFA3A3AB`, error `0xFFFF5A4F`.
- Spacing 2/4/8/12/16/24/32 dp. Shapes 8/14/20 dp + pill `RoundedCornerShape(50)`.
- Deprecated `SharedColors.kt` vals keep their **old values**.
- No halos, blur, or glow shadows. Accent emphasis = 1dp accent border + `accentTint` fill.
- **Local build environment (copy into every subagent prompt):**
  - Gradle via PowerShell with `$env:JAVA_HOME = 'C:\Users\Kiwi PC\.gradle\jdks\jetbrains_s_r_o_-21-amd64-windows.2'` and the tool flag `dangerouslyDisableSandbox: true`. Do NOT use the Android Studio JBR (broken, no `conf/`).
  - Run from `C:\Code\Android\shared-android-lib`: `.\gradlew.bat :shared:testDebugUnitTest :shared:lintDebug --console=plain`.
  - Python locally is `python` (3.14); CI uses `python3`. Scripts must use only the stdlib.
- Commits: write the message to a file with `[IO.File]::WriteAllText(path, text, (New-Object Text.UTF8Encoding($false)))`, then `git commit -F <file>`. End every message with `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- Acceptance: every task returns the exact command(s) it ran and their raw output (last ~15 lines of Gradle, full checker output).

## Review Focus

1. **Unset M3 slots keep the purple baseline.** If `toColorScheme()` leaves `surfaceContainer*`, `secondaryContainer`, `surfaceTint` and the other slots at their defaults, AlertDialog, Card, FilterChip and Snackbar stay purple-grey. All slots get set explicitly, and Task 4 tests them.
2. **A custom accent (for example blue) must re-derive everything accent-dependent**: primary, tertiary, primaryContainer, zones, recording. No red should leak. Task 1 and Task 4 test this with blue.
3. **`lerp` round-off at f = 1.0.** Z5 and `recording` are assigned `accent` directly, never through lerp. Task 1 asserts exact equality.
4. **False positives from the checker** on `.size(48.dp)`, `Color.Transparent` and comment lines. Task 5's fixture test asserts that these are NOT flagged.
5. **Apps still compile against the branch.** Deprecations stay WARNING level, and no public signature is removed. Task 9 builds both apps locally against the working tree.

---
### Task 1: Brand and AppColors (derived accent tokens)

**Files:**
- Create: `shared/src/main/kotlin/com/kevin/shared/ui/theme/Brand.kt`
- Create: `shared/src/main/kotlin/com/kevin/shared/ui/theme/AppColors.kt`
- Test: `shared/src/test/kotlin/com/kevin/shared/ui/theme/BrandTest.kt`

**Interfaces:**
- Produces:
  - `data class Brand(accent: Color = NightDriveRed, onAccent: Color = Color(0xFFFFFFFF))` with `companion object { val Default: Brand }`
  - `val NightDriveRed: Color`
  - `internal fun Brand.toAppColors(): AppColors`
  - `data class AppColors(accentTint, accentSoft, zones: List<Color>, recording, textDisabled, success, warning, highlight, onMedia, mediaScrim, mediaTextShadow: Shadow, scrimStrong, gridLine, tooltipContainer)`
  - `internal val LocalAppColors: ProvidableCompositionLocal<AppColors>`
  - Internal neutral constants (Task 2 uses them): `NdBackground`, `NdSurface`, `NdOutline`, `NdOutlineVariant`, `NdOnSurface`, `NdOnSurfaceVariant`, `NdTextDisabled`, `NdSecondary`, `NdError`, `NdGraphite`, `NdTintBase`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.kevin.shared.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BrandTest {
    private val blue = Color(0xFF2E6BE8)

    @Test fun defaultAccentIsNightDriveRed() {
        assertEquals(Color(0xFFE8202E), Brand.Default.accent)
        assertEquals(Color(0xFFFFFFFF), Brand.Default.onAccent)
    }

    @Test fun zonesFollowTheDocumentedRamp() {
        val zones = Brand.Default.toAppColors().zones
        assertEquals(5, zones.size)
        val graphite = Color(0xFF26262B)
        listOf(0.10f, 0.28f, 0.50f, 0.75f).forEachIndexed { i, f ->
            assertEquals(lerp(graphite, Brand.Default.accent, f), zones[i])
        }
        assertEquals(Brand.Default.accent, zones[4])
    }

    @Test fun recordingAndZ5AreExactlyTheAccent() {
        val colors = Brand(accent = blue).toAppColors()
        assertEquals(blue, colors.recording)
        assertEquals(blue, colors.zones[4])
    }

    @Test fun customAccentRederivesTheRamp() {
        val red = Brand.Default.toAppColors()
        val custom = Brand(accent = blue).toAppColors()
        assertEquals(lerp(Color(0xFF26262B), blue, 0.50f), custom.zones[2])
        assertNotEquals(red.zones[2], custom.zones[2])
        assertEquals(lerp(Color(0xFF0A0A0A), blue, 0.09f), custom.accentTint)
        assertEquals(blue.copy(alpha = 0.40f), custom.accentSoft)
    }

    @Test fun fixedRolesMatchSpec() {
        val c = Brand.Default.toAppColors()
        assertEquals(Color(0xFF55555C), c.textDisabled)
        assertEquals(Color(0xFF3FB27F), c.success)
        assertEquals(Color(0xFFFFB74D), c.warning)
        assertEquals(Color(0xFFFFC850), c.highlight)
        assertEquals(Color.White, c.onMedia)
        assertEquals(Color.Black.copy(alpha = 0.28f), c.mediaScrim)
        assertEquals(Color.Black.copy(alpha = 0.78f), c.scrimStrong)
        assertEquals(Color.White.copy(alpha = 0.07f), c.gridLine)
        assertEquals(Color(0xE60B0B0D), c.tooltipContainer)
        assertEquals(Color.Black.copy(alpha = 0.75f), c.mediaTextShadow.color)
        assertEquals(3f, c.mediaTextShadow.blurRadius)
    }
}
```

- [ ] **Step 2: Run the test and verify it fails**

Run: `.\gradlew.bat :shared:testDebugUnitTest --tests "com.kevin.shared.ui.theme.BrandTest" --console=plain`
Expected: compilation FAIL with "Unresolved reference: Brand".

- [ ] **Step 3: Implement `Brand.kt`**

```kotlin
package com.kevin.shared.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Night Drive default accent. */
val NightDriveRed = Color(0xFFE8202E)

/**
 * The only per-app theme input. Everything accent-dependent (scheme primary/tertiary,
 * accentTint, zone ramp, recording) is derived from [accent]; see toColorScheme()/toAppColors().
 */
@Immutable
data class Brand(
    val accent: Color = NightDriveRed,
    val onAccent: Color = Color(0xFFFFFFFF),
) {
    companion object {
        val Default = Brand()
    }
}

// Night Drive neutrals (spec §4.3). Internal: apps read them through MaterialTheme / AppTheme.colors.
internal val NdBackground = Color(0xFF000000)
internal val NdSurface = Color(0xFF0B0B0D)
internal val NdOutline = Color(0xFF1F1F23)
internal val NdOutlineVariant = Color(0xFF2A2A2F)
internal val NdOnSurface = Color(0xFFF2F2F4)
internal val NdOnSurfaceVariant = Color(0xFF8D8D94)
internal val NdTextDisabled = Color(0xFF55555C)
internal val NdSecondary = Color(0xFFA3A3AB)
internal val NdError = Color(0xFFFF5A4F)
internal val NdGraphite = Color(0xFF26262B)   // zone ramp start
internal val NdTintBase = Color(0xFF0A0A0A)   // accentTint base
```

- [ ] **Step 4: Implement `AppColors.kt`**

```kotlin
package com.kevin.shared.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.lerp

/** Color roles Material 3 has no slot for. Read via AppTheme.colors. */
@Immutable
data class AppColors(
    val accentTint: Color,
    val accentSoft: Color,
    val zones: List<Color>,
    val recording: Color,
    val textDisabled: Color,
    val success: Color,
    val warning: Color,
    val highlight: Color,
    val onMedia: Color,
    val mediaScrim: Color,
    val mediaTextShadow: Shadow,
    val scrimStrong: Color,
    val gridLine: Color,
    val tooltipContainer: Color,
)

private val ZoneMix = listOf(0.10f, 0.28f, 0.50f, 0.75f)

internal fun Brand.toAppColors(): AppColors = AppColors(
    accentTint = lerp(NdTintBase, accent, 0.09f),
    accentSoft = accent.copy(alpha = 0.40f),
    // Z5 is the accent itself, not lerp(…, 1f): lerp goes through Oklab and can drift by one step.
    zones = ZoneMix.map { lerp(NdGraphite, accent, it) } + accent,
    recording = accent,
    textDisabled = NdTextDisabled,
    success = Color(0xFF3FB27F),
    warning = Color(0xFFFFB74D),
    highlight = Color(0xFFFFC850),
    onMedia = Color.White,
    mediaScrim = Color.Black.copy(alpha = 0.28f),
    mediaTextShadow = Shadow(Color.Black.copy(alpha = 0.75f), offset = Offset(0f, 1f), blurRadius = 3f),
    scrimStrong = Color.Black.copy(alpha = 0.78f),
    gridLine = Color.White.copy(alpha = 0.07f),
    tooltipContainer = Color(0xE60B0B0D),
)

internal val LocalAppColors = staticCompositionLocalOf { Brand.Default.toAppColors() }
```

- [ ] **Step 5: Run the test and verify it passes**

Run: `.\gradlew.bat :shared:testDebugUnitTest --tests "com.kevin.shared.ui.theme.BrandTest" --console=plain`
Expected: BUILD SUCCESSFUL, 5 tests passed.

- [ ] **Step 6: Commit**

```powershell
git add shared/src/main/kotlin/com/kevin/shared/ui/theme/Brand.kt shared/src/main/kotlin/com/kevin/shared/ui/theme/AppColors.kt shared/src/test/kotlin/com/kevin/shared/ui/theme/BrandTest.kt
# message: "feat(theme): add Brand and derived AppColors (Night Drive)"
git commit -F <msgfile>
```

### Task 2: Spacing and shape tokens

**Files:**
- Create: `shared/src/main/kotlin/com/kevin/shared/ui/theme/AppSpacing.kt`
- Create: `shared/src/main/kotlin/com/kevin/shared/ui/theme/AppShapes.kt`
- Test: `shared/src/test/kotlin/com/kevin/shared/ui/theme/TokensTest.kt`

**Interfaces:**
- Produces: `data class AppSpacing(xxs, xs, sm, md, lg, xl, xxl: Dp)`, `internal val LocalAppSpacing`, `val AppShapes: Shapes`, `internal val PillShape: RoundedCornerShape`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.kevin.shared.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class TokensTest {
    @Test fun spacingScale() {
        val s = AppSpacing()
        assertEquals(listOf(2.dp, 4.dp, 8.dp, 12.dp, 16.dp, 24.dp, 32.dp),
            listOf(s.xxs, s.xs, s.sm, s.md, s.lg, s.xl, s.xxl))
    }

    @Test fun shapeScale() {
        assertEquals(RoundedCornerShape(8.dp), AppShapes.small)
        assertEquals(RoundedCornerShape(14.dp), AppShapes.medium)
        assertEquals(RoundedCornerShape(20.dp), AppShapes.large)
        assertEquals(RoundedCornerShape(50), PillShape)
    }
}
```

- [ ] **Step 2: Run and verify it fails**

Run: `.\gradlew.bat :shared:testDebugUnitTest --tests "com.kevin.shared.ui.theme.TokensTest" --console=plain`
Expected: compilation FAIL, "Unresolved reference: AppSpacing".

- [ ] **Step 3: Implement `AppSpacing.kt`**

```kotlin
package com.kevin.shared.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Spacing scale. Read via AppTheme.spacing; never write padding/spacedBy/Spacer literals in UI code. */
@Immutable
data class AppSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
)

internal val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }
```

- [ ] **Step 4: Implement `AppShapes.kt`**

```kotlin
package com.kevin.shared.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** small = chips/fields, medium = cards (14dp), large = sheets/dialogs. */
val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
)

/** Buttons, chips, status tags. Exposed as AppTheme.pillShape. */
internal val PillShape = RoundedCornerShape(50)
```

- [ ] **Step 5: Run and verify it passes**

Same command. Expected: BUILD SUCCESSFUL, 2 tests passed.

- [ ] **Step 6: Commit** — message `feat(theme): add spacing and shape tokens`.

### Task 3: Night Drive typography

**Files:**
- Modify: `shared/src/main/kotlin/com/kevin/shared/ui/theme/Typography.kt` (the `AppTypography` block, lines 28-44)
- Test: `shared/src/test/kotlin/com/kevin/shared/ui/theme/TypographyTest.kt`

**Interfaces:**
- Produces: `AppTypography` (unchanged name). `displayLarge` = hero 64sp Light (jump height), `displayMedium` = hero 52sp Light (BPM), `headlineSmall` Regular, `titleMedium` Medium + 0.2em tracking, `labelSmall` Regular 9.5sp + 0.16em tracking. Apps get hero sizes from the scale and never write `fontSize =`.

- [ ] **Step 1: Write the failing test**

```kotlin
package com.kevin.shared.ui.theme

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Test

class TypographyTest {
    private val t = AppTypography

    @Test fun heroNumbersAreLight() {
        assertEquals(64.sp, t.displayLarge.fontSize)
        assertEquals(FontWeight.Light, t.displayLarge.fontWeight)
        assertEquals(52.sp, t.displayMedium.fontSize)
        assertEquals(FontWeight.Light, t.displayMedium.fontWeight)
    }

    @Test fun labelsAndTitlesAreTracked() {
        assertEquals(FontWeight.Normal, t.labelSmall.fontWeight)
        assertEquals(9.5.sp, t.labelSmall.fontSize)
        assertEquals(0.16.em, t.labelSmall.letterSpacing)
        assertEquals(FontWeight.Medium, t.titleMedium.fontWeight)
        assertEquals(0.2.em, t.titleMedium.letterSpacing)
        assertEquals(FontWeight.Normal, t.headlineSmall.fontWeight)
    }

    @Test fun everyStyleUsesSpaceGrotesk() {
        listOf(t.displayLarge, t.bodyMedium, t.labelSmall, t.titleMedium)
            .forEach { assertEquals(SpaceGroteskFamily, it.fontFamily) }
    }
}
```

If the JVM test cannot initialize `Typography.kt` (because of `GoogleFont.Provider`/`R.array`), report the stack trace to the main session. Do not mock or delete the test.

- [ ] **Step 2: Run and verify it fails**

Run: `.\gradlew.bat :shared:testDebugUnitTest --tests "com.kevin.shared.ui.theme.TypographyTest" --console=plain`
Expected: FAIL. `heroNumbersAreLight` expected 64.sp but was 57.sp.

- [ ] **Step 3: Replace the `AppTypography` block**

```kotlin
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
```

Add the imports `androidx.compose.ui.unit.em` and `androidx.compose.ui.unit.sp`.

- [ ] **Step 4: Run and verify it passes.** Expected: 3 tests passed.

- [ ] **Step 5: Commit** — message `feat(theme): Night Drive type scale (light hero numbers, tracked labels)`.

### Task 4: AppTheme rewire and SharedColors deprecation

**Files:**
- Modify: `shared/src/main/kotlin/com/kevin/shared/ui/theme/AppTheme.kt` (full rewrite)
- Modify: `shared/src/main/kotlin/com/kevin/shared/ui/theme/SharedColors.kt` (add `@Deprecated`; values unchanged)
- Test: `shared/src/test/kotlin/com/kevin/shared/ui/theme/ColorSchemeTest.kt`

**Interfaces:**
- Consumes:
  - from Task 1: `Brand`, `toAppColors()`, `LocalAppColors`, the `Nd*` constants
  - from Task 2: `AppSpacing`, `LocalAppSpacing`, `AppShapes`, `PillShape`
  - from Task 3: `AppTypography`
- Produces:
  - `@Composable fun AppTheme(brand: Brand = Brand.Default, content: @Composable () -> Unit)`. Existing `AppTheme { }` callers keep compiling.
  - `object AppTheme { val colors: AppColors; val spacing: AppSpacing; val pillShape: Shape }`
  - `internal fun Brand.toColorScheme(): ColorScheme`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.kevin.shared.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorSchemeTest {
    private val s = Brand.Default.toColorScheme()
    private val red = Color(0xFFE8202E)

    @Test fun specSlots() {
        assertEquals(red, s.primary)
        assertEquals(Color(0xFFFFFFFF), s.onPrimary)
        assertEquals(lerp(Color(0xFF0A0A0A), red, 0.09f), s.primaryContainer)
        assertEquals(Color(0xFFA3A3AB), s.secondary)
        assertEquals(red, s.tertiary)
        assertEquals(Color(0xFFFF5A4F), s.error)
        assertEquals(Color(0xFF000000), s.background)
        assertEquals(Color(0xFF0B0B0D), s.surface)
        assertEquals(Color(0xFF1F1F23), s.surfaceVariant)
        assertEquals(Color(0xFF1F1F23), s.outline)
        assertEquals(Color(0xFF2A2A2F), s.outlineVariant)
        assertEquals(Color(0xFFF2F2F4), s.onBackground)
        assertEquals(Color(0xFFF2F2F4), s.onSurface)
        assertEquals(Color(0xFF8D8D94), s.onSurfaceVariant)
    }

    // Slots M3 components read (dialogs, cards, chips, snackbars) must not fall back to the purple baseline.
    @Test fun noBaselineSlotsLeak() {
        assertEquals(Color.Transparent, s.surfaceTint)
        assertEquals(Color(0xFF2A2A2F), s.secondaryContainer)
        assertEquals(Color(0xFFF2F2F4), s.onSecondaryContainer)
        assertEquals(Color(0xFFF2F2F4), s.onPrimaryContainer)
        assertEquals(Color(0xFF000000), s.surfaceContainerLowest)
        assertEquals(Color(0xFF0B0B0D), s.surfaceContainerLow)
        assertEquals(Color(0xFF0B0B0D), s.surfaceContainer)
        assertEquals(Color(0xFF141417), s.surfaceContainerHigh)
        assertEquals(Color(0xFF1F1F23), s.surfaceContainerHighest)
        assertEquals(Color(0xFFF2F2F4), s.inverseSurface)
        assertEquals(Color(0xFF0B0B0D), s.inverseOnSurface)
    }

    @Test fun customAccentRecolorsAccentSlotsOnly() {
        val blue = Color(0xFF2E6BE8)
        val b = Brand(accent = blue).toColorScheme()
        assertEquals(blue, b.primary)
        assertEquals(blue, b.tertiary)
        assertEquals(lerp(Color(0xFF0A0A0A), blue, 0.09f), b.primaryContainer)
        assertEquals(s.background, b.background)
        assertEquals(s.error, b.error)
    }

    @Test @Suppress("DEPRECATION") fun deprecatedValsKeepOldValues() {
        assertEquals(Color(0xFFB6A6F2), PrimaryPurple)
        assertEquals(Color(0xFF4CAF82), ConnectedGreen)
        assertEquals(5, ZoneColors.size)
    }
}
```

- [ ] **Step 2: Run the test and verify it fails**

Run: `.\gradlew.bat :shared:testDebugUnitTest --tests "com.kevin.shared.ui.theme.ColorSchemeTest" --console=plain`
Expected: compilation FAIL with "Unresolved reference: toColorScheme".

- [ ] **Step 3: Rewrite `AppTheme.kt`**

```kotlin
package com.kevin.shared.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp

/** Every slot is set: unset slots would keep M3's purple baseline (dialogs, chips, snackbars). */
internal fun Brand.toColorScheme(): ColorScheme {
    val tint = lerp(NdTintBase, accent, 0.09f)
    return darkColorScheme(
        primary = accent, onPrimary = onAccent,
        primaryContainer = tint, onPrimaryContainer = NdOnSurface,
        inversePrimary = accent,
        secondary = NdSecondary, onSecondary = NdBackground,
        secondaryContainer = NdOutlineVariant, onSecondaryContainer = NdOnSurface,
        tertiary = accent, onTertiary = onAccent,
        tertiaryContainer = tint, onTertiaryContainer = NdOnSurface,
        background = NdBackground, onBackground = NdOnSurface,
        surface = NdSurface, onSurface = NdOnSurface,
        surfaceVariant = NdOutline, onSurfaceVariant = NdOnSurfaceVariant,
        surfaceTint = Color.Transparent,
        inverseSurface = NdOnSurface, inverseOnSurface = NdSurface,
        error = NdError, onError = NdBackground,
        errorContainer = lerp(NdTintBase, NdError, 0.12f), onErrorContainer = NdOnSurface,
        outline = NdOutline, outlineVariant = NdOutlineVariant,
        scrim = Color.Black,
        surfaceBright = NdOutline, surfaceDim = NdBackground,
        surfaceContainerLowest = NdBackground, surfaceContainerLow = NdSurface,
        surfaceContainer = NdSurface, surfaceContainerHigh = Color(0xFF141417),
        surfaceContainerHighest = NdOutline,
    )
}

/** The one app theme (Night Drive, dark only). Apps may pass their own accent via [brand]. */
@Composable
fun AppTheme(brand: Brand = Brand.Default, content: @Composable () -> Unit) {
    val appColors = remember(brand) { brand.toAppColors() }
    val colorScheme = remember(brand) { brand.toColorScheme() }
    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalAppSpacing provides AppSpacing(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}

/** Token accessors for roles M3 lacks: AppTheme.colors / .spacing / .pillShape (MaterialTheme pattern). */
object AppTheme {
    val colors: AppColors
        @Composable @ReadOnlyComposable get() = LocalAppColors.current
    val spacing: AppSpacing
        @Composable @ReadOnlyComposable get() = LocalAppSpacing.current
    val pillShape: Shape get() = PillShape
}
```

- [ ] **Step 4: Deprecate the vals in `SharedColors.kt`.** The values stay exactly as they are.

```kotlin
package com.kevin.shared.ui.theme

import androidx.compose.ui.graphics.Color

// Pre-Night-Drive palette. Kept with the old values until hr/jump migrate (sub-projects 5/6); removed in sub-project 7.
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.primary", "androidx.compose.material3.MaterialTheme"))
val PrimaryPurple  = Color(0xFFB6A6F2)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.secondary", "androidx.compose.material3.MaterialTheme"))
val SecondaryBlue  = Color(0xFF5BA9E6)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.tertiary", "androidx.compose.material3.MaterialTheme"))
val TertiaryPink   = Color(0xFFF4738E)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.error", "androidx.compose.material3.MaterialTheme"))
val ErrorRed       = Color(0xFFEF4444)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.background", "androidx.compose.material3.MaterialTheme"))
val BackgroundDark = Color(0xFF1D1B20)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.surface", "androidx.compose.material3.MaterialTheme"))
val SurfaceDark    = Color(0xFF252330)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.onPrimary", "androidx.compose.material3.MaterialTheme"))
val OnPrimary      = Color(0xFF0B0912)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.primaryContainer", "androidx.compose.material3.MaterialTheme"))
val LightPurple    = Color(0xFFCBBCFF)
@Deprecated("Use the theme", ReplaceWith("AppTheme.colors.success", "com.kevin.shared.ui.theme.AppTheme"))
val ConnectedGreen = Color(0xFF4CAF82)

@Deprecated("Zones derive from the accent", ReplaceWith("AppTheme.colors.zones", "com.kevin.shared.ui.theme.AppTheme"))
val ZoneColors = listOf(
    Color(0xFF5BA9E6), // Z1
    Color(0xFF88C5EE), // Z2
    Color(0xFFCBBCFF), // Z3
    Color(0xFFE48AD6), // Z4
    Color(0xFFF4738E)  // Z5
)
```

- [ ] **Step 5: Run all lib tests and lint**

Run: `.\gradlew.bat :shared:testDebugUnitTest :shared:lintDebug --console=plain`
Expected: BUILD SUCCESSFUL. Deprecation warnings in `StatItem.kt` and `BleScanComponents.kt` are expected; Task 6 removes them.

- [ ] **Step 6: Commit** — message `feat(theme): Night Drive AppTheme(brand) + AppTheme accessors; deprecate SharedColors`.

### Task 5: Design token checker

**Files:**
- Create: `tools/check_design_tokens.py`
- Create: `tools/test_check_design_tokens.py`
- Modify: `.github/workflows/ci.yml` (job `lib`, after the step "Hardcoded UI strings")

**Interfaces:**
- Produces:
  - CLI: `check_design_tokens.py [--warn] <dir-or-file> [...]`. Output lines look like `path:line: [rule-id] snippet`. Exit code is 1 on findings, or 0 with `--warn`.
  - Python: `check(paths) -> list[tuple[pathlib.Path, int, str, str]]`, a list of (file, line, rule, snippet).
  - Rule ids: `raw-color`, `raw-text-size`, `raw-shape`, `raw-spacing`, `glow`.

- [ ] **Step 1: Write the failing test `tools/test_check_design_tokens.py`**

```python
#!/usr/bin/env python3
"""Fixture tests for check_design_tokens.py. Run: python3 tools/test_check_design_tokens.py"""
import pathlib
import subprocess
import sys
import tempfile
import textwrap
import unittest

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import check_design_tokens  # noqa: E402

SCRIPT = pathlib.Path(__file__).resolve().parent / "check_design_tokens.py"

BAD = textwrap.dedent("""\
    val a = Color(0xFF112233)
    Text("x", color = Color.White)
    Text("x", fontSize = 12.sp)
    Card(shape = RoundedCornerShape(12.dp))
    Modifier.padding(horizontal = 16.dp)
    Spacer(Modifier.height(8.dp))
    Modifier.shadow(8.dp, spotColor = MaterialTheme.colorScheme.primary)
    Modifier.size(48.dp).background(Color.Transparent)
    // Color.White in a comment
    val c = Color.Black // design-ignore: video overlay
    Modifier.padding(AppTheme.spacing.lg)
    Text("x", style = MaterialTheme.typography.labelSmall, maxFontSize = style.fontSize)
""")

EXPECTED = [
    (1, "raw-color"), (2, "raw-color"), (3, "raw-text-size"), (4, "raw-shape"),
    (5, "raw-spacing"), (6, "raw-spacing"), (7, "glow"),
]


class CheckDesignTokensTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        root = pathlib.Path(self.tmp.name)
        self.src = root / "src"
        (self.src / "main/kotlin/app/ui/theme").mkdir(parents=True)
        (self.src / "test/kotlin/app").mkdir(parents=True)
        (self.src / "main/kotlin/app/Bad.kt").write_text(BAD, encoding="utf-8")
        (self.src / "main/kotlin/app/ui/theme/Colors.kt").write_text("val X = Color(0xFF000000)\n", encoding="utf-8")
        (self.src / "test/kotlin/app/BadTest.kt").write_text("val X = Color.White\n", encoding="utf-8")

    def tearDown(self):
        self.tmp.cleanup()

    def test_rules_and_line_numbers(self):
        found = [(line, rule) for _, line, rule, _ in check_design_tokens.check([self.src])]
        self.assertEqual(EXPECTED, found)

    def test_theme_and_test_sources_excluded(self):
        files = {f.name for f, _, _, _ in check_design_tokens.check([self.src])}
        self.assertEqual({"Bad.kt"}, files)

    def test_single_file_argument(self):
        found = check_design_tokens.check([self.src / "main/kotlin/app/Bad.kt"])
        self.assertEqual(len(EXPECTED), len(found))

    def test_exit_codes_and_output_format(self):
        strict = subprocess.run([sys.executable, str(SCRIPT), str(self.src)], capture_output=True, text=True)
        self.assertEqual(1, strict.returncode)
        self.assertRegex(strict.stdout, r"Bad\.kt:1: \[raw-color\] val a = Color\(0xFF112233\)")
        warn = subprocess.run([sys.executable, str(SCRIPT), "--warn", str(self.src)], capture_output=True, text=True)
        self.assertEqual(0, warn.returncode)
        self.assertIn("[raw-shape]", warn.stdout)

    def test_clean_tree_exits_zero(self):
        clean = self.src / "main/kotlin/app/ui/theme"
        result = subprocess.run([sys.executable, str(SCRIPT), str(clean)], capture_output=True, text=True)
        self.assertEqual(0, result.returncode)


if __name__ == "__main__":
    unittest.main()
```

- [ ] **Step 2: Run the test and verify it fails**

Run: `python tools/test_check_design_tokens.py`
Expected: `ModuleNotFoundError: No module named 'check_design_tokens'`.

- [ ] **Step 3: Implement `tools/check_design_tokens.py`**

```python
#!/usr/bin/env python3
"""Flags Compose/Kotlin UI code that bypasses the design tokens (see DESIGN.md).

Rules:
  raw-color      Color(0x...), Color.White/Black/Red/... (Transparent/Unspecified are fine)
  raw-text-size  12.sp literals, fontSize =            -> MaterialTheme.typography.*
  raw-shape      RoundedCornerShape(                   -> MaterialTheme.shapes.* / AppTheme.pillShape
  raw-spacing    literal .dp inside padding( / spacedBy( / Spacer(   -> AppTheme.spacing.*
  glow           .shadow( with primary/accent, BlurMaskFilter, .blur(   (Night Drive: no halos)
Skipped: **/ui/theme/**, src/test, src/androidTest, comment lines, lines containing `// design-ignore`.

Usage: check_design_tokens.py [--warn] <dir-or-file> [...]
  exit 1 on findings; --warn prints the same findings but exits 0.
"""
import argparse
import pathlib
import re
import sys

_RULES = [
    ("raw-color", re.compile(
        r"\bColor\(\s*0x|\bColor\.(?:White|Black|Red|Green|Blue|Yellow|Gray|LightGray|DarkGray|Cyan|Magenta)\b")),
    ("raw-text-size", re.compile(r"\b\d+(?:\.\d+)?\.sp\b|\bfontSize\s*=")),
    ("raw-shape", re.compile(r"\bRoundedCornerShape\(")),
    ("raw-spacing", re.compile(r"\b(?:padding|spacedBy|Spacer)\([^)]*?\b\d+(?:\.\d+)?\.dp\b")),
    ("glow", re.compile(r"\.shadow\([^)]*\b(?:primary|accent)|BlurMaskFilter|\.blur\(")),
]
_EXCLUDED = ("/ui/theme/", "/src/test/", "/src/androidTest/")


def _files(paths):
    for root in map(pathlib.Path, paths):
        candidates = [root] if root.is_file() else sorted(root.rglob("*.kt"))
        for file in candidates:
            if not any(part in "/" + file.as_posix() for part in _EXCLUDED):
                yield file


def check(paths):
    findings = []
    for file in _files(paths):
        for number, line in enumerate(file.read_text(encoding="utf-8-sig").splitlines(), 1):
            if "design-ignore" in line or line.lstrip().startswith(("//", "*", "/*")):
                continue
            for rule, pattern in _RULES:
                if pattern.search(line):
                    findings.append((file, number, rule, line.strip()))
    return findings


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--warn", action="store_true", help="report findings but exit 0")
    parser.add_argument("paths", nargs="+")
    args = parser.parse_args(argv)
    results = check(args.paths)
    for file, number, rule, snippet in results:
        print(f"{file}:{number}: [{rule}] {snippet}")
    if not results:
        print("No design token violations found.")
        return 0
    print(f"\n{len(results)} design token violation(s). Use MaterialTheme / AppTheme tokens (DESIGN.md) "
          "or mark a deliberate exception with // design-ignore: <reason>.")
    return 0 if args.warn else 1


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
```

- [ ] **Step 4: Run the test and verify it passes**

Run: `python tools/test_check_design_tokens.py`
Expected: `Ran 5 tests ... OK`.

- [ ] **Step 5: Produce the migration list for Task 6**

Run: `python tools/check_design_tokens.py --warn shared/src/main`
Expected: exit 0, with findings only in `ui/StatItem.kt`, `ui/scan/BleScanComponents.kt`, `ui/zone/ZoneComponents.kt`, `ui/session/SessionComponents.kt`, `ui/session/TrashComponents.kt` and `ui/LabelPickerDialog.kt`. Paste the full output into the task report.

- [ ] **Step 6: Add the test step to CI.** In `.github/workflows/ci.yml`, job `lib`, insert after the "Hardcoded UI strings" step:

```yaml
      - name: Design token checker tests
        run: python3 tools/test_check_design_tokens.py
```

(The strict `check_design_tokens.py shared/src/main` step is added in Task 7, once the lib is clean.)

- [ ] **Step 7: Commit** — message `feat(tools): design token checker + fixture tests in CI`.

### Value mapping for Tasks 6 and 7 (binding)

| Literal | Token |
|---|---|
| `2.dp` / `4.dp` / `8.dp` / `12.dp` / `16.dp` / `24.dp` / `32.dp` in padding, spacedBy or Spacer | `AppTheme.spacing.xxs` / `xs` / `sm` / `md` / `lg` / `xl` / `xxl` |
| `6.dp` (off-scale) | `AppTheme.spacing.sm` |
| `10.dp` (off-scale) | `AppTheme.spacing.md` |
| `RoundedCornerShape(12.dp)` on a Card | `MaterialTheme.shapes.medium` |
| `SurfaceDark` | `MaterialTheme.colorScheme.surface` |
| `ConnectedGreen` | `AppTheme.colors.success` |
| `Color.White` as a text/label color | `MaterialTheme.colorScheme.onSurface` |
| `Color.Gray` as a fallback | `MaterialTheme.colorScheme.outlineVariant` |
| `size(…dp)`, `BorderStroke(1.dp, …)` | unchanged (these are not spacing, and the checker doesn't flag them) |

Composable default parameters may read theme values (`valueColor: Color = MaterialTheme.colorScheme.onSurface`). **Do not change any parameter name, type or order.** The apps call these functions.

### Task 6: Migrate StatItem, BleScanComponents and ZoneComponents to tokens

**Files:**
- Modify: `shared/src/main/kotlin/com/kevin/shared/ui/StatItem.kt`
- Modify: `shared/src/main/kotlin/com/kevin/shared/ui/scan/BleScanComponents.kt`
- Modify: `shared/src/main/kotlin/com/kevin/shared/ui/zone/ZoneComponents.kt`

**Interfaces:**
- Consumes: `AppTheme.colors.success`, `AppTheme.colors.zones`, `AppTheme.spacing.*` (Task 4), `MaterialTheme.shapes.medium` (Task 2).
- Produces: the same public signatures, with new default values:
  - `StatItem(valueColor = MaterialTheme.colorScheme.onSurface, containerColor = MaterialTheme.colorScheme.surface)`
  - `TargetZoneDialog(zoneColors = AppTheme.colors.zones)`

- [ ] **Step 1: Confirm the findings (the failing check)**

Run: `python tools/check_design_tokens.py shared/src/main/kotlin/com/kevin/shared/ui/StatItem.kt shared/src/main/kotlin/com/kevin/shared/ui/scan/BleScanComponents.kt shared/src/main/kotlin/com/kevin/shared/ui/zone/ZoneComponents.kt`
Expected: exit 1, with findings including StatItem.kt:28 [raw-color], :34 [raw-shape], :41 [raw-spacing], :56/:58 [raw-text-size]; BleScanComponents.kt:67/68/73/80/94/102/104/136/139/141; ZoneComponents.kt:28/31/38.

- [ ] **Step 2: Edit `StatItem.kt`**

```kotlin
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(vertical = AppTheme.spacing.md, horizontal = AppTheme.spacing.sm),
```

And in the autoSize block:

```kotlin
                    minFontSize = 14.sp, // design-ignore: autosize floor, no type-scale equivalent
                    maxFontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    stepSize = 0.5.sp // design-ignore: autosize step
```

Imports: remove `RoundedCornerShape`, `dp` and `SurfaceDark`; add `com.kevin.shared.ui.theme.AppTheme`. Keep `sp`.

- [ ] **Step 3: Edit `BleScanComponents.kt`**

| Line | Before | After |
|---|---|---|
| 50 | `ConnectedGreen` | `AppTheme.colors.success` |
| 67 | `.padding(end = 8.dp)` | `.padding(end = AppTheme.spacing.sm)` |
| 68, 102, 136 | `RoundedCornerShape(12.dp)` | `MaterialTheme.shapes.medium` |
| 73 | `padding(horizontal = 12.dp, vertical = 8.dp)` | `padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.sm)` |
| 80 | `Spacer(Modifier.width(8.dp))` | `Spacer(Modifier.width(AppTheme.spacing.sm))` |
| 94 | `Spacer(Modifier.width(4.dp))` | `Spacer(Modifier.width(AppTheme.spacing.xs))` |
| 104 | `padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp)` | `padding(start = AppTheme.spacing.lg, top = AppTheme.spacing.sm, bottom = AppTheme.spacing.sm, end = AppTheme.spacing.xs)` |
| 139 | `padding(horizontal = 16.dp, vertical = 12.dp)` | `padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.md)` |
| 141 | `spacedBy(12.dp)` | `spacedBy(AppTheme.spacing.md)` |

Imports: replace `com.kevin.shared.ui.theme.ConnectedGreen` with `com.kevin.shared.ui.theme.AppTheme`. Drop `RoundedCornerShape`. Keep `dp` if `size(8.dp)`/`BorderStroke(1.dp…)` still use it.

- [ ] **Step 4: Edit `ZoneComponents.kt`**

```kotlin
    zoneColors: List<Color> = AppTheme.colors.zones
) {
    …
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                (1..zoneCount).forEach { z ->
                    val selected = targetZone == z
                    val zoneColor = zoneColors.getOrElse(z - 1) { MaterialTheme.colorScheme.outlineVariant }
                    …
                            selectedContainerColor = zoneColor,
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface
```

Imports: add `androidx.compose.material3.MaterialTheme` and `com.kevin.shared.ui.theme.AppTheme`. Remove `dp` if it is unused.

- [ ] **Step 5: Verify the check is clean, then run tests and lint**

Run: `python tools/check_design_tokens.py shared/src/main/kotlin/com/kevin/shared/ui/StatItem.kt shared/src/main/kotlin/com/kevin/shared/ui/scan/BleScanComponents.kt shared/src/main/kotlin/com/kevin/shared/ui/zone/ZoneComponents.kt`
Expected: `No design token violations found.`, exit 0.
Run: `.\gradlew.bat :shared:testDebugUnitTest :shared:lintDebug --console=plain`
Expected: BUILD SUCCESSFUL, with no deprecation warnings from these three files.

- [ ] **Step 6: Commit** — message `refactor(ui): StatItem, BLE scan and zone dialog on design tokens`.

### Task 7: Migrate SessionComponents, TrashComponents and LabelPickerDialog; make the lib checker strict

**Files:**
- Modify: `shared/src/main/kotlin/com/kevin/shared/ui/session/SessionComponents.kt`
- Modify: `shared/src/main/kotlin/com/kevin/shared/ui/session/TrashComponents.kt`
- Modify: `shared/src/main/kotlin/com/kevin/shared/ui/LabelPickerDialog.kt`

**Interfaces:** the same public signatures; only internals change.

- [ ] **Step 1: Confirm the findings**

Run: `python tools/check_design_tokens.py shared/src/main`
Expected: exit 1, with findings only in these three files (SessionComponents.kt:29/30/58/100/105, TrashComponents.kt:50/68/71/86/113, LabelPickerDialog.kt:40/46/52/70/81, plus any the grep missed. The checker output is authoritative).

- [ ] **Step 2: Apply the value mapping table to every finding.** Concretely:
  - `padding(12.dp)` → `padding(AppTheme.spacing.md)`
  - `padding(horizontal = 12.dp, vertical = 8.dp)` → `padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.sm)`
  - `spacedBy(8.dp)` → `spacedBy(AppTheme.spacing.sm)`
  - `spacedBy(6.dp)` → `spacedBy(AppTheme.spacing.sm)`
  - `Spacer(Modifier.height(8.dp))` → `Spacer(Modifier.height(AppTheme.spacing.sm))`
  - `Spacer(Modifier.height(16.dp))` → `Spacer(Modifier.height(AppTheme.spacing.lg))`
  - `padding(end = 8.dp)` → `padding(end = AppTheme.spacing.sm)`
  - `padding(vertical = 4.dp)` → `padding(vertical = AppTheme.spacing.xs)`

  Leave `Modifier.size(32.dp)` unchanged. Add `import com.kevin.shared.ui.theme.AppTheme`, and drop `dp` imports that become unused.

- [ ] **Step 3: Verify the whole lib is clean**

Run: `python tools/check_design_tokens.py shared/src/main`
Expected: `No design token violations found.`, exit 0.

- [ ] **Step 4: Run tests and lint**

Run: `.\gradlew.bat :shared:testDebugUnitTest :shared:lintDebug --console=plain`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Make CI strict.** In `.github/workflows/ci.yml`, job `lib`, after "Design token checker tests":

```yaml
      - name: Design tokens
        run: python3 tools/check_design_tokens.py shared/src/main
```

- [ ] **Step 6: Commit** — message `refactor(ui): session, trash and label picker on design tokens; strict token check in CI`.

### Task 8: DESIGN.md, CHANGELOG, version 0.4.0

**Files:**
- Create: `DESIGN.md` (lib root)
- Modify: `CHANGELOG.md` (fold the `[Unreleased]` section into `[0.4.0] — 2026-10-12`)
- Modify: `shared/build.gradle.kts:10` (`version = "0.3.2"` → `version = "0.4.0"`)

**Interfaces:** consumes every token name from Tasks 1–4 and the checker CLI from Task 5. The doc must use exactly these names.

- [ ] **Step 1: Write `DESIGN.md`** with these ten sections, following spec §4.11. Content per section:
  1. **Principles.** Copy the six Night Drive rules from spec §4.1 verbatim. Add the scope rule: "Generic UI belongs in this lib even if only one app uses it today (replaces the 0.3.0 'both apps' rule)."
  2. **Color roles.** Two tables:
     - `MaterialTheme.colorScheme` slots with their hex values, from Task 4's `toColorScheme()`
     - `AppTheme.colors` roles with their values or derivation formulas, from Task 1
  3. **Accent usage.**
     - Allowed: primary action, active/selected state, live value marker, target zone, recording.
     - Do: 1dp accent border + `AppTheme.colors.accentTint` fill; gradient divider transparent → `accentSoft` → transparent.
     - Don't: halos, `.shadow` in the accent color, blur, accent on more than one element per region, color as the only error signal.
  4. **Spacing.** A table of `AppTheme.spacing.xxs…xxl` = 2/4/8/12/16/24/32 dp.
  5. **Shapes.** `MaterialTheme.shapes.small/medium/large` = 8/14/20 dp, and `AppTheme.pillShape` for buttons, chips and tags.
  6. **Typography.** Table of the styles changed in Task 3. Hero numbers use `displayLarge` (64sp, jump height) and `displayMedium` (52sp, BPM). Labels use `labelSmall` plus `.uppercase()`. Never `fontSize =`.
  7. **Brand.** Show `AppTheme(brand = Brand(accent = Color(0xFF2E6BE8))) { … }`. Everything accent-dependent is derived from it.
  8. **Component catalog.** List the current lib components (StatItem, BLE scan bar and cards, session/trash components, SelectionHeader, TargetZoneDialog, LabelPickerDialog, ChartToggleButton). Add the note "grows with sub-projects 2–4".
  9. **Checker.**
     - Rule table from Task 5, with the usage line.
     - `// design-ignore: <reason>` (always with a reason).
     - `--warn` for apps until their redesign.
     - CI runs it strict on `shared/src/main`.
  10. **Mockups.** Link to `docs/design/mockups/00-final-screens.html` (binding) and `docs/design/mockups/README.md`.

- [ ] **Step 2: Bump the version** in `shared/build.gradle.kts:10` to `version = "0.4.0"`.

- [ ] **Step 3: CHANGELOG.** Rename `## [Unreleased]` to `## [0.4.0] — 2026-10-12` and keep its existing release-tag entry. Add:

```markdown
### Added
- Night Drive design system: `Brand` (configurable accent, default red `#E8202E`), `AppTheme(brand)`, `AppTheme.colors` (`AppColors`: accentTint/accentSoft, accent-derived zone ramp, recording, success/warning/highlight, media and chart roles), `AppTheme.spacing` (2–32dp), `MaterialTheme.shapes` 8/14/20dp + `AppTheme.pillShape`, Night Drive type scale (light hero numbers, tracked labels)
- `DESIGN.md`: rules, tokens, accent usage, component catalog
- `tools/check_design_tokens.py` (+ fixture tests), strict in lib CI; apps can run it with `--warn`

### Changed
- **Visual:** the theme is now Night Drive (black background, graphite neutrals, red accent). Every M3 color slot is set explicitly. Theme-driven UI in the apps changes immediately; app-hardcoded colors stay until the app redesigns
- Scope rule: generic UI belongs in the lib even if only one app uses it today
- Lib components use tokens (StatItem/TargetZoneDialog defaults now come from the theme)

### Deprecated
- `SharedColors.kt` vals (`PrimaryPurple`, `ZoneColors`, …) keep their old values; use `MaterialTheme.colorScheme` / `AppTheme.colors`. Removal planned after both app redesigns
```

- [ ] **Step 4: Verify**

Run: `python tools/check_design_tokens.py shared/src/main; python tools/test_check_design_tokens.py; .\gradlew.bat :shared:testDebugUnitTest :shared:lintDebug --console=plain`
Expected: exit 0 / OK / BUILD SUCCESSFUL.

- [ ] **Step 5: Commit** — message `docs: DESIGN.md, CHANGELOG 0.4.0, bump version`.

### Task 9: Branch verification (@qa-verifier, read-only)

**Files:** none modified.

- [ ] **Step 1: Run the lib gate**
  - `python tools/test_check_design_tokens.py` → OK
  - `python tools/check_design_tokens.py shared/src/main` → exit 0
  - `.\gradlew.bat :shared:testDebugUnitTest :shared:lintDebug --console=plain` → BUILD SUCCESSFUL

- [ ] **Step 2: Build both apps against the working tree.** Their `includeBuild` points at `C:\Code\Android\shared-android-lib`. Do not modify or commit anything in the app repos.
  - hr: in `C:\Code\Android\hr_tracker_app\repo\code`, run `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest --console=plain` → BUILD SUCCESSFUL. Deprecation warnings are allowed.
  - jump: in `C:\Code\Android\jump_tracker_app\code`, run the same command → BUILD SUCCESSFUL.
  - Report every *error* verbatim. Report the count of deprecation warnings per app.

- [ ] **Step 3: Check the spec**
  - every §5 row has evidence
  - `git diff master --stat` touches only the files named in Tasks 1–8, plus the plan and spec docs

- [ ] **Step 4: User-only check (not claimable by agents).** Install both debug apps. M3 defaults should look black and red, and no text should be unreadable on black.

## Notes for later sub-projects (from planning, 2026-10-12)

- **How the apps consume the lib.** They use `includeBuild` from `code/settings.gradle.kts` (`sharedLibPath` in `code/local.properties`). The CI pin is the SHA in `code/shared-app-lib.ref`, not a version. "The app pins 0.3.x" therefore means: don't bump `shared-app-lib.ref` until that app's redesign PR. Locally the apps always compile this lib's working tree.
- **Parallel lib sub-projects 2/3/4.**
  - Stack them on `design-system-foundation`, each on its own branch or worktree.
  - Version and CHANGELOG headings are assigned at merge time (0.5.0, 0.6.0, 0.7.0 in merge order) to avoid conflicts.
- **hr repo.**
  - The git repo is `C:\Code\Android\hr_tracker_app\repo`, currently on `feat/hrv-quality-rating`.
  - Plans and handover go in the workspace `C:\Code\Android\hr_tracker_app\docs\`, not in `repo/`.
  - Scan's battery value needs new BLE Battery Service (0x180F / 0x2A19) read code. Show the name only when the service is absent.
- **jump repo.** `C:\Code\Android\jump_tracker_app` `main` has 2 unpushed commits and an untracked `test_output.log`; don't touch them.
- **Merging and pushing** to `master` happen only with the user's go-ahead.

## Self-review (done 2026-10-12)

- Coverage, spec section → task:
  - §4.2 → T1
  - §4.3 → T4
  - §4.4 → T1
  - §4.5 / §4.6 → T2
  - §4.7 → T3
  - §4.8 / §4.9 → T4
  - §4.10 → T6, T7. `SelectionHeader` and `ChartToggleButton` have no checker findings; they stay as they are unless the checker flags them.
  - §4.11 / §4.13 → T8
  - §4.12 → T5, T7
  - §5 → T1–T5, T9
- Spec amendments made with this plan:
  - §4.7 now states the hero sizes (displayLarge 64sp, displayMedium 52sp)
  - §4.3 states that all remaining M3 slots are set explicitly
  - §6 rewords the pin as `shared-app-lib.ref`
