# Design System Foundation + "Night Drive" Redesign — Design Spec

Date: 2026-10-11 (rev. 2026-10-12) · Status: draft, awaiting review · Target lib version: 0.4.0

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

Both apps also get a **visual redesign**. It is modelled on the interior of an Audi car (not the website): simple and elegant, with red accents. The direction is called **"Night Drive"**. It was chosen and refined in browser mockups between 2026-10-11 and 2026-10-12; see §7 and `docs/design/mockups/`.

## 2. Goals

1. The lib becomes a **reusable design system for current and future apps**. Generic UI belongs in the lib even if only one app uses it today. This replaces the 0.3.0 rule "a feature moves into the lib only when both apps use it".
2. Apps get **one brandable, dark-only theme**. `AppTheme(brand = …)` swaps the accent color. Everything else is shared, and that includes the zone ramp, which is derived from the accent.
3. The theme implements the **Night Drive visual language** (§4.1), so the look is owned by the lib and not by each app.
4. **Design rules are written down and enforced**: `DESIGN.md` plus a CI checker script.
5. Tokens are exposed through **M3 plus extended CompositionLocals**:
   - standard roles go through `MaterialTheme.*`
   - everything M3 lacks goes through `AppTheme.colors` and `AppTheme.spacing`

### Non-goals (sub-project 1)

- Light mode and dynamic color.
- Migrating and redesigning app screens. Those are sub-projects 5 and 6.
- New components, formatters or chart code. Those are sub-projects 2 to 4.

## 3. Roadmap (each item gets its own spec section or spec, plan and release)

| # | Sub-project | Depends on | Team |
|---|---|---|---|
| **1** | **Foundation: Night Drive tokens, Brand, typography, DESIGN.md, checker** (this spec, §4) | — | lib |
| 2 | Basics and dialogs in Night Drive style:<br>• components: PrimaryButton/SecondaryButton, StatItem/Metric merge, EmptyState, TextInputDialog, SingleChoiceDialog, ProgressDialog, NumberField, SwipeToDelete, DateRangeFilterDialog, SegmentedControl, ListCard, TabRow styling<br>• formatters (duration, clock, date, number, units)<br>• `UiText` | 1 | lib |
| 3 | Chart toolkit: ChartViewport, zoom/pan, ticks and axis, scrubber, zone bands from the accent ramp, GOAL badge and target band, TrendChart (from hr), time-in-zone bar | 1, 2 | lib |
| 4 | Overlays and media chrome: camera HUD (the Record A2 layout, §7.3), text-on-video style, ShutterButton, framing guide, tutorial spotlight, permission gate | 1 | lib |
| 5 | **hr-tracker**: migrate to tokens, adopt the lib components and redesign all screens (§7.2). Checker enabled in app CI | 1–4 (released) | hr |
| 6 | **jump-tracker**: migrate to tokens, adopt the lib components and redesign all screens (§7.3). Checker enabled in app CI | 1–4 (released) | jump |
| 7 | Remove the deprecated `SharedColors.kt` vals | 5, 6 | lib |

2, 3 and 4 can run in parallel after 1, and so can 5 and 6 after 2–4. Each lib sub-project ships its own minor release (0.4.0, 0.5.0, …) so the app teams pin real versions.

## 4. Sub-project 1 design

All new files live in `shared/src/main/kotlin/com/kevin/shared/ui/theme/`.

### 4.1 Night Drive visual language (binding rules)

- **Cabin:** a pure black background, near-black surfaces and graphite hairlines. No elevation shadows.
- **The accent is rare.** It marks the one thing that matters on a screen: the primary action, the active/selected state, the live value marker, the target zone and the recording state. Everything else is white, grey or graphite.
- **Subtle glow only.** An accent element is drawn as a **1dp accent border plus a 9% accent tint** fill. Section dividers may use a 1dp horizontal gradient line (transparent → accent at 40% → transparent). **No halos, blur shadows or outer glows.**
- **Numbers are the heroes.** Primary readings (BPM, jump height) use a large Light (300) weight. Labels are small, uppercase and widely tracked.
- **Pills** are used for buttons, chips and status tags. Cards use a 14dp radius.
- **Over video**, text sits directly on the image with a text shadow and no boxes. Scrims are at most 25–30% and only at the edges.

### 4.2 `Brand.kt`

```kotlin
@Immutable
data class Brand(
    val accent: Color = NightDriveRed,          // 0xFFE8202E
    val onAccent: Color = Color(0xFFFFFFFF),
)
```

