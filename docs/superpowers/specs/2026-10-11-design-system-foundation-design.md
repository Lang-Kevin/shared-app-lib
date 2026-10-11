# Design System Foundation — Design Spec

Date: 2026-10-11 · Status: draft, awaiting review · Target lib version: 0.4.0

## 1. Context

hr-tracker and jump-tracker already take their whole theme from this lib (`AppTheme`, `SharedColors.kt`, `Typography.kt`). Neither app has its own Theme, Color, Type or Shape file. The shared theme is thin, though:

- the color scheme is dark only, built from raw top-level color vals
- there are no spacing or shape tokens

The apps work around it constantly:

| Smell | hr-tracker | jump-tracker |
|---|---|---|
| `Color.White` as the text color | ~30× | ~40× |
| Inline `Color(0x…)` | 4 | 5+ |
| Hardcoded dp/sp literals | ~56 | ~92 |
| Shared color vals passed directly into component params | yes | yes |

Generic components and formatters are duplicated between the apps or exist in only one of them.

## 2. Goals

1. The lib becomes a **reusable design system for current and future apps**. Generic UI belongs in the lib even if only one app uses it today. This replaces the 0.3.0 rule "a feature moves into the lib only when both apps use it".
2. Apps get **one brandable, dark-only theme**. `AppTheme(brand = …)` swaps the accent colors, and everything else is shared.
3. **Design rules are written down and enforced**: `DESIGN.md` plus a CI checker script.
4. Tokens are exposed through **M3 plus extended CompositionLocals**:
   - standard roles go through `MaterialTheme.*`
   - everything M3 lacks goes through `AppTheme.colors` and `AppTheme.spacing`

### Non-goals (this sub-project)

- Light mode and dynamic color.
- Migrating app code to the tokens. That is sub-project 2.
- New components, formatters or chart code. Those are sub-projects 3 to 5.
- Changing the typography scale.

## 3. Roadmap (each item gets its own spec, plan and release)

| # | Sub-project | Depends on |
|---|---|---|
| **1** | **Foundation: tokens, Brand, DESIGN.md, checker** (this spec) | — |
| 2 | App migration to tokens in both apps; checker turned on in app CI; deprecated vals removed | 1 |
| 3 | Basics and dialogs: PrimaryButton, EmptyState, Metric/StatItem merge, TextInputDialog, SingleChoiceDialog, ProgressDialog, NumberField, SwipeToDelete, DateRangeFilterDialog. Also formatters (duration, clock, date, number, units) and `UiText` | 1 |
| 4 | Chart toolkit: ChartViewport, zoom/pan gesture, ticks and axis, scrubber, TrendChart (from hr) | 1, 3 |
| 5 | Overlays and media chrome: tutorial spotlight, permission gate, pill/scrim, ShutterButton | 1 |

## 4. Design

All new files live in `shared/src/main/kotlin/com/kevin/shared/ui/theme/`.

### 4.1 `Brand.kt`

```kotlin
@Immutable
data class Brand(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val tertiary: Color,
) {
    companion object {
        // Private raw constants holding the same values as today's SharedColors.kt
        // vals (see 4.6), so the theme doesn't reference deprecated API.
        val Default = Brand(
            primary = PrimaryPurple,
            onPrimary = OnPrimary,
            primaryContainer = LightPurple,
            secondary = SecondaryBlue,
            tertiary = TertiaryPink,
        )
    }
}
```

`Brand.Default` uses exactly the current values, so introducing it causes no visual change. Apps define their own `Brand` instances in app code.

### 4.2 `AppColors.kt`: roles that M3 doesn't have

`@Immutable data class AppColors` with a default instance built from values already used in the apps:

