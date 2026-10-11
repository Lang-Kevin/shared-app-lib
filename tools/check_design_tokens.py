#!/usr/bin/env python3
"""Flags Compose/Kotlin UI code that bypasses the design tokens (see DESIGN.md).

Rules:
  raw-color      Color(0x...), Color.White/Black/Red/... (Transparent/Unspecified are fine)
  raw-text-size  12.sp literals, fontSize =            -> MaterialTheme.typography.*
  raw-shape      RoundedCornerShape(                   -> MaterialTheme.shapes.* / AppTheme.pillShape
  raw-spacing    literal .dp inside padding( / spacedBy( / Spacer(   -> AppTheme.spacing.*
  glow           .shadow( with primary/accent, BlurMaskFilter, .blur(   (Night Drive: no halos)
Skipped: **/ui/theme/**, src/test, src/androidTest, comment lines, lines containing `// design-ignore`.

Heuristic, line/regex-based: no Kotlin parser. raw-spacing walks paren depth from each
padding( / spacedBy( / Spacer( call (across lines) and flags .dp literals inside it; comment
lines are blanked for that scan, so parens in strings or trailing comments can skew it.

Usage: check_design_tokens.py [--warn] <dir-or-file> [...]
  exit 1 on findings; --warn prints the same findings but exits 0.
  exit 2 if a path does not exist (even with --warn).
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
    ("raw-spacing", None),  # span-based, see _spacing_lines
    ("glow", re.compile(r"\.shadow\([^)]*\b(?:primary|accent)|BlurMaskFilter|\.blur\(")),
]
_EXCLUDED = ("/ui/theme/", "/src/test/", "/src/androidTest/")
_SPACING_CALL = re.compile(r"\b(?:padding|spacedBy|Spacer)\(")
_DP = re.compile(r"\b\d+(?:\.\d+)?\.dp\b")


def _spacing_lines(text):
    """1-based line numbers of .dp literals inside the argument list of a spacing call."""
    lines = set()
    for call in _SPACING_CALL.finditer(text):
        open_at = call.end() - 1
        depth = 0
        close_at = len(text) - 1
        for i in range(open_at, len(text)):
            if text[i] == "(":
                depth += 1
            elif text[i] == ")":
                depth -= 1
                if depth == 0:
                    close_at = i
                    break
        for lit in _DP.finditer(text, open_at, close_at):
            lines.add(text.count("\n", 0, lit.start()) + 1)
    return lines


def _files(paths):
    for root in map(pathlib.Path, paths):
        candidates = [root] if root.is_file() else sorted(root.rglob("*.kt"))
        for file in candidates:
            if not any(part in file.resolve().as_posix() for part in _EXCLUDED):
                yield file


def check(paths):
    findings = []
    for file in _files(paths):
        lines = file.read_text(encoding="utf-8-sig", errors="replace").split("\n")
        code = "\n".join("" if l.lstrip().startswith(("//", "*", "/*")) else l for l in lines)
        spacing = _spacing_lines(code)
        for number, line in enumerate(lines, 1):
            if "design-ignore" in line or line.lstrip().startswith(("//", "*", "/*")):
                continue
            for rule, pattern in _RULES:
                hit = number in spacing if pattern is None else pattern.search(line)
                if hit:
                    findings.append((file, number, rule, line.strip()))
    return findings


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--warn", action="store_true", help="report findings but exit 0")
    parser.add_argument("paths", nargs="+")
    args = parser.parse_args(argv)
    missing = [p for p in args.paths if not pathlib.Path(p).exists()]
    if missing:
        for p in missing:
            print(f"error: path not found: {p}", file=sys.stderr)
        return 2
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