- `Brand.Default = Brand()` (red).
- Apps may pass their own accent (`AppTheme(brand = Brand(accent = …))`). Both apps keep the red default for now.
- Everything accent-dependent is **derived** in `Brand.toAppColors()` and `Brand.toColorScheme()`, which are pure, `internal` and unit-tested:

| Derived | Formula |
|---|---|
| `accentTint` | `lerp(Color(0xFF0A0A0A), accent, 0.09f)` |
| `accentSoft` | `accent.copy(alpha = 0.40f)` |
| `zones[0..4]` | `lerp(Graphite 0xFF26262B, accent, f)` for f = 0.10, 0.28, 0.50, 0.75, 1.00 |
| `recording` | `accent` |

### 4.3 Neutrals and `ColorScheme`

| Role | Value | Usage |
|---|---|---|
| `background` | `0xFF000000` | screens |
| `surface` | `0xFF0B0B0D` | cards |
| `surfaceVariant` / `outline` | `0xFF1F1F23` | hairlines, card borders |
| `outlineVariant` | `0xFF2A2A2F` | secondary button borders, inactive chips |
| `onBackground` / `onSurface` | `0xFFF2F2F4` | primary text |
| `onSurfaceVariant` | `0xFF8D8D94` | labels, secondary text |
| `AppColors.textDisabled` | `0xFF55555C` | disabled text, axis ticks |
| `primary` | `brand.accent` | — |
| `onPrimary` | `brand.onAccent` | — |
| `primaryContainer` | `accentTint` | primary button fill |
| `secondary` | `0xFFA3A3AB` | neutral secondary (no second hue) |
| `tertiary` | `brand.accent` | — |
| `error` | `0xFFFF5A4F` | kept distinct from the accent by a slightly orange hue; error states also always carry text or an icon |

### 4.4 `AppColors.kt`: roles that M3 doesn't have

`@Immutable data class AppColors`, built by `Brand.toAppColors()`:

| Token | Value | Replaces |
|---|---|---|
| `accentTint`, `accentSoft`, `zones`, `recording` | derived (§4.2) | `ZoneColors`, jump `Color.Red`/`0xFFC62828`, hr gap line |
| `textDisabled` | `0xFF55555C` | — |
| `success` | `0xFF3FB27F` | `ConnectedGreen`, jump phase green |
| `warning` | `0xFFFFB74D` | hr `CautionOrange` |
| `highlight` | `0xFFFFC850` | hr milestone line |
| `onMedia` | `Color.White` | text and icons on camera or video |
| `mediaScrim` | `Color.Black.copy(alpha = 0.28f)` | the bottom HUD scrim on the camera |
| `mediaTextShadow` | `Shadow(Color.Black.copy(alpha = 0.75f), offset = (0, 1), blurRadius = 3)` | text on video |
| `scrimStrong` | `Color.Black.copy(alpha = 0.78f)` | hr tutorial scrim |
| `gridLine` | `Color.White.copy(alpha = 0.07f)` | chart grid |
| `tooltipContainer` | `0xE60B0B0D` | chart tooltip |

Text on surfaces uses `MaterialTheme.colorScheme.onSurface` or `onSurfaceVariant`, not `Color.White`. Errors use `colorScheme.error`.

### 4.5 `AppSpacing.kt`

`@Immutable data class AppSpacing(xxs = 2.dp, xs = 4.dp, sm = 8.dp, md = 12.dp, lg = 16.dp, xl = 24.dp, xxl = 32.dp)`.

### 4.6 Shapes

`AppShapes = Shapes(small = RoundedCornerShape(8.dp), medium = RoundedCornerShape(14.dp), large = RoundedCornerShape(20.dp))`, set on `MaterialTheme`. `AppTheme.pillShape = RoundedCornerShape(50)` is used for buttons, chips and tags.

### 4.7 Typography (Space Grotesk, kept)

The scale is adjusted to the Night Drive rules:

| Style | Change |
|---|---|
| `displayLarge` / `displayMedium` | Light (300). Used for hero numbers (BPM 52sp, jump height 64sp) |
| `headlineSmall` | Regular (400). Used for stat values |
| `titleMedium` | Medium (500), letterSpacing 0.2em. Used for screen and session titles (shown uppercase) |
| `labelSmall` | Regular, 9.5sp, letterSpacing 0.16em. Used for uppercase labels |