| Token | Value | Replaces |
|---|---|---|
| `success` | current `ConnectedGreen` | `ConnectedGreen`, jump phase green `0xFF2E7D32` |
| `warning` | `0xFFFFB74D` | hr `CautionOrange` |
| `highlight` | `0xFFFFC850` | hr milestone line (2×) |
| `recording` | `0xFFE53935` | jump `Color.Red` recording dot, hr gap line, jump phase red `0xFFC62828` |
| `onMedia` | `Color.White` | white text and icons on camera or video |
| `mediaScrim` | `Color(0x80000000)` | jump pill background |
| `scrimStrong` | `Color.Black.copy(alpha = 0.78f)` | hr tutorial scrim |
| `overlaySubtle` | `Color.White.copy(alpha = 0.10f)` | hr chart bands |
| `gridLine` | `Color.White.copy(alpha = 0.08f)` | hr chart grid |
| `tooltipContainer` | `Color(0xE6202030)` | hr chart tooltip |
| `zones` | current `ZoneColors` (5) | `ZoneColors` |

Text on surfaces uses `MaterialTheme.colorScheme.onSurface` or `onBackground`, not `Color.White`. Errors use `colorScheme.error`.

### 4.3 `AppSpacing.kt`

`@Immutable data class AppSpacing(xxs = 2.dp, xs = 4.dp, sm = 8.dp, md = 12.dp, lg = 16.dp, xl = 24.dp, xxl = 32.dp)`.

### 4.4 Shapes

`AppShapes = Shapes(small = RoundedCornerShape(8.dp), medium = RoundedCornerShape(12.dp), large = RoundedCornerShape(16.dp))`, set on `MaterialTheme`. A separate pill shape (`RoundedCornerShape(50)`) is available as `AppTheme.pillShape`.

### 4.5 `AppTheme.kt`

```kotlin
@Composable
fun AppTheme(brand: Brand = Brand.Default, content: @Composable () -> Unit) {
    val scheme = darkColorScheme(
        primary = brand.primary, onPrimary = brand.onPrimary,
        primaryContainer = brand.primaryContainer,
        secondary = brand.secondary, tertiary = brand.tertiary,
        background = BackgroundDark, surface = SurfaceDark, error = ErrorRed,
        // other slots exactly as today
    )
    CompositionLocalProvider(
        LocalAppColors provides AppColors.Default,
        LocalAppSpacing provides AppSpacing(),
    ) {
        MaterialTheme(colorScheme = scheme, typography = AppTypography, shapes = AppShapes, content = content)
    }
}

object AppTheme {
    val colors: AppColors @Composable @ReadOnlyComposable get() = LocalAppColors.current
    val spacing: AppSpacing @Composable @ReadOnlyComposable get() = LocalAppSpacing.current
    val pillShape: Shape get() = PillShape
}
```

A top-level function and an object with the same name is legal Kotlin; `MaterialTheme` uses the same pattern. Existing callers of `AppTheme { … }` keep compiling unchanged.

The color-scheme construction is pulled into a pure function, `internal fun Brand.toColorScheme(): ColorScheme`, so it can be unit-tested.

### 4.6 Deprecating `SharedColors.kt`

The top-level vals stay, marked `@Deprecated(level = WARNING)` with a `ReplaceWith` that points to their replacement:

| Val | Replacement |
|---|---|
| `PrimaryPurple` | `MaterialTheme.colorScheme.primary` |
| `ConnectedGreen` | `AppTheme.colors.success` |
| `ZoneColors` | `AppTheme.colors.zones` |
| `OnPrimary` | `MaterialTheme.colorScheme.onPrimary` |
| `LightPurple` | `MaterialTheme.colorScheme.primaryContainer` |
| `SecondaryBlue` | `MaterialTheme.colorScheme.secondary` |
| `TertiaryPink` | `MaterialTheme.colorScheme.tertiary` |
| `ErrorRed` | `MaterialTheme.colorScheme.error` |
| `BackgroundDark` | `MaterialTheme.colorScheme.background` |
| `SurfaceDark` | `MaterialTheme.colorScheme.surface` |

The theme files reference the raw values through private constants, so they don't trigger the deprecation themselves. Sub-project 2 removes the vals.

### 4.7 Migrating the lib's own components

The lib's composables are switched to tokens so the lib passes its own checker:

