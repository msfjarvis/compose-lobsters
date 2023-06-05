# Claw for [lobste.rs](https://lobste.rs) [![CI](https://github.com/msfjarvis/compose-lobsters/actions/workflows/ci.yml/badge.svg)](https://github.com/msfjarvis/compose-lobsters/actions/workflows/ci.yml)

Unofficial Android app for read-only access to [lobste.rs](https://lobste.rs), built with [Jetpack Compose](https://developer.android.com/jetpack/compose).

<a href="https://play.google.com/store/apps/details?id=dev.msfjarvis.claw.android">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png"
       alt="Get it on Google Play"
       height="80" />
</a>

> Snapshots from the development branch can be obtained [here](https://github.com/msfjarvis/compose-lobsters/releases/tag/nightly).

<img src="https://github.com/msfjarvis/compose-lobsters/blob/main/.github/readme_feature.webp"
     alt="Side by side screenshots of the app's main page in dark and light UI modes"
     height="550" />

## Dependency Diagram

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR

  benchmark --> android
  android --> api
  android --> common
  android --> core
  android --> coroutine-utils
  android --> database
  android --> metadata-extractor
  android --> model
  api --> model
  common --> core
  common --> database
  common --> model
  metadata-extractor --> model

```
## License

See [LICENSE](LICENSE)