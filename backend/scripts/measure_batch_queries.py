#!/usr/bin/env python3
"""Compare actual MyBatis statement counts against the pre-optimization code.

Runs only against disposable H2 databases. Does not start Spring or connect to RDS.
Requires Python 3, Git, Maven, and a JDK supported by the project.
"""
import pathlib
import re
import shutil
import subprocess
import tempfile

ROOT = pathlib.Path(__file__).resolve().parents[2]
BASELINE = "57a0d56405d5c34e96f4c07d650ac676611fc112"
FILES = (
    "src/main/java/cn/kmbeast/service/impl/UserHealthServiceImpl.java",
    "src/main/java/cn/kmbeast/mapper/HealthModelConfigMapper.java",
    "src/main/resources/mapper/HealthModelConfigMapper.xml",
)
TEST = "src/test/java/cn/kmbeast/service/impl/UserHealthBatchQueryTest.java"


def measure(directory, baseline):
    command = ["mvn", "-q", "-f", str(directory / "pom.xml"),
               "-Dtest=UserHealthBatchQueryTest"]
    if baseline:
        command.append("-Dbatch.baseline=true")
    result = subprocess.run(command + ["test"], capture_output=True, text=True)
    if result.returncode:
        raise RuntimeError(result.stdout + result.stderr)
    rows = re.findall(r"BATCH_METRIC records=(\d+) config_selects=(\d+) total_statements=(\d+)", result.stdout)
    if len(rows) != 3:
        raise RuntimeError("Expected three batch measurements; got: " + result.stdout)
    return {int(n): (int(selects), int(total)) for n, selects, total in rows}


with tempfile.TemporaryDirectory(prefix="healthpals-batch-") as temporary:
    baseline_dir = pathlib.Path(temporary) / "backend"
    shutil.copytree(ROOT / "backend" / "src", baseline_dir / "src")
    shutil.copy2(ROOT / "backend" / "pom.xml", baseline_dir / "pom.xml")
    for file in FILES:
        original = subprocess.check_output(["git", "show", BASELINE + ":backend/" + file], cwd=ROOT)
        (baseline_dir / file).write_bytes(original)
    test = baseline_dir / TEST
    source = test.read_text()
    start = source.index("    // BEGIN optimized-only regression tests")
    end = source.index("    // END optimized-only regression tests")
    test.write_text(source[:start] + source[end:])
    print("Measuring original implementation...", flush=True)
    before = measure(baseline_dir, True)
    print("Measuring optimized implementation and regression tests...", flush=True)
    after = measure(ROOT / "backend", False)
    print("records,config_selects_before,config_selects_after,total_sql_before,total_sql_after")
    for size in sorted(before):
        print(f"{size},{before[size][0]},{after[size][0]},{before[size][1]},{after[size][1]}")
