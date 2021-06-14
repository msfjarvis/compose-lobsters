# This is a nix-shell configuration to load nodejs in an impure shell, which
# is required by Spotless to format XML code.
with import <nixpkgs> { }; mkShell { buildInputs = [ nodejs-16_x ]; }
