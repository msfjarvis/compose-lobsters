#!/usr/bin/env python3

import sys
import subprocess
import shutil
from pathlib import Path


def main():
    if len(sys.argv) != 4:
        raise RuntimeError(
            "USAGE: encrypt-secret.py <input file> <output file> <encryption key>"
        )
    input_file = Path(sys.argv[1])
    output_file = Path(sys.argv[2])
    age_key = sys.argv[3]
    if shutil.which("age") is None:
        raise RuntimeError("age not installed")
    if not input_file.exists():
        raise RuntimeError(f"Input file '{input_file.name}' does not exist")
    recipient = subprocess.run(
        ["age-keygen", "-y"], capture_output=True, text=True, input=age_key
    ).stdout.strip()
    subprocess.run(
        ["age", "--encrypt", "-r", recipient, "-o", output_file],
        input=input_file.read_bytes(),
    )


if __name__ == "__main__":
    main()
