#!/usr/bin/env python3


import signal
import subprocess
import os
from pathlib import Path

emu_process = None


def signal_handler(sig, frame):
    if emu_process is not None:
        emu_process.signal(signal.SIGINT)


def make_cmdline_tools_path(android_home: Path) -> str:
    return android_home / "cmdline-tools" / "latest" / "bin"


def main():
    signal.signal(signal.SIGINT, signal_handler)
    android_home = os.getenv("ANDROID_HOME")
    if not android_home:
        raise RuntimeError("$ANDROID_HOME must be set to use this script")
    android_home = Path(android_home)
    api_level = os.getenv("ANDROID_API_LEVEL")
    if not api_level:
        print("$ANDROID_API_LEVEL not defined; defaulting to 33")
        api_level = "33"
    cmdline_path = make_cmdline_tools_path(android_home)
    image_package = f"system-images;android-{api_level};google_apis;x86_64"
    subprocess.run(
        [
            cmdline_path / "sdkmanager",
            image_package,
        ],
        check=True,
    )
    subprocess.run(
        [
            cmdline_path / "avdmanager",
            "create",
            "avd",
            "--force",
            "-n",
            f"Pixel_XL_API_{api_level}",
            "--abi",
            "google_apis/x86_64",
            "--package",
            image_package,
            "--device",
            "pixel_xl",
        ],
        check=True,
    )
    emu_process = subprocess.Popen(
        [
            android_home / "emulator" / "emulator",
            "-avd",
            f"Pixel_XL_API_{api_level}",
            "-gpu",
            "swiftshader_indirect",
            "-noaudio",
        ],
    )
    emu_process.wait()


if __name__ == "__main__":
    main()