Uppercasing is done at the call site with `.uppercase()`; the `StatItem`/label components (sub-project 2) do it internally.

### 4.8 `AppTheme.kt`

```kotlin
@Composable
fun AppTheme(brand: Brand = Brand.Default, content: @Composable () -> Unit) {
    val appColors = remember(brand) { brand.toAppColors() }
    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalAppSpacing provides AppSpacing(),
    ) {
        MaterialTheme(
            colorScheme = remember(brand) { brand.toColorScheme() },
            typography = AppTypography, shapes = AppShapes, content = content,
        )
    }
}

object AppTheme {
    val colors: AppColors @Composable @ReadOnlyComposable get() = LocalAppColors.current
    val spacing: AppSpacing @Composable @ReadOnlyComposable get() = LocalAppSpacing.current
    val pillShape: Shape get() = PillShape
}
```

A top-level function and an object with the same name is legal Kotlin; `MaterialTheme` uses the same pattern. Existing `AppTheme { … }` callers keep compiling.

### 4.9 Deprecating `SharedColors.kt`

The top-level vals stay, marked `@Deprecated(level = WARNING)` with a `ReplaceWith`:

| Val | Replacement |
|---|---|
| `PrimaryPurple`, `LightPurple`, `OnPrimary` | `MaterialTheme.colorScheme.primary` / `primaryContainer` / `onPrimary` |
| `SecondaryBlue`, `TertiaryPink` | `colorScheme.secondary` / `tertiary` |
| `ErrorRed`, `BackgroundDark`, `SurfaceDark` | `colorScheme.error` / `background` / `surface` |
| `ConnectedGreen` | `AppTheme.colors.success` |
| `ZoneColors` | `AppTheme.colors.zones` |

**The vals keep their old values.** Apps that still pass them directly keep their old purple look in those spots until sub-projects 5 and 6 migrate them. Theme-driven M3 defaults switch to Night Drive immediately (see §6). Sub-project 7 removes the vals.

### 4.10 Migrating the lib's own components

The lib's composables are switched to tokens so the lib passes its own checker:

- `StatItem`
- `scan/BleScanComponents`
- `session/*`
- `selection/SelectionHeader`
- `zone/ZoneComponents`
- `LabelPickerDialog`
- `chart/ChartToggleButton`

The replacements are:
- shapes → `MaterialTheme.shapes.*`
- literal paddings → `AppTheme.spacing.*`
- deprecated vals → scheme roles or `AppTheme.colors`

The full Night Drive restyle of these components (uppercase labels, the accent-border pattern) is done in sub-project 2. Sub-project 1 only moves them onto tokens.

### 4.11 `DESIGN.md` (lib root)

Sections:
1. Principles: the Night Drive rules from §4.1, and the scope rule
2. Color roles (scheme plus `AppColors`, with values and derivation formulas)
3. Accent usage: where the accent is allowed, plus a do/don't list with the subtle-glow rule
4. Spacing scale
5. Shapes
6. Typography (`MaterialTheme.typography.*`, never `fontSize =`; hero numbers and uppercase labels)
7. How to define a `Brand`
8. Component catalog (grows with each sub-project)
9. The checker and `// design-ignore`
10. Links to the mockups in `docs/design/mockups/`

### 4.12 Checker: `tools/check_design_tokens.py`

Same style and CLI shape as `tools/check_hardcoded_strings.py`. It scans `*.kt` under the given paths, skips `**/ui/theme/**` and test sources, and flags:

| Rule id | Pattern |
|---|---|
| `raw-color` | `Color(0x…)`, `Color.White/Black/Red/Green/Blue/Yellow/Gray/…` (`Color.Transparent` and `Color.Unspecified` are allowed) |
| `raw-text-size` | `\d+(\.\d+)?\.sp`, `fontSize =` |
| `raw-shape` | `RoundedCornerShape(` |
| `raw-spacing` | a literal `\d+\.dp` inside `padding(`, `spacedBy(` or `Spacer(` size modifiers |
| `glow` | `.shadow(` with an accent/primary color, `BlurMaskFilter`, `drawBehind` blur |

- A line ending in `// design-ignore` is skipped.
- Output is `path:line: [rule-id] snippet`, and the exit code is 1 if anything was found.
- `--warn` prints the same output but exits 0. Apps run in this mode until sub-projects 5 and 6.
- The lib CI (`.github/workflows/ci.yml`, job `lib`) runs it in strict mode on `shared/src/main`.

### 4.13 Release

