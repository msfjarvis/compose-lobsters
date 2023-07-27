#!/usr/bin/env python3

import subprocess
import os
from typing import Optional
from pathlib import Path
import glob
import tempfile

NIGHTLY_TAG = "nightly"


def exec(command_str: str, shell: bool = False) -> Optional[int]:
    print(f"Executing '{command_str}'")
    proc = None
    if shell:
        proc = subprocess.run(command_str, text=True, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    else:
        proc = subprocess.run(
            command_str.split(" "), text=True, shell=False, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
        )
    result = proc.returncode
    print(f"{proc.stdout}")
    return result


def exec_out(command_str: str, shell: bool = False) -> str:
    print(f"Executing '{command_str}'")
    proc = None
    if shell:
        proc = subprocess.run(command_str, capture_output=True, text=True, shell=True)
    else:
        proc = subprocess.run(
            command_str.split(" "), capture_output=True, text=True, shell=False
        )
    data = proc.stdout
    return data


def get_current_rev() -> str:
    return exec_out("git rev-parse --short HEAD")


def get_asset_directory() -> Path:
    workspace = os.getenv("GITHUB_WORKSPACE")
    return Path(workspace) / "android" / "apk"


def overwrite_local_tag():
    exec(f"git tag -f {NIGHTLY_TAG} -m 'Nightly release'", shell=True)


def overwrite_remote_tag():
    exec(f"git push -f origin {NIGHTLY_TAG}")


def has_release() -> bool:
    result = exec(f"gh release view {NIGHTLY_TAG}")
    return result is not None and result == 0


def delete_release():
    exec(f"gh release delete --yes {NIGHTLY_TAG}")


def create_release():
    with tempfile.NamedTemporaryFile("w+") as cf:
        cf.write(f"Latest release for Claw from revision {get_current_rev()}")
        cf.flush()
        cwd = os.getcwd()
        os.chdir(get_asset_directory())
        apks = " ".join(glob.glob("*.apk"))
        exec(
            "gh release create --prerelease "
            +f"--title 'Latest snapshot build' --notes-file {cf.name} "
            +f"{NIGHTLY_TAG} {apks}",
            shell=True
        )
        os.chdir(cwd)


def main():
    overwrite_local_tag()
    if has_release():
        delete_release()
    overwrite_remote_tag()
    create_release()


if __name__ == "__main__":
    main()
