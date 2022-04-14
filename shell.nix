{ pkgs ? import <nixpkgs> { config.android_sdk.accept_license = true; } }:

let
  androidComposition = pkgs.androidenv.composeAndroidPackages {
    toolsVersion = "26.1.1";
    platformToolsVersion = "31.0.3";
    buildToolsVersions = [ "31.0.0" ];
    includeEmulator = false;
    emulatorVersion = "30.9.0";
    platformVersions = [ "31" ];
    includeSources = false;
    includeSystemImages = false;
    systemImageTypes = [ ];
    abiVersions = [ ];
    cmakeVersions = [ ];
    includeNDK = false;
    ndkVersions = [ ];
    useGoogleAPIs = false;
    useGoogleTVAddOns = false;
    includeExtras = [ ];
  };
in pkgs.mkShell rec {
  ANDROID_SDK_ROOT = "${androidComposition.androidsdk}/libexec/android-sdk";
  buildInputs = with pkgs; [ gradle glibc zlib ];
}