- `StatItem`
- `scan/BleScanComponents`
- `session/*`
- `selection/SelectionHeader`
- `zone/ZoneComponents`
- `LabelPickerDialog`
- `chart/ChartToggleButton`

The replacements are:
- `RoundedCornerShape(12.dp)` → `MaterialTheme.shapes.medium`
- literal paddings → `AppTheme.spacing.*`
- deprecated vals → scheme roles or `AppTheme.colors`

The visual result must stay identical. All spacing values used today (4, 8, 12, 16) map 1:1 to tokens.

### 4.8 `DESIGN.md` (lib root)

Sections:
1. Principles and scope rule
2. Color roles (M3 plus `AppColors`, with a table of values)
3. Spacing scale
4. Shapes
5. Typography (use `MaterialTheme.typography.*`, never `fontSize =`)
6. How to define a `Brand`
7. Do/Don't list
8. Component catalog (current lib components; later sub-projects add to it)
9. The checker and `// design-ignore`

### 4.9 Checker: `tools/check_design_tokens.py`

Same style and CLI shape as `tools/check_hardcoded_strings.py`. It scans `*.kt` under the given paths, skips `**/ui/theme/**` and test sources, and flags:

| Rule id | Pattern |
|---|---|
| `raw-color` | `Color(0x…)`, `Color.White/Black/Red/Green/Blue/Yellow/Gray/…` (`Color.Transparent` and `Color.Unspecified` are allowed) |
| `raw-text-size` | `\d+(\.\d+)?\.sp`, `fontSize =` |
| `raw-shape` | `RoundedCornerShape(` |
| `raw-spacing` | a literal `\d+\.dp` inside `padding(`, `spacedBy(` or `Spacer(` size modifiers |

- A line ending in `// design-ignore` is skipped.
- Output is `path:line: [rule-id] snippet`, and the exit code is 1 if anything was found.
- `--warn` prints the same output but exits 0. Apps run in this mode until sub-project 2.
- The lib CI (`.github/workflows/ci.yml`, job `lib`) runs it in strict mode on `shared/src/main`.

### 4.10 Release

- Bump the version in `shared/build.gradle.kts` from `0.3.2` to `0.4.0`. The changes are additive plus deprecations; no breaking removals.
- Add a CHANGELOG entry, including the new scope rule.
- `release-tag.yml` creates `v0.4.0` on merge.

## 5. Testing and verification

| Check | Evidence |
|---|---|
| Unit: `Brand.Default.toColorScheme()` slots equal the pre-change scheme values (primary, secondary, tertiary, background, surface, error, onPrimary, primaryContainer) | `:shared:testDebugUnitTest` |
| Unit: `AppColors.Default` and `AppSpacing()` values match this spec | same |
| Checker fixture test: a bad fixture file produces the expected rule ids and line numbers; `// design-ignore` is honored; the `ui/theme/` path is excluded; `--warn` exits 0 | `python tools/test_check_design_tokens.py` (run in CI) |
| The lib passes its own checker | `python tools/check_design_tokens.py shared/src/main` exits 0 |
| Lint is green | `:shared:lintDebug` |
| Both apps still build against the branch (deprecation warnings only) | CI job `apps`. Check beforehand that neither app sets `allWarningsAsErrors` |
| No visual change | Manual check: run both apps and compare key screens |

## 6. Risks

- **Deprecation warnings break an app build** if it treats warnings as errors. Mitigation: check before merging. Fallback: no `@Deprecated` until sub-project 2.
- **Collapsing the reds** (`recording`, which merges `Color.Red`, `0xFFC62828` and `0xFFE53935`) is a small visual change in jump-tracker. It is accepted, but it only lands in sub-project 2 when the apps migrate, not in this sub-project.
- **Checker false positives** on `.dp` in non-spacing contexts (sizes, strokes). Mitigation: `raw-spacing` only fires inside padding, `spacedBy` and `Spacer` contexts; everything else uses `// design-ignore`.
