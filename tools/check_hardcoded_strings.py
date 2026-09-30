#!/usr/bin/env python3
"""Flags user-visible string literals in Compose/Kotlin code that should be string resources.

Android lint's HardcodedText only covers XML layouts, so Compose code needs this check.
Heuristic: a string literal containing a word (2+ letters) passed to a UI text slot, e.g.
  Text("Save")   contentDescription = "Delete"   title = "Trash"   Toast.makeText(ctx, "Saved", ...)
Silence a deliberate literal (unit, technical term, identical in every language) by putting
`// i18n-ignore` on the same line.

Usage: check_hardcoded_strings.py <source dir> [<source dir> ...]   exit code 1 on findings
"""
import pathlib
import re
import sys

# A literal with at least one word of 2+ letters; template expressions ($x, ${x}) don't count as words.
_WORD = re.compile(r"[A-Za-zÄÖÜäöüß]{2,}")
_LITERAL = r'"((?:[^"\\]|\\.)*)"'
_PATTERNS = [
    re.compile(r"\bText\(\s*" + _LITERAL),
    re.compile(r"\b(?:text|title|label|contentDescription|placeholder|message|confirmText|dismissText)\s*=\s*" + _LITERAL),
    re.compile(r"\bmakeText\([^,]+,\s*" + _LITERAL),
    re.compile(r"\.set(?:ContentTitle|ContentText|Ticker)\(\s*" + _LITERAL),
]


def _has_words(literal: str) -> bool:
    without_templates = re.sub(r"\$\{[^}]*\}|\$[A-Za-z_]\w*", "", literal)
    return bool(_WORD.search(without_templates))


def check(paths):
    findings = []
    for root in paths:
        for file in sorted(pathlib.Path(root).rglob("*.kt")):
            for number, line in enumerate(file.read_text(encoding="utf-8-sig").splitlines(), 1):
                if "i18n-ignore" in line or line.lstrip().startswith(("//", "*", "/*")):
                    continue
                for pattern in _PATTERNS:
                    match = pattern.search(line)
                    if match and _has_words(match.group(1)):
                        findings.append(f"{file}:{number}: hardcoded UI text \"{match.group(1)}\"")
                        break
    return findings


if __name__ == "__main__":
    if len(sys.argv) < 2:
        sys.exit(__doc__)
    results = check(sys.argv[1:])
    for finding in results:
        print(finding)
    if results:
        print(f"\n{len(results)} hardcoded UI string(s). Move them to res/values/strings*.xml "
              "or mark a deliberate literal with // i18n-ignore.")
        sys.exit(1)
    print("No hardcoded UI strings found.")
