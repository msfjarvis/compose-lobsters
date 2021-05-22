# Hacking on compose-lobsters

This app is built as a very lightweight frontend for [lobste.rs](https://lobste.rs), mainly as an experiment in using [Jetpack Compose](https://d.android.com/jetpack/compose).

The codebase is fairly simple, and the package structure follows [Buffer's](https://buffer.com/resources/android-rethinking-package-structure/).

## Subproject breakdown

- `:api`: Retrofit-backed interface to the [lobste.rs] API

- `:app`: The guts of the Android client

- `:common`: Contains the abstractions we use for sharing strings and browser handling across desktop and mobile

- `:database`: Database layer implemented using SQLDelight to persist saved posts

- `:desktop`: Proof-of-concept desktop client that needs significantly more love than it has received so far

## Handling Compose dependencies

The app currently uses JetBrains' [compose-jb](https://github.com/JetBrains/compose-jb) as the upstream for our builds of Compose, so each Compose-depending library we pull in must be added through the `composeDependency` helper rather than `implementation` directly.
