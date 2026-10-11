# Night Drive mockups

Browser mockups from the redesign brainstorming (2026-10-11/12). Open any file in a browser. They are HTML fragments, and the Space Grotesk font loads from Google Fonts.

The binding decisions are in `docs/superpowers/specs/2026-10-11-design-system-foundation-design.md` §7. Where a mockup and §7 disagree, §7 wins.

| File | Shows | Caveats |
|---|---|---|
| **`00-final-screens.html`** | **The approved final screens (2026-10-12): hr Scan / Live / History / Detail / Stats / Form / Settings / dialog; jump History / Record idle / Record recording / Trim / Result + analyzing / Detail** | **Binding visual reference.** Settings and Body-data field lists are illustrative; keep the real app's fields |
| `01-night-drive-screens.html` | hr Scan / Live / History, jump History / Result, and an accent picker with the derived zone ramp | The hr Live screen shows **rejected** ideas (zone-status tag, axis zone strip, Avg/Max/kcal). Keep the GOAL badge and Avg/Target/Total. jump History shows a **rejected** sparkline/PB card, and Result shows a rejected "Play clip" button. Record options A/B there are superseded by `02` |
| `02-record-view.html` | Record idle vs. recording, layouts A1/A2 | **A2 is the chosen layout** |
| `03-ux-proposals.html` | 10 UX proposals, without vs. with | The accepted ones are listed in spec §7 |

`toggleSelect` click handlers do nothing outside the brainstorming companion; that is harmless.