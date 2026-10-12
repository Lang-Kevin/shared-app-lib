# Design — shared-android-lib (Night Drive)

Source of truth for tokens: `shared/src/main/kotlin/com/kevin/shared/ui/theme/`. Checker: `tools/check_design_tokens.py`.

## 1. Principles

Night Drive rules (spec §4.1, verbatim):

- **Cabin:** a pure black background, near-black surfaces and graphite hairlines. No elevation shadows.
- **The accent is rare.** It marks the one thing that matters on a screen: the primary action, the active/selected state, the live value marker, the target zone and the recording state. Everything else is white, grey or graphite.
- **Subtle glow only.** An accent element is drawn as a **1dp accent border plus a 9% accent tint** fill. Section dividers may use a 1dp horizontal gradient line (transparent → accent at 40% → transparent). **No halos, blur shadows or outer glows.**
- **Numbers are the heroes.** Primary readings (BPM, jump height) use a large Light (300) weight. Labels are small, uppercase and widely tracked.
- **Pills** are used for buttons, chips and status tags. Cards use a 14dp radius.
- **Over video**, text sits directly on the image with a text shadow and no boxes. Scrims are at most 25–30% and only at the edges.

Scope rule: Generic UI belongs in this lib even if only one app uses it today (replaces the 0.3.0 "both apps" rule).

## 2. Color roles

### `MaterialTheme.colorScheme` (set by `Brand.toColorScheme()`)

Every slot is set explicitly. Unset slots would keep M3's purple baseline.

