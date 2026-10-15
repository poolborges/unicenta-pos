#!/usr/bin/env python3
"""
add_root_padding.py  --  KriolOS POS / dev-scripts (git-ignored)
-----------------------------------------------------------------
Inserts  setBorder(javax.swing.BorderFactory.createEmptyBorder(8,8,8,8))
as the FIRST statement inside initDomainAdapters() for every target
Panel class that does not yet have a root-level EmptyBorder.

UI/UX Rule: No panel content may touch container edges.
Documented in: docs/modules/guide-devel/pages/ui-modal-architecture-migration.adoc

Usage:
  python3 dev/add_root_padding.py --dry-run   # preview only
  python3 dev/add_root_padding.py             # apply
"""
import re, sys
from pathlib import Path

WORKSPACE = Path("/home/dev/PDEV/kriolos-obiz/kriolos-pos")

TARGETS = [
    "kriolos-opos-base/src/main/java/com/openbravo/beans/JNumberPanel.java",
    "kriolos-opos-base/src/main/java/com/openbravo/beans/JEditorTextPanel.java",
    "kriolos-opos-base/src/main/java/com/openbravo/beans/DinerNumberPanel.java",
    "kriolos-opos-base/src/main/java/com/openbravo/beans/JCalendarDlgPanel.java",
    "kriolos-opos-base/src/main/java/com/openbravo/data/gui/JFindPanel.java",
    "kriolos-opos-base/src/main/java/com/openbravo/data/gui/JSortPanel.java",
    "kriolos-opos-base/src/main/java/com/openbravo/data/gui/JMessagePanel.java",
]

BORDER_CALL = "        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));\n"
METHOD_PAT = re.compile(r"\s*private\s+void\s+initDomainAdapters\s*\(\s*\)\s*\{")

def has_root_border(snippet):
    for line in [l.strip() for l in snippet.splitlines() if l.strip()][:5]:
        if line.startswith("setBorder(") or line.startswith("this.setBorder("):
            return True
    return False

def patch(path, dry_run):
    src = path.read_text(encoding="utf-8")
    m = METHOD_PAT.search(src)
    if not m:
        return f"  SKIP  {path.name} — no initDomainAdapters()"
    pos = src.index("{", m.start()) + 1
    if has_root_border(src[pos:pos+400]):
        return f"  OK    {path.name} — already has root EmptyBorder"
    patched = src[:pos] + "\n" + BORDER_CALL + src[pos:]
    if not dry_run:
        path.write_text(patched, encoding="utf-8")
        return f"  FIXED {path.name}"
    return f"  DRY   {path.name} — would insert EmptyBorder(8,8,8,8)"

def main():
    dry = "--dry-run" in sys.argv
    print(f"\n=== add_root_padding [{'DRY RUN' if dry else 'APPLY'}] ===\n")
    results = []
    for rel in TARGETS:
        p = WORKSPACE / rel
        results.append(patch(p, dry) if p.exists() else f"  MISS  {p.name}")
    [print(r) for r in results]
    print(f"\n{sum('FIXED' in r for r in results)} patched, {sum('MISS' in r or 'SKIP' in r for r in results)} skipped.\n")

if __name__ == "__main__":
    main()
