#!/usr/bin/env python3
"""Fixture tests for check_design_tokens.py. Run: python3 tools/test_check_design_tokens.py"""
import pathlib
import subprocess
import sys
import tempfile
import textwrap
import unittest

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import check_design_tokens  # noqa: E402

SCRIPT = pathlib.Path(__file__).resolve().parent / "check_design_tokens.py"

BAD = textwrap.dedent("""\
    val a = Color(0xFF112233)
    Text("x", color = Color.White)
    Text("x", fontSize = 12.sp)
    Card(shape = RoundedCornerShape(12.dp))
    Modifier.padding(horizontal = 16.dp)
    Spacer(Modifier.height(8.dp))
    Modifier.shadow(8.dp, spotColor = MaterialTheme.colorScheme.primary)
    Modifier.size(48.dp).background(Color.Transparent)
    // Color.White in a comment
    val c = Color.Black // design-ignore: video overlay
    Modifier.padding(AppTheme.spacing.lg)
    Text("x", style = MaterialTheme.typography.labelSmall, maxFontSize = style.fontSize)
""")

EXPECTED = [
    (1, "raw-color"), (2, "raw-color"), (3, "raw-text-size"), (4, "raw-shape"),
    (5, "raw-spacing"), (6, "raw-spacing"), (7, "glow"),
]


class CheckDesignTokensTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        root = pathlib.Path(self.tmp.name)
        self.src = root / "src"
        (self.src / "main/kotlin/app/ui/theme").mkdir(parents=True)
        (self.src / "test/kotlin/app").mkdir(parents=True)
        (self.src / "main/kotlin/app/Bad.kt").write_text(BAD, encoding="utf-8")
        (self.src / "main/kotlin/app/ui/theme/Colors.kt").write_text("val X = Color(0xFF000000)\n", encoding="utf-8")
        (self.src / "test/kotlin/app/BadTest.kt").write_text("val X = Color.White\n", encoding="utf-8")

    def tearDown(self):
        self.tmp.cleanup()

    def test_rules_and_line_numbers(self):
        found = [(line, rule) for _, line, rule, _ in check_design_tokens.check([self.src])]
        self.assertEqual(EXPECTED, found)

    def test_theme_and_test_sources_excluded(self):
        files = {f.name for f, _, _, _ in check_design_tokens.check([self.src])}
        self.assertEqual({"Bad.kt"}, files)

    def test_single_file_argument(self):
        found = check_design_tokens.check([self.src / "main/kotlin/app/Bad.kt"])
        self.assertEqual(len(EXPECTED), len(found))

    def test_exit_codes_and_output_format(self):
        strict = subprocess.run([sys.executable, str(SCRIPT), str(self.src)], capture_output=True, text=True)
        self.assertEqual(1, strict.returncode)
        self.assertRegex(strict.stdout, r"Bad\.kt:1: \[raw-color\] val a = Color\(0xFF112233\)")
        warn = subprocess.run([sys.executable, str(SCRIPT), "--warn", str(self.src)], capture_output=True, text=True)
        self.assertEqual(0, warn.returncode)
        self.assertIn("[raw-shape]", warn.stdout)

    def test_clean_tree_exits_zero(self):
        clean = self.src / "main/kotlin/app/ui/theme"
        result = subprocess.run([sys.executable, str(SCRIPT), str(clean)], capture_output=True, text=True)
        self.assertEqual(0, result.returncode)


if __name__ == "__main__":
    unittest.main()
