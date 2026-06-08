# Releasing an update for the Zipline module

1. Set `ZIPLINE_SIGNING_KEY` in environment
2. Build the production JS bundle: `gradle :zipline-parser:jsTest :zipline-parser:compileProductionExecutableKotlinJsZipline`
3. Zip up the files at `zipline-parser/build/zipline/Production/` and upload them to wailord.
   1. `zip -r -j zipline-release.zip zipline-parser/build/zipline/Production/`
4. On wailord unzip the folder then run the deployment script from the Claw repo
   1. `unzip -d release zipline-release.zip`
   2. `scripts/deploy-zipline-parser.sh release ./release /var/lib/claw-deploy/`
