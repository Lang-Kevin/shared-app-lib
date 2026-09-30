# Changelog — shared-android-lib

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

### Changed (breaking)
- `TrashTab` and `SoftDeleteConfirmationDialog` take a required `retention` parameter
- `validateZoneTexts` returns `List<ZoneTextError?>` instead of German strings
- `Long.toDateString()` uses the device locale's short date/time format
- `DiscoveredDevice.Real.displayName` falls back to the MAC address instead of a German text
- Compose UI/Material3/icons, google fonts, activity-compose, coroutines and serialization are now `api` dependencies
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
