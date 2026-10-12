# Handover — Night Drive design system + redesign

Updated: 2026-10-12 · Branch: `ds-integration` (local, not pushed; contains `design-system-foundation`)

## Status

| # | Sub-project | Status |
|---|---|---|
| 1 | Foundation (tokens, Brand, typography, DESIGN.md, checker) → 0.4.0 | Done, QA PASS |
| 2 | Basics & dialogs + formatters + UiText | Done (`f98a8c9`…`ecef49e`, branch `ds-basics`) → released in 0.5.0 |
| 3 | Chart toolkit | Done (`20498a2`…`fb49d76`, branch `ds-charts`) → 0.5.0 |
| 4 | Overlays & media chrome (Record A2 HUD) | Done (`1cd2813`…`c9177ea`, branch `ds-media`) → 0.5.0 |
| — | Integration `ds-integration` | Merged (`87e4b85`, `b3832ba`, `72012c9`) + `0320db4` (0.5.0, PrimaryButton swap). Integration QA PASS, review APPROVE; review warnings being fixed |
| 5 | hr-tracker migration + redesign | `feat/night-drive` in hr repo: T1–T16 approved (`a3204c5`…`7b0130a`), T17 no-op (lib Trash already restyled), T18–T32 approved (`9d1bfc2`…`e4a5197`). QA: build/tests (167; drop from 197 = deleted hr chart-toolkit tests), token/string checkers, parity OK 313 all PASS; lint failed on 10 `NonObservableLocale` → fixed in `f005f1f` (`currentLocale()` helper), review APPROVE. **Done.** Room v7→v8 migration has no unit test → device check |
| 6 | jump-tracker migration + redesign | **Done.** `feat/night-drive` in jump repo: 22 commits (`0101362`…`9102569`) on `960a204`, 37 files. QA PASS: 454 tests, 0 deprecations, token + string checkers exit 0. Ref bump pending lib push |
| 7 | Remove deprecated SharedColors vals | **Done.** `e7fa983` on `ds-integration` (part of 0.5.0, `### Removed (breaking)`), review APPROVE. Apps' feat branches have 0 usages; CI pins protect old branches |

**Decision (PO):** SP2–4 ship together as one release **0.5.0** (not 0.5/0.6/0.7).

Integration QA (ds-integration @ `0320db4`): lib 244 tests 0 fail, lint + checker clean; hr + jump `assembleDebug testDebugUnitTest` BUILD SUCCESSFUL, 0 errors (deprecated `w:` counts not comparable — incremental build); EN/DE parity 7 keys.

## Final QA (2026-10-12, lib `e7fa983`)

Full `--rerun-tasks` gates, sequential: hr `feat/night-drive` @ `f005f1f`: BUILD SUCCESSFUL, 167 tests 0 fail, lint 0 errors, token + string checkers exit 0. jump `feat/night-drive` @ `9102569`: BUILD SUCCESSFUL, 454 tests 0 fail, lint 0 errors, checkers exit 0. Lib: 243 tests (244 − removed deprecated-vals test), lint green.

## Waiting for the user

1. Go-ahead to push `ds-integration` and merge to `master` (0.5.0 → CI tag `v0.5.0`).
2. Then bump `code/shared-app-lib.ref` to `v0.5.0` in both apps (hr T31 / jump T22 remainder), push `feat/night-drive`, open PRs.
3. Optional: rewrite "Haiku 5.5" trailers (`ae85e2d`, `52a539a`, `e7fa983`, hr `f005f1f`).
4. Device check: hr Room v7→v8 migration on existing data, DE locale; jump record HUD on 19.5:9 + 16:9, PiP, swipe.

Known: lib `DateStyle.WeekdayTime` is always 24h (EN users lose 12h). Lib follow-up candidates: `BleStatusCard(deviceName, detail)`, `mediaScrim` 0.28 vs ≤25%, jump HeroValue/AccentTag/AccentDivider/InlineBar, hr Detail decimation smoothing peaks. hr orphan strings (`history_load_legend_*`, some Readiness/RestingHr) to clean.

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
| `d974e2a` | chore: ignore `__pycache__/`, handover update |
| `78c48e0` | Fix: 12 M3 `*Fixed` slots set + `noSlotKeepsM3Default` / `fixedSlotsFollowAccent` tests (review folded into T8 review) |
| `3ce92e4` | SP1 T8: `DESIGN.md`, CHANGELOG `[0.4.0]`, version 0.4.0. Reviewed (with `78c48e0`): APPROVE; warning fixed in next row |
| `d46b965` | Review follow-ups: dark-polarity secondary `*Fixed` + WCAG test, DESIGN.md/CHANGELOG fixes; reviewed: APPROVE |
| — | SP1 T9 QA: PASS. Lib gate green, checker clean, hr + jump `assembleDebug testDebugUnitTest` BUILD SUCCESSFUL (0 errors; deprecated `w:` hr 104, jump 20), diff scope OK. Visual check is user-only |