| Slot | Value | Slot | Value |
|---|---|---|---|
| primary | accent | onPrimary | onAccent (#FFFFFF) |
| primaryContainer | accentTint | onPrimaryContainer | NdOnSurface #F2F2F4 |
| inversePrimary | accent | secondary | NdSecondary #A3A3AB |
| onSecondary | NdBackground #000000 | secondaryContainer | NdOutlineVariant #2A2A2F |
| onSecondaryContainer | NdOnSurface | tertiary | accent |
| onTertiary | onAccent | tertiaryContainer | accentTint |
| onTertiaryContainer | NdOnSurface | background | #000000 |
| onBackground | NdOnSurface | surface | NdSurface #0B0B0D |
| onSurface | NdOnSurface | surfaceVariant | NdOutline #1F1F23 |
| onSurfaceVariant | NdOnSurfaceVariant #8D8D94 | surfaceTint | Transparent |
| inverseSurface | NdOnSurface | inverseOnSurface | NdSurface |
| error | NdError #FF5A4F | onError | NdBackground |
| errorContainer | lerp(NdTintBase, NdError, 0.12) | onErrorContainer | NdOnSurface |
| outline | NdOutline | outlineVariant | NdOutlineVariant |
| scrim | Black | surfaceBright | NdOutline |
| surfaceDim | NdBackground | surfaceContainerLowest | NdBackground |
| surfaceContainerLow | NdSurface | surfaceContainer | NdSurface |
| surfaceContainerHigh | #141417 | surfaceContainerHighest | NdOutline |
| primaryFixed | accent | primaryFixedDim | accentTint |
| onPrimaryFixed | onAccent | onPrimaryFixedVariant | NdOnSurface |
| secondaryFixed | NdOutlineVariant | secondaryFixedDim | NdOutline |
| onSecondaryFixed | NdOnSurface | onSecondaryFixedVariant | NdOnSurfaceVariant |
| tertiaryFixed | accent | tertiaryFixedDim | accentTint |
| onTertiaryFixed | onAccent | onTertiaryFixedVariant | NdOnSurface |

The `*Fixed` slots keep M3 from falling back to its baseline when a component reads fixed roles.

Neutrals (`Brand.kt`, internal): NdBackground #000000, NdSurface #0B0B0D, NdOutline #1F1F23, NdOutlineVariant #2A2A2F, NdOnSurface #F2F2F4, NdOnSurfaceVariant #8D8D94, NdTextDisabled #55555C, NdSecondary #A3A3AB, NdError #FF5A4F, NdGraphite #26262B (zone ramp start), NdTintBase #0A0A0A (accentTint base).

### `AppTheme.colors` (`AppColors`, set by `Brand.toAppColors()`)

| Role | Value / derivation |
|---|---|
| accentTint | `lerp(NdTintBase, accent, 0.09)` |
| accentSoft | `accent` at 40% alpha |
| zones (Z1–Z5) | `lerp(NdGraphite, accent, 0.10 / 0.28 / 0.50 / 0.75)` plus `accent` for Z5 (not `lerp(…, 1f)`: Oklab drift) |
| recording | `accent` |
| textDisabled | #55555C |
| success | #3FB27F |
| warning | #FFB74D |
| highlight | #FFC850 |
| onMedia | #FFFFFF |
| mediaScrim | black at 28% |
| mediaTextShadow | black 75%, offset (0, 1), blur 3 |
| scrimStrong | black at 78% |
| gridLine | white at 7% |
| tooltipContainer | #E60B0B0D (NdSurface at ~90%) |

## 3. Accent usage

Allowed (one per region):
- primary action
- active / selected state
- live value marker
- target zone
- recording

Do:
- 1dp accent border + `AppTheme.colors.accentTint` fill
- gradient divider: transparent → `AppTheme.colors.accentSoft` → transparent

Don't:
- halos, outer glows
- `.shadow(` in the accent color
- blur (`.blur(`, `BlurMaskFilter`)
- accent on more than one element per region
- color as the only error signal

Contrast: selected chip/segment on a full accent fill: label contrast is ~4.0:1. Prefer the 1dp accent border + accentTint fill for selected states.

## 4. Spacing

`AppTheme.spacing` (`AppSpacing`, dp):

| Token | dp | Token | dp |
|---|---|---|---|
| xxs | 2 | lg | 16 |
| xs | 4 | xl | 24 |
| sm | 8 | xxl | 32 |
| md | 12 | | |

Never write padding / `spacedBy` / `Spacer` dp literals in UI code.

## 5. Shapes

| Token | Radius | Use |
|---|---|---|
| `MaterialTheme.shapes.small` | 8dp | chips, fields |
| `MaterialTheme.shapes.medium` | 14dp | cards |
| `MaterialTheme.shapes.large` | 20dp | sheets, dialogs |
| `AppTheme.pillShape` | 50% (pill) | buttons, chips, status tags |

Never `RoundedCornerShape(` in UI code.

## 6. Typography

Font: Space Grotesk (Google Fonts provider), Light to Bold. Access via `MaterialTheme.typography.*`. Never `fontSize =` or `N.sp`.

| Style | Size / line | Weight | Tracking |
|---|---|---|---|
| displayLarge | 64sp / 68sp | Light | 0 |
| displayMedium | 52sp / 56sp | Light | 0 |
| headlineSmall | (M3 default) | Normal | (M3 default) |
| titleMedium | (M3 default) | Medium | 0.2em |
| labelSmall | 9.5sp / 14sp | Normal | 0.16em |
| all other styles | M3 default size | M3 default | M3 default |

- Hero numbers: `displayLarge` (64sp, jump height) and `displayMedium` (52sp, BPM).
- Labels: `labelSmall` plus `.uppercase()` at the call site.

## 7. Brand

`Brand(accent, onAccent)` is the only per-app theme input. Everything accent-dependent (scheme primary/tertiary, accentTint, zone ramp, recording) is derived from `accent`.

```kotlin
AppTheme(brand = Brand(accent = Color(0xFF2E6BE8))) { … }
```

Default: `Brand.Default` = `NightDriveRed` #E8202E, onAccent #FFFFFF.

## 8. Component catalog

Lib components on tokens (grows with sub-projects 2–4):

| Component | File |
|---|---|
| `StatItem` | `ui/StatItem.kt` |
| `BleStatusCard`, `SavedDeviceItem`, `DiscoveredDeviceItem` (BLE scan) | `ui/scan/BleScanComponents.kt` |
| `SummaryCard`, `SummaryItem`, `SessionListItem`, `CategoryFilterRow`, `LeaveSessionDialog` | `ui/session/SessionComponents.kt` |
| `TrashTab`, `SoftDeleteConfirmationDialog`, `DeleteForeverConfirmationDialog` | `ui/session/TrashComponents.kt` |
| `SelectionHeader` | `ui/selection/SelectionHeader.kt` |
| `TargetZoneDialog` | `ui/zone/ZoneComponents.kt` |
| `LabelPickerDialog` | `ui/LabelPickerDialog.kt` |
| `ChartToggleButton` | `ui/chart/ChartToggleButton.kt` |
| `LineChart` + `LineSeries`, `ChartZone`, `ChartMarker`, `ChartReferenceLine` (zone chart: target band, GOAL badge, dashed accent mean line, milestones, last point, scrubber + tooltip, time axis) | `ui/chart/LineChart.kt`, `ui/chart/ChartModels.kt` |
| `TrendChart` + `TrendSeries` (optional neutral band with label, highlighted last point, category labels) | `ui/chart/TrendChart.kt` |
| `BarChart` (highlighted current bar) | `ui/chart/BarChart.kt` |
| `ZoneDistributionBar`, `TimeInZoneSection` (time in zone; History mini zone bar) | `ui/chart/TimeInZone.kt` |
| `ChartViewport`, `LiveWindow`, `nearestIndex`, `plotFraction`, `Modifier.chartZoomPan` (zoom/pan/scrub state + gestures) | `ui/chart/ChartViewport.kt`, `ui/chart/ChartGeometry.kt`, `ui/chart/ChartGestures.kt` |
| `ChartDefaults`, `ChartColors` (chart roles from tokens, dimensions) | `ui/chart/ChartColors.kt` |

Chart rules:
- Colors come only from `ChartDefaults.colors()`.
- x is in data units: `LiveWindow.toWindow` (Live) or `ChartViewport.toWindow` (Detail).
- Gaps are non-finite values.
- Charts draw edge to edge, so gesture px map to fractions with `plotFraction(x, 0f, width)`.
- All text is passed in by the caller.

## 9. Checker

`python tools/check_design_tokens.py [--warn] <dir-or-file> [...]`

| Rule id | Flags |
|---|---|
| `raw-color` | `Color(0x…)`, `Color.White/Black/Red/Green/Blue/Yellow/Gray/…` (`Transparent`, `Unspecified` allowed) |
| `raw-text-size` | `N.sp` literals, `fontSize =` |
| `raw-shape` | `RoundedCornerShape(` |
| `raw-spacing` | literal `N.dp` inside `padding(`, `spacedBy(`, `Spacer(` |
| `glow` | `.shadow(` with primary/accent before its first `)`, `BlurMaskFilter`, `.blur(` |

Exit codes: 0 clean (or `--warn`), 1 findings, 2 a path does not exist (even with `--warn`).

Exceptions: `// design-ignore: <reason>` on the line (always with a reason). Skipped: `**/ui/theme/**`, `src/test`, `src/androidTest`, comment lines.

Heuristic limits (line/regex based, no Kotlin parser):
- `raw-spacing` walks paren depth across lines from each call. Parens in strings or trailing comments can skew it.
- Only literal `N.dp` inside those calls is seen. `width(8.dp)` or a `val x = 8.dp` is not flagged.
- `glow` matches `.shadow(` only when primary/accent appears on the same line before the first closing `)`; `.shadow(elevation(x), color = primary)` is missed. Reviewers catch the rest.
- Only `Color.` names in the list are caught; `Color(red, green, blue)` is not.
- `--warn` for apps until their redesign. CI runs strict on `shared/src/main`.

## 10. Mockups

- Binding: `docs/design/mockups/00-final-screens.html`
- Index and notes: `docs/design/mockups/README.md`