- Bump `shared/build.gradle.kts` from `0.3.2` to `0.4.0`. This is a visual change plus additive API plus deprecations.
- The CHANGELOG entry covers the Night Drive theme, the new scope rule and the deprecations.
- `release-tag.yml` creates `v0.4.0` on merge.

## 5. Testing and verification (sub-project 1)

| Check | Evidence |
|---|---|
| Unit: `Brand.Default.toColorScheme()` slots equal §4.3 | `:shared:testDebugUnitTest` |
| Unit: `toAppColors()`: zones for the red default equal the documented lerp values; a custom accent (blue) produces a blue ramp; Z5 == accent; `recording` == accent | same |
| Unit: `AppSpacing()` values | same |
| Checker fixture test: a bad fixture produces the expected rule ids and line numbers; `// design-ignore` is honored; `ui/theme/` is excluded; `--warn` exits 0 | `python tools/test_check_design_tokens.py` (run in CI) |
| The lib passes its own checker | `python tools/check_design_tokens.py shared/src/main` exits 0 |
| Lint is green | `:shared:lintDebug` |
| Both apps still build against the branch (deprecation warnings only) | CI job `apps`. Check beforehand that neither app sets `allWarningsAsErrors` |
| Visual check | Run both apps: M3 defaults are black and red; nothing is unreadable (text contrast on the black background) |

## 6. Risks

- **Mixed look between 0.4.0 and the app redesigns.** Theme-driven parts turn black and red while app-hardcoded parts stay purple. Mitigation: the apps pin 0.3.x until their redesign sub-project starts, then bump to the latest lib release in the same PR.
- **Accent vs. error confusion** with a red accent. Mitigation: a distinct error hue (§4.3), and errors always carry text or an icon, never color alone.
- **Deprecation warnings break an app build** if warnings are treated as errors. Check before merging.
- **Checker false positives** on `.dp` in non-spacing contexts. `raw-spacing` only fires inside padding, `spacedBy` and `Spacer`; everything else uses `// design-ignore`.

## 7. Redesign decisions (inputs for sub-projects 2–6)

Approved in the visual companion on 2026-10-11/12. The mockups are in `docs/design/mockups/` (HTML fragments; they render in any browser).

### 7.1 General
- The large-number style is used for primary readings. The ring gauge was rejected.
- The accent default is red (`0xFFE8202E`) and is configurable per app via `Brand`.
- Zone colors are always the accent ramp (§4.2). There are no fixed zone hues.

### 7.2 hr-tracker
| Screen | Decision |
|---|---|
| Scan | Status card shows the **device name plus battery** ("Polar H10 · Ready", "Battery 80%" if the BLE Battery Service is available, otherwise the name only); the primary action is Start workout |
| Live | Header: "RUNNING" with the elapsed time on the left, a large BPM number and the accent heart on the right. **No** zone-status tag. The chart keeps the **GOAL badge** and the target band. Stat row: **Avg BPM / Target / Total** (unchanged). Pause / Flag / Stop; Stop reacts to a **single tap** as today (no long-press) |
| History | **Grouped by week** ("This week", "Last week", …); each card shows the weekday and time, **Ø BPM** and a **mini zone-distribution bar** |
| Detail, Settings, Stats, Form | Restyled with the Night Drive rules; no structural changes |

### 7.3 jump-tracker
| Screen | Decision |
|---|---|
| Record (idle) | Full-bleed video. The resolution chip and camera switch are visible |
| Record (recording), **layout A2** | Full-bleed video. **No timer, resolution chip or camera switch.** **Last** (left) and **Best** (right, label in the accent) flank the shutter, with no box. A light scrim (≤25%) covers only the bottom 20%. The framing guide is dimmed to 55%. Phase text ("Ready – jump!") is a small uppercase label at the top. **Hint and phase text sit directly on the video** with a text shadow (no pills) |
| Result | Hero best value, a **"New personal best +x cm" badge** when applicable, **average** in the stat row, and the per-jump table with **inline height bars**. Buttons: **Done only** (no Play clip) |
| History | Restyled; **no** trend sparkline or PB card |
| Trim, Detail | Restyled with the Night Drive rules; no structural changes |

### 7.4 Rejected proposals (do not implement)
- Zone-status tag in the hr Live header
- Zone strip on the chart axis replacing the GOAL badge
- Long-press to stop
- Avg / Max / kcal Live stats
- jump History trend sparkline and PB card
- "Play clip" on Result
