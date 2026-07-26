# Changelog — shared-android-lib

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
