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

## Verification for the user

- Once SP1 is done: install both debug apps. M3 defaults should look black and red, with no unreadable text.
