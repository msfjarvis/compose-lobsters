#!/usr/bin/env python3
"""Record a predictive-back gesture. Run with --help for options."""

import argparse
import math
import os
import pty
import re
import select
import shutil
import signal
import subprocess
import sys
import time


def main():
    parser = argparse.ArgumentParser(description=__doc__, epilog="Requires adb, scrcpy, and ffmpeg with libvpx-vp9.")
    parser.add_argument("output", help="output recording (.mp4 or .mkv); must not exist")
    parser.add_argument("-s", "--serial", help="adb device serial (or set ANDROID_SERIAL)")
    parser.add_argument("--hold", type=float, default=4, help="hold duration in seconds (default: 4)")
    args = parser.parse_args()
    if not 0 < args.hold <= 60:
        parser.error("--hold must be greater than 0 and at most 60 seconds")
    if not args.output.lower().endswith((".mp4", ".mkv")):
        parser.error("output must end in .mp4 or .mkv")
    webm_output = os.path.splitext(args.output)[0] + ".webm"
    if os.path.exists(args.output) or os.path.exists(webm_output):
        parser.error("output or corresponding .webm already exists")
    if not shutil.which("ffmpeg"):
        parser.error("ffmpeg is required to generate the WebM")

    serial = args.serial or os.environ.get("ANDROID_SERIAL")
    adb = ["adb"] + (["-s", serial] if serial else [])

    def device(*command):
        return subprocess.check_output(adb + list(command), text=True)

    sizes = re.findall(r"(?:Physical|Override) size:\s*(\d+)x(\d+)", device("shell", "wm", "size"))
    rotation = re.search(r"\bmRotation=(\d)", device("shell", "dumpsys", "window"))
    if not sizes or not rotation:
        raise RuntimeError("Could not determine display size and rotation")
    width, height = map(int, sizes[-1])
    if int(rotation.group(1)) in (1, 3):
        width, height = height, width
    x, y = max(1, width * 80 // 100), height // 2
    print(f"Display: {width}x{height}; gesture: (1, {y}) to ({x}, {y})", flush=True)
    print("Use gesture navigation and open the screen to record before running this script.", flush=True)

    scale_setting = device("shell", "settings", "get", "global", "window_animation_scale").strip()
    try:
        scale = 1.0 if scale_setting == "null" else float(scale_setting)
    except ValueError:
        raise RuntimeError(f"Invalid window animation scale: {scale_setting!r}") from None
    if not math.isfinite(scale) or scale <= 0:
        raise RuntimeError("Window animation scale must be greater than zero; animations are disabled")

    command = ["scrcpy"] + (["--serial", serial] if serial else [])
    # scrcpy's own time limit finalizes the recording even when headless
    # scrcpy does not respond to SIGINT (observed with scrcpy 5 on macOS).
    duration = math.ceil(args.hold + 4 * scale)
    command += ["--no-playback", "--no-audio", "--record", args.output, f"--time-limit={duration}"]
    master, slave = pty.openpty()
    try:
        recording = subprocess.Popen(command, stdout=slave, stderr=slave)
    finally:
        os.close(slave)
    try:
        # A terminal makes scrcpy flush its readiness message immediately.
        deadline = time.monotonic() + 30
        output = b""
        while b"Recording started" not in output:
            if time.monotonic() >= deadline or recording.poll() is not None:
                raise RuntimeError("scrcpy did not start recording")
            if select.select([master], [], [], 0.2)[0]:
                output += os.read(master, 4096)
        time.sleep(1)
        # A single shell keeps the touch down between events. input swipe cannot
        # pause at its destination: it releases immediately after its final move.
        events = [f"input touchscreen motionevent DOWN 1 {y}"]
        for step in range(1, 6):
            events.append(f"input touchscreen motionevent MOVE {1 + (x - 1) * step // 5} {y}")
        events.append(f"sleep {args.hold}")
        for step in range(4, -1, -1):
            events.append(f"input touchscreen motionevent MOVE {1 + (x - 1) * step // 5} {y}")
        events.append(f"input touchscreen motionevent UP 1 {y}")
        subprocess.run(adb + ["shell", " && ".join(events)], check=True)
        if recording.poll() is not None:
            raise RuntimeError("scrcpy stopped before the gesture finished")
        print("Waiting for scrcpy to finalize the recording...", flush=True)
        recording.wait(timeout=duration + 15)
    finally:
        os.close(master)
        if recording.poll() is None:
            recording.send_signal(signal.SIGINT)
            try:
                recording.wait(timeout=5)
            except subprocess.TimeoutExpired:
                recording.kill()
                recording.wait()
    if recording.returncode:
        raise RuntimeError(f"scrcpy exited with status {recording.returncode}")
    print(f"Saved {args.output}", flush=True)
    print(f"Encoding {webm_output}...", flush=True)
    subprocess.run([
        "ffmpeg", "-v", "error", "-n", "-i", args.output,
        "-an", "-c:v", "libvpx-vp9", "-pix_fmt", "yuv420p",
        "-b:v", "0", "-crf", "32", "-deadline", "good",
        "-cpu-used", "2", "-row-mt", "1", webm_output,
    ], check=True)
    print(f"Saved {webm_output}")


if __name__ == "__main__":
    try:
        main()
    except (OSError, subprocess.CalledProcessError, RuntimeError) as error:
        print(f"error: {error}", file=sys.stderr)
        sys.exit(1)
