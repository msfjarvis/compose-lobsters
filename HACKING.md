# Hacking on compose-lobsters

This app is built as a very lightweight frontend for [lobste.rs](https://lobste.rs), mainly as an experiment in using [Jetpack Compose](https://d.android.com/jetpack/compose).

The codebase is fairly simple, and the package structure follows [Buffer's](https://buffer.com/resources/android-rethinking-package-structure/).

We use screenshot testing for our Android UI, and a script is included in the `scripts` directory to create an emulator that will exactly match the one in our CI so you can deterministically replicate the setup for locally running tests.

## Subproject breakdown

- `:api`: Retrofit-backed interface to the [lobste.rs] API

- `:app`: The guts of the Android client

- `:common`: Contains the abstractions we use for sharing strings and browser handling across desktop and mobile

- `:database`: Database layer implemented using SQLDelight to persist saved posts

- `:desktop`: Proof-of-concept desktop client that needs significantly more love than it has received so far
