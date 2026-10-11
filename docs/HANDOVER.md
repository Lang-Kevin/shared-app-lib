# Handover — Night Drive design system + redesign

Updated: 2026-10-12 · Branch: `design-system-foundation` (not pushed)

## Status

| # | Sub-project | Status |
|---|---|---|
| 1 | Foundation (tokens, Brand, typography, DESIGN.md, checker) → 0.4.0 | Plan written, execution starting |
| 2 | Basics & dialogs + formatters + UiText | Plan pending (@architect) |
| 3 | Chart toolkit | Plan pending (@architect) |
| 4 | Overlays & media chrome (Record A2 HUD) | Plan pending (@architect) |
| 5 | hr-tracker migration + redesign | Waits for 2–4 |
| 6 | jump-tracker migration + redesign | Waits for 2–4 |
| 7 | Remove deprecated SharedColors vals | Waits for 5, 6 |

## Done

| Commit | What |
|---|---|
| `0f6062c` | Initial design system spec |
| `f9ce884` | Spec revised for Night Drive, mockups |
| `6d7ecf4` | Final mockups, spec approved |
| `32a3c07` | SP1 plan, handover |
| `e244ddf` | SP1 T1: `Brand` + `AppColors` + `BrandTest` (5 tests green, lint green), reviewed: APPROVE |
| `5a552b8` | SP1 T2: `AppSpacing` + `AppShapes` + `TokensTest` (green), reviewed: APPROVE |
| `49b4d6d` | SP1 T3: Night Drive type scale + `TypographyTest` (green), reviewed: APPROVE |
| `5ff4a85` | SP1 T4: `AppTheme(brand)` (all M3 slots), `object AppTheme` accessors, `SharedColors` @Deprecated; `ColorSchemeTest` 4 green |
| `1a7ec8e` | SP1 T5: `tools/check_design_tokens.py` + 5 fixture tests + CI test step; 33 findings in 6 lib files (to migrate in T6/T7), reviewed: APPROVE w/ warnings → fixed in `ae85e2d` |
| `ae85e2d` | Checker fix: multi-line/nested spacing, exit 2 on missing path, utf-8 errors=replace, resolved-path exclusion; 9 tests green (trailer says Haiku 5.5, accurate for the subagent) |
| `a3448aa` | SP1 T6: StatItem, BLE scan, zone dialog on tokens; checker clean for these files; gradle green, 0 `w:`, reviewed: APPROVE |
| `52a539a` | SP1 T7: session, trash, label picker on tokens; strict `Design tokens` CI step; whole lib checker clean (exit 0) |

## Key documents

- Spec: `docs/superpowers/specs/2026-10-11-design-system-foundation-design.md`
- Plan SP1: `docs/superpowers/plans/2026-10-12-design-system-foundation.md`
- Mockups: `docs/design/mockups/00-final-screens.html` (binding)

## Local build environment

- `$env:JAVA_HOME = 'C:\Users\Kiwi PC\.gradle\jdks\jetbrains_s_r_o_-21-amd64-windows.2'`. The Android Studio JBR is broken (it has no `conf/`).
- Gradle needs to run outside the sandbox.
- `local.properties` `sdk.dir` was escaped (`C\:/…`) to fix the lint `PropertyEscape` error. The file is gitignored, so this was a local-only change.
- Python is `python` (3.14) locally and `python3` in CI.

## Open items / risks

- Apps consume the lib via `includeBuild`; the CI pin is `code/shared-app-lib.ref`. Bump it only in each app's redesign PR.
- hr Scan battery display needs new BLE Battery Service read code.
- jump `main` has 2 unpushed commits (user's); don't touch them.
- Merge to `master` and push only with the user's go-ahead.
- Follow-ups from reviews:
  - Set the 12 M3 `*Fixed` slots in `toColorScheme` and add a slot-completeness test (queued after T7).
  - Z5 chip label contrast (~4.0:1 on the accent) is below AA. Address it in SP2 chip/segmented styling, for example with an accent-border selected state instead of a full accent fill.
  - hr LiveScreen still passes the deprecated pastel `ZoneColors` (low contrast). SP5 swaps it to `AppTheme.colors.zones`.

## Verification for the user

- Once SP1 is done: install both debug apps. M3 defaults should look black and red, with no unreadable text.
