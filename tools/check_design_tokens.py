#!/usr/bin/env python3
"""Flags Compose/Kotlin UI code that bypasses the design tokens (see DESIGN.md).

Rules:
  raw-color      Color(0x...), Color.White/Black/Red/... (Transparent/Unspecified are fine)
  raw-text-size  12.sp literals, fontSize =            -> MaterialTheme.typography.*
  raw-shape      RoundedCornerShape(                   -> MaterialTheme.shapes.* / AppTheme.pillShape
  raw-spacing    literal .dp inside padding( / spacedBy( / Spacer(   -> AppTheme.spacing.*
  glow           .shadow( with primary/accent, BlurMaskFilter, .blur(   (Night Drive: no halos)
Skipped: **/ui/theme/**, src/test, src/androidTest, comment lines, lines containing `// design-ignore`.

Usage: check_design_tokens.py [--warn] <dir-or-file> [...]
  exit 1 on findings; --warn prints the same findings but exits 0.
"""
import argparse
import pathlib
import re
import sys

_RULES = [
    ("raw-color", re.compile(
        r"\bColor\(\s*0x|\bColor\.(?:White|Black|Red|Green|Blue|Yellow|Gray|LightGray|DarkGray|Cyan|Magenta)\b")),
    ("raw-text-size", re.compile(r"\b\d+(?:\.\d+)?\.sp\b|\bfontSize\s*=")),
    ("raw-shape", re.compile(r"\bRoundedCornerShape\(")),
    ("raw-spacing", re.compile(r"\b(?:padding|spacedBy|Spacer)\([^)]*?\b\d+(?:\.\d+)?\.dp\b")),
    ("glow", re.compile(r"\.shadow\([^)]*\b(?:primary|accent)|BlurMaskFilter|\.blur\(")),
]
_EXCLUDED = ("/ui/theme/", "/src/test/", "/src/androidTest/")


def _files(paths):
    for root in map(pathlib.Path, paths):
        candidates = [root] if root.is_file() else sorted(root.rglob("*.kt"))
        for file in candidates:
            if not any(part in "/" + file.as_posix() for part in _EXCLUDED):
                yield file


def check(paths):
    findings = []
    for file in _files(paths):
        for number, line in enumerate(file.read_text(encoding="utf-8-sig").splitlines(), 1):
            if "design-ignore" in line or line.lstrip().startswith(("//", "*", "/*")):
                continue
            for rule, pattern in _RULES:
                if pattern.search(line):
                    findings.append((file, number, rule, line.strip()))
    return findings


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--warn", action="store_true", help="report findings but exit 0")
    parser.add_argument("paths", nargs="+")
    args = parser.parse_args(argv)
    results = check(args.paths)
    for file, number, rule, snippet in results:
        print(f"{file}:{number}: [{rule}] {snippet}")
    if not results:
        print("No design token violations found.")
        return 0
    print(f"\n{len(results)} design token violation(s). Use MaterialTheme / AppTheme tokens (DESIGN.md) "
          "or mark a deliberate exception with // design-ignore: <reason>.")
    return 0 if args.warn else 1


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
