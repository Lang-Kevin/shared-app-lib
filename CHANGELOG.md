# Changelog — shared-android-lib

## [0.5.1] — 2026-10-12

### Fixed
- `DateStyle.WeekdayTime` follows the locale's 12h/24h convention (en-US "Mon 4:42 PM", de "Mo. 16:42"); was always 24h

## [0.5.0] — 2026-10-12

### Added
- Night Drive basics in `ui/components`: `PrimaryButton`, `SecondaryButton` (`destructive`), `TextActionButton`; `Metric`, `StatGrid`/`StatCell`; `SegmentedControl`; `AppTabRow`; `ListCard`/`ListCardValue`; `EmptyState`; `SwipeToDelete` (`error` background, slides out at 40%)
- Dialog family in `ui/dialog`: `AppDialog` shell (78% scrim, stacked full-width actions), `DialogScrim`, `TextInputDialog`, `SingleChoiceDialog`, `ProgressDialog`, `DateRangeFilterDialog` (emits inclusive `LocalDate` start/end, not epoch millis)
- `ui/input`: `NumberField`, `parseNumberInput`/`NumberInput`
- Formatters in `format` (pure JVM, explicit `Locale`/style): `formatElapsed`/`DurationStyle`, `formatDecimal`, `formatMeasure`, `formatPercent`, `NO_VALUE`, `formatDate`/`DateStyle`, `pickerMillisToLocalDate`/`toPickerMillis`
- `UiText` in `text` (drop-in for jump's `UiText`)
- Strings `shared_action_ok/cancel/save/confirm/apply/delete`, `shared_date_range_title` (EN + DE)
- Chart toolkit (`ui/chart/`):
  - `LineChart` zone chart: target band, GOAL badge, dashed accent mean line (full width or gap segments), milestone markers, last point, scrubber + tooltip, time axis.
  - `TrendChart`: optional band with label, highlighted last point, category labels.
  - `BarChart`: highlighted current bar.
  - `ZoneDistributionBar` + `TimeInZoneSection`, also used as the History mini zone bar.
  - `ChartDefaults` / `ChartColors`: token-only chart roles.
- Pure, JVM-tested chart math:
  - `ChartViewport` / `LiveWindow`, plus `toWindow` and the `LiveWindow.zoomBy` min/max parameters.
  - `nearestIndex`, `plotFraction`.
  - Tick and category-label spacing, y ranges.
  - Absolute-anchored, gap-preserving decimation.
  - Scrub, tooltip, zone-segment and bar geometry.
- `Modifier.chartZoomPan`: zoom, pan, double tap and long-press scrub, ported from hr-tracker.
- Media and overlay components (`com.kevin.shared.ui.media`, `com.kevin.shared.ui.overlay`): `MediaText`/`MediaReading` (text on video with the token shadow, no boxes), `ShutterButton` (optional progress arc), `FramingGuide` (A2 dimming, error colour, hint slot that flips above the foot line), `CameraHud` (A2 layout: instant idle/recording slot swap, <=25% bottom scrim, inset-aware, reports its bottom band height), `MediaIconButton`, `MediaTextButton`, `TutorialSpotlight` (from hr, on tokens; fixes overlay offset, rotation and card sizing), `PermissionGate` with `permissionGateMode` and `openAppSettings` (permanent denial leads to settings). Spotlight and gate primary actions are `PrimaryButton`s; Skip is a `TextActionButton`. No new dependencies; all strings are parameters.

### Changed
- **Visual:** `StatItem` labels are uppercase and values Regular; new trailing parameter `unit` (default `null`)
- **Visual:** `LeaveSessionDialog`, `SoftDeleteConfirmationDialog`, `DeleteForeverConfirmationDialog`, `LabelPickerDialog` and `TargetZoneDialog` use `AppDialog`: 78% scrim, full-width stacked buttons, destructive actions in `error`
- **Visual:** `SessionListItem`, `TrashTab` rows, `SavedDeviceItem` and `DiscoveredDeviceItem` are `ListCard`s. The selected state is an accent border plus tint, replacing `primaryContainer`, and the whole saved-device card is now clickable. `SummaryCard` is a hairline `StatGrid`, `CategoryFilterRow` chips are outlined pills, and the `TrashTab` empty text is an `EmptyState`
- **Visual:** `TargetZoneDialog` picks the zone with a `SegmentedControl` plus a zone-colour strip, replacing the zone-filled chips
- **Visual:** `BleStatusCard` always has a hairline border; the `SelectionHeader` count is uppercase
- `durationString` delegates to `formatElapsed`; negative input now gives `00:00`

### Removed (breaking)
- `SharedColors.kt` vals deprecated in 0.4.0 (`PrimaryPurple`, `LightPurple`, `SecondaryBlue`, `TertiaryPink`, `OnPrimary`, `ErrorRed`, `BackgroundDark`, `SurfaceDark`, `ConnectedGreen`, `ZoneColors`); use `MaterialTheme.colorScheme` / `AppTheme.colors`

## [0.4.0] — 2026-10-12

### Added
- Release tags are created by CI (`.github/workflows/release-tag.yml`): on every change to `shared/build.gradle.kts` on master, the tag `v<version>` is created if it doesn't exist; a manual run with `tag` + `sha` backfills older releases. A release is now: bump the version, merge.
- Night Drive design system: `Brand` (configurable accent, default red `#E8202E`), `AppTheme(brand)`, `AppTheme.colors` (`AppColors`: accentTint/accentSoft, accent-derived zone ramp, recording, success/warning/highlight, media and chart roles), `AppTheme.spacing` (2–32dp), `MaterialTheme.shapes` 8/14/20dp + `AppTheme.pillShape`, Night Drive type scale (light hero numbers, tracked labels)
- `DESIGN.md`: rules, tokens, accent usage, component catalog
- `tools/check_design_tokens.py` (+ fixture tests), strict in lib CI; apps can run it with `--warn`; multi-line spacing detection, exit 2 on missing paths

### Changed (breaking)
- **Visual:** the theme is now Night Drive (black background, graphite neutrals, red accent). Every M3 color slot is set explicitly. Theme-driven UI in the apps changes immediately; app-hardcoded colors stay until the app redesigns
- Scope rule: generic UI belongs in the lib even if only one app uses it today
- Lib components use tokens (StatItem/TargetZoneDialog defaults now come from the theme)

### Deprecated
- `SharedColors.kt` vals (`PrimaryPurple`, `ZoneColors`, …) keep their old values; use `MaterialTheme.colorScheme` / `AppTheme.colors`. Removal planned after both app redesigns

## [0.3.2] — 2026-09-30

### Security
- Kotlin 2.1.0 → 2.4.20 (Kotlin Gradle plugin, Compose compiler, serialization plugin): fixes CVE-2026-53914 / GHSA-r937-wjx7-w2jp, code execution via unsafe deserialization of Kotlin build cache metadata
- Build tooling (AGP plugin classpath, Android lint, Unified Test Platform): vulnerable transitive versions raised by a rule in `settings.gradle.kts` (Netty 4.1.138, Bouncy Castle 1.86, jdom2 2.0.6.1, jose4j 0.9.7, commons-lang3 3.21.0, httpclient 4.5.14); nothing of this reaches the apps' runtime classpath

### Added
- CVE check (`.github/workflows/security.yml`): resolves the full dependency tree into Gradle lockfiles and scans them with osv-scanner on push, PRs, weekly and on demand
- Dependabot for Gradle and GitHub Actions (`.github/dependabot.yml`)

### Changed
- kotlinx-serialization-json 1.7.3 → 1.11.0, matching the newer serialization compiler plugin (reaches the apps via `api`)

## [0.3.1] — 2026-09-30

### Added
- `tools/check_hardcoded_strings.py` — CI check for hardcoded UI text in Compose code (Android lint's HardcodedText only covers XML); `// i18n-ignore` marks deliberate literals
- CI (`.github/workflows/ci.yml`): lib unit tests, lint and the string check, plus both apps built against every lib change (same-named app branch if present, else the app's default branch)

### Changed
- Apps pin the lib version their CI builds against in `code/shared-app-lib.ref` (a tag of this repo); bump it to adopt a new lib release

## [0.3.0] — 2026-09-30

Rule from now on: a feature moves into the lib only when both hr-tracker and jump-tracker use it.
as-tracker (ArmSwing) is deprecated and no longer built against this lib.

### Added
- `ui/theme/AppTheme` + `AppTypography`/`SpaceGroteskFamily` — one theme for all apps (Google-font certificates ship with the lib)
- `domain/Selection` — immutable multi-select state; `ui/selection/SelectionHeader` — "n selected" header with cancel/delete and back handling
- `domain/LabelFilter` — chip filter state; `CategoryFilterRow(categories, filter, onToggle)` overload
- `ui/session/TrashRetention` (`ON_NEXT_APP_START`, `MANUAL`) — `TrashTab` and `SoftDeleteConfirmationDialog` now describe the app's real trash policy
- `TrashTab(onDeleteForever = …)` and `DeleteForeverConfirmationDialog` for apps without auto-purge
- `domain/ZoneTextError` + `ZoneTextError.message()`
- Localization: all lib texts are Android string resources, English default (`values/`), German (`values-de/`); resources carry the `shared_` prefix

### Fixed
- Google-Fonts certificates were corrupt (copied from hr), so Space Grotesk never loaded; replaced with the official dev/prod certificates

### Changed (breaking)
- `TrashTab` and `SoftDeleteConfirmationDialog` take a required `retention` parameter
- `validateZoneTexts` returns `List<ZoneTextError?>` instead of German strings
- `Long.toDateString()` uses the device locale's short date/time format
- `DiscoveredDevice.Real.displayName` falls back to the MAC address instead of a German text
- Compose UI/Material3/icons, google fonts, activity-compose, coroutines and serialization are now `api` dependencies
- `compileSdk` 37 (matches the apps and the 2026 Compose BOM)
- Apps locate the lib via `sharedLibPath` in `code/local.properties` (default `../../shared-app-lib`)

### Removed
- `settings/BleDevicePreferences`, `settings/BleDevicePrefKeys` — unused by every app; the DataStore dependency is gone with them
- `ui/chart/aggregatePerSecond`, `AggregatedPoint`, `VelocityPoint` — no remaining consumer
- `ui/chart/ChartScaleMode` — the only consumer (ArmSwing) now uses a persisted `Boolean`
- `ui/chart/VelocityChart` — removed earlier, recorded here

## [0.2.0] — 2026-06-14

### Added
- `ui/chart/ChartScaleMode` — enum with three modes: `SCROLL` (rolling window), `SHOW_ALL` (full session, max-per-second aggregated), `AUTO_FIT` (raw below 300 samples, aggregated above)
- `ui/chart/VelocityChart` — Canvas-based Composable replacing `OmegaSessionChart`; fully backwards-compatible via default parameters; `lineColor` and `markerColor` are injectable so any app can theme it
- `ui/chart/ChartAggregator` — pure functions `aggregatePerSecond` and `aggregateByChunks`; no Android dependencies; unit-tested
- `ui/chart/VelocityPoint`, `AggregatedPoint` — data classes carrying `min`, `avg`, `max` per bucket (band-render ready for future milestone)

### Migration (optional)
Consuming apps can replace `OmegaSessionChart` with `VelocityChart` to gain mode switching. The old call site continues to compile and behaves identically to `ChartScaleMode.SCROLL`.

## [0.1.0] — initial release

BLE scan UI, session history components, soft-delete dialog, BLE preferences, base recording service.
