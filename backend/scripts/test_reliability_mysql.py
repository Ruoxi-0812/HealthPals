#!/usr/bin/env python3
"""Start a disposable loopback MySQL, run reliability tests, then remove its data.

Requires local mysqld and mysqladmin executables. No existing MySQL server is used.
"""
import argparse
import pathlib
import socket
import subprocess
import tempfile
import time

parser = argparse.ArgumentParser()
parser.add_argument("--mysql-bin", default="/opt/homebrew/opt/mysql/bin")
parser.add_argument("--tests", default="HealthSubmissionReliabilityTest")
args = parser.parse_args()
bin_dir = pathlib.Path(args.mysql_bin)
server = bin_dir / "mysqld"
admin = bin_dir / "mysqladmin"
if not server.is_file() or not admin.is_file():
    parser.error("Set --mysql-bin to a directory containing mysqld and mysqladmin")
root = pathlib.Path(__file__).resolve().parents[2]
print(subprocess.check_output([str(server), "--version"], text=True).strip(), flush=True)
with tempfile.TemporaryDirectory(prefix="healthpals-mysql-") as temporary:
    directory = pathlib.Path(temporary)
    data = directory / "data"
    socket_path = directory / "mysql.sock"
    log_path = directory / "mysql.log"
    with socket.socket() as probe:
        probe.bind(("127.0.0.1", 0))
        port = probe.getsockname()[1]
    initialize = subprocess.run([str(server), "--no-defaults", "--initialize-insecure",
                                 "--datadir=" + str(data)], capture_output=True, text=True)
    if initialize.returncode:
        raise RuntimeError(initialize.stderr)
    with log_path.open("w") as log:
        process = subprocess.Popen([str(server), "--no-defaults", "--datadir=" + str(data),
                                    "--socket=" + str(socket_path), "--port=" + str(port),
                                    "--bind-address=127.0.0.1", "--mysqlx=0",
                                    "--pid-file=" + str(directory / "mysql.pid")], stdout=log, stderr=log)
        try:
            for _ in range(150):
                if process.poll() is not None:
                    raise RuntimeError(log_path.read_text())
                ping = subprocess.run([str(admin), "--no-defaults", "--socket=" + str(socket_path),
                                       "-uroot", "ping"], capture_output=True)
                if ping.returncode == 0:
                    break
                time.sleep(0.2)
            else:
                raise RuntimeError("Disposable MySQL did not start: " + log_path.read_text())
            print("Running against disposable MySQL on loopback port " + str(port), flush=True)
            result = subprocess.run(["mvn", "-q", "-f", str(root / "backend/pom.xml"),
                                     "-Dtest=" + args.tests,
                                     "-Dhealth.test.mysqlUrl=jdbc:mysql://127.0.0.1:" + str(port) + "/",
                                     "test"], cwd=root)
            if result.returncode:
                raise SystemExit(result.returncode)
        finally:
            process.terminate()
            try:
                process.wait(timeout=20)
            except subprocess.TimeoutExpired:
                process.kill()
                process.wait()