## Execution decisions for SP2–4 (Product Owner, 2026-10-12, override the plans)

- **Worktrees.** Each sub-project runs in its own worktree:
  - `..\shared-android-lib-ds-basics` (branch `ds-basics`)
  - `..\shared-android-lib-ds-charts` (branch `ds-charts`)
  - `..\shared-android-lib-ds-media` (branch `ds-media`)

  Each worktree gets a copy of `local.properties`. The main checkout stays on `design-system-foundation`. SP4's "one sub-project at a time" constraint is dropped.
- **App QA.** The plans' app-QA steps (SP2 T18, SP3 T11, SP4 T8) run only the lib gate and the scope diff in their worktree. The app builds run once, on an integration branch `ds-integration` in the main checkout (design-system-foundation + ds-basics + ds-charts + ds-media merged in that order). ~~Versions 0.5.0 / 0.6.0 / 0.7.0 are assigned there.~~ Superseded: single 0.5.0.
- **SP2 T14–16** (restyle of existing lib components) stay in. The apps' CI pin protects them, and the redesign is the goal.
- **SP2:** accept the new swipe behaviour (slide-out at 40%, no deprecated overload). The locale stays a required parameter; SP5/SP6 pick it. `CategoryFilterRow` "empty = all" goes to SP5.
- **SP3:** keep `BarChart`. Decimation uses the mean (SP5 may add a max mode). Mean-line mode is decided in SP5.
- **SP4:**
  - phase text uses `titleMedium`
  - `FramingGuide` out-of-frame stays at **full strength** while recording (override of 0.55)
  - progress arc off by default
  - HUD scrim clamp is 0.25
  - `AccentPillButton` is replaced by SP2 `PrimaryButton` at integration

## In flight

- Workflow `wf_5be2eb23-506` (SP2–4, 34 tasks) completed: all tasks APPROVED, per-branch lib QA PASS. Worktrees `..\shared-android-lib-ds-{basics,charts,media}` can be removed after acceptance.
- Review follow-ups on ds-integration: `formatTickLabel` → `formatElapsed(Clock)`, TutorialSpotlight Skip → `TextActionButton`, CHANGELOG/DESIGN.md wording.
- Review follow-ups committed in `c35ccf4`; `design-system-foundation` fast-forwarded to `c35ccf4` (local).
- SP5 + SP6 workflows finished (results in session `tasks\w5i2otlxv.output`, `tasks\wbj3kg1yy.output`). Lib worktrees `..\shared-android-lib-ds-{basics,charts,media}` still present; remove after acceptance.
- (History) **SP5 + SP6 ran via** workflow `wf_3646ae25-f57` (script `…/workflows/scripts/night-drive-sp5-6-wf_3646ae25-f57.js`). Each task goes implement → review → up to 2 fixes, then app QA. Both apps work on `feat/night-drive`.
  - hr builds the lib from the main checkout. Keep it on `ds-integration` and don't touch it while the run is going.
  - jump built the lib from a temporary worktree during the run. Its `sharedLibPath` has been restored and the worktree removed.
  - PO decisions are recorded in each plan's "PO resolution" block. The CI pin bumps (hr T31 and jump T22) wait for the user's lib-push go-ahead.
- (Done) @architect wrote the SP5 plan → `hr_tracker_app\docs\superpowers\plans\2026-10-12-hr-night-drive.md` and the SP6 plan → `jump_tracker_app\docs\superpowers\plans\2026-10-12-jump-night-drive.md`.

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
  - ~~Set the 12 M3 `*Fixed` slots~~ done in `78c48e0`.
  - Commits `ae85e2d`, `52a539a` carry a "Haiku 5.5" co-author trailer; rewrite before push only if the user wants it.
  - Z5 chip label contrast (~4.0:1 on the accent) is below AA. Address it in SP2 chip/segmented styling, for example with an accent-border selected state instead of a full accent fill.
  - hr LiveScreen still passes the deprecated pastel `ZoneColors` (low contrast). SP5 swaps it to `AppTheme.colors.zones`.

## Verification for the user

- Once SP1 is done: install both debug apps. M3 defaults should look black and red, with no unreadable text.
