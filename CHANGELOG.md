# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixes

- Add a workaround for rare crashes while loading SQLite with older devices
- Fix comments page resetting collapsed state while scrolling

## [1.51.0] - 2024-09-22

### Fixed

- Pull to refresh indicator is no longer hiding behind the top app bar
- User profile page correctly renders contents below the top app bar
- Revert unread comment handling changes that caused false positives

## [1.50.0] - 2024-08-30

### Fixed

- Search bar draws under the status bar when hiding keyboard
- Bottom of the comments page is cut off by the navigation bar
- Rare crash in comments page

## [1.49.0] - 2024-08-29

### Added

- Opening posts you have previously seen will show the number of new comments since last visit

### Fixed

- Saving posts no longer triggers a page refresh that invalidates scroll position

### Changed

- Change submitter text to 'authored' when applicable
- Unread comments now have a brighter background rather than a text badge
- Bottom navigation bar has been redesigned

## [1.48.0] - 2024-06-05

### Added

- Swiping a post from left to right now offers a share action

### Fixed

- Try to workaround an infrequent crash when scrolling hottest/newest posts

### Changed

- Upgrade to Kotlin 2.0.0
- Upgrade to Compose May beta releases

## [1.47.0] - 2024-05-14

### Changed

- Bring back dividers between posts (I regret my earlier choices)
- Upgrade to Compose May stable releases

### Fixed

- Navigating to user profiles now works when invoked from the search
  results page
- Fix occasional crashes due to the app incorrectly trying to open
  multiple database connections

## [1.46.0] - 2024-04-24

### Changed

- Remove dividers between posts
- Post titles in lists are now truncated to be a single line

### Fixed

- Disable logging of network errors to Sentry
- Add potential workaround for navigation-related crashes

## [1.45.0] - 2024-04-24

### Changed

- Upgrade to Compose April releases
- Story items are now more compact so you can see more items
  on your screen.

### Fixed

- Fixed a crash when clicking an item on the bottom navigation bar
  too quickly
- Removed buggy deeplinks
- Clicking a username now correctly navigates to the right page in-app

## [1.44.0] - 2024-03-19

### Fixed

- Fixed a bug in the database code that was triggering crashes for
  a subset of upgrading users

## [1.43.0] - 2024-03-17

### Fixed

- Fix crash when trying to open a comments-only post from
  the home screen widget

## [1.42.0] - 2024-03-16

### Fixed

- Adapt to changes in lobste.rs API

## [1.41.0] - 2024-03-07

### Fixed

- Downgrade dependency that was pulling in extraneous storage
  permissions which the app does not require.

## [1.40.0] - 2024-03-07

### Changed

- Update to Compose March releases
- Improve performance of frequently invoked database queries
- Refactor UI data model to improve state handling
- Make saved posts update in the background more often
- Consolidate widget display logic
- Rework some theming logic to align with new Material guidelines

## [1.39.0] - 2024-01-23

### Changed

- Add HTML bookmarks as an export format
- Rework widget item layout for consistent touch targets
- Add attribution for libraries used in the app
- Redesign settings screen
- Update to Compose December releases

## [1.38.0] - 2023-11-20

### Changed

- Add brand new icon by dzuk
- Fix a bug that caused the app to fetch the same 20 posts over and over
- Significantly improve scrolling performance when there are a lot of saved posts
- Move search feature to its own screen
- Make top app and bottom system bars use the same color
- Reduce unnecessary API calls in search screen
- Upgrade to Compose November releases
- Enable logging of SQLite queries

## [1.37.0] - 2023-10-06

### Changed

- Fixed a crash that infrequently happened in the comments page
- Fixed a crash when trying to list saved posts
- Fixed a crash in home screen widget when user had less than 50 saved posts
- Upgraded to Compose 1.6.0-alpha07

## [1.36.0] - 2023-10-04

### Added

- Introduce an initial attempt at a home screen widget

### Changed

- Fixed a crash triggered when swiping items
- Adjusted pull to refresh component to match Material You theme

## [1.35.0] - 2023-09-19

### Changed

- Fixed missing vertical spacing between post tags
- Fixed user profile links not displaying correctly in-app
- Update to Compose October release

## [1.34.0] - 2023-08-30

### Added

- Add a swipe action on each story to open the comments page on `lobste.rs`
- Automatically mark posts as "read" and visually distinguish between them

### Changed

- Tweak list items to reduce vertical size and adjust colors
- Drop bogus workarounds for native library crashes
- Updated Jetpack Compose and SQLite

### Fixed

- Rework how comments are displayed to adapt to lobste.rs API change

## [1.33.0] - 2023-07-31

### Changed

- Yet another attempt at fixing native library crashes
- Rework how baseline profiles are generated

## [1.32.0] - 2023-07-27

### Changed

- Upgrade to AndroidX July 26 release
- Revert selectable text feature due to sporadic crashes

## [1.31.0] - 2023-07-25

### Added

- Added the ability to search for posts
- Text in the comments page is now selectable

### Changed

- Upgrade to Compose July release
- Upgrade to Kotlin 1.9.0

## [1.30.0] - 2023-07-02

### Changed

- Added another workaround for native library loading crash

## [1.29.0] - 2023-06-08

### Added

- Backup and restore options for saved posts

### Fixed

- Spamming the comments button no longer causes it to be opened multiple times
- Saved posts screen now has a visual indication when you have nothing saved
- Iconography has been updated across the board to be more consistent

## [1.28.0] - 2023-06-03

### Changed

- Navigation transitions have been slightly sped up

### Fixed

- Add workaround for a native library loading crash observed on some devices

## [1.27.0] - 2023-05-31

### Changed

- Small accessibility improvements
- Slightly tweak the layout for story items to take less vertical space
- Upgrade to Compose `1.5.0-beta01` release
- Set accessibility web URI for profile screen
- Directly use Material Icons from upstream artifacts
- Upgrade dependencies
- Add adaptive navigation support (thanks @Yash-Garg)
- Use latest SQLite for backing databases

## [1.26.0] - 2023-05-03

### Changed

- Upgrade to Compose May release
- Disable Sentry performance reporting
- Migrate to Compose Foundation `FlowRow` in `LobstersCard`

## [1.25.0] - 2023-04-18

### Changed

- Make the app draw edge to edge
- Simplify Top App Bar color scheme
- Rework how baseline profiles are generated
- Upgrade to Compose April release

## [1.24.0] - 2023-03-24

### Changed

- Rebuild app icon assets to align better with Material guidelines
- Adopt Slack's Compose lints and address issues found by it
- Upgrade to OkHttp 4.x
- Upgrade to Compose March release
- Start work on support for logging in with lobste.rs

## [1.23.0] - 2023-03-02

### Added

- Introduce [Sentry](https://sentry.io) for error reporting and performance monitoring

## [1.22.0] - 2023-03-02

### Changed

- In-app browser now respects the user's choice for dark mode
- Upgrade dependencies

## [1.21.0] - 2023-02-09

### Changed

- Fix bug that caused the app to crash on launch

## [1.20.0] - 2023-02-09

### Changed

- Upgrade dependencies

## [1.19.0] - 2023-02-01

### Changed

- Upgrade dependencies
- Optimize packaged baseline profiles

## [1.18.0] - 2023-01-21

### Changed

- Special-case deleted stories in comments API
- Fix regression where save button had the incorrect visual state

## [1.17.0] - 2023-01-12

### Added

- The app will now mark new comments as unread when returning to posts

### Changed

- Upgrade to Kotlin 1.8.0
- Revert questionable app bar scrolling behaviour change

## [1.16.0] - 2022-12-24

### Changed

- Fix bug where newest posts tab actually showed hottest posts instead

## [1.15.0] - 2022-12-20

### Changed

- Fix a case where collapsing the parent of an already collapsed comment caused it to become expanded
- Tweak progress bar UI to use a linear indicator
- Allow app bar to hide when scrolling down
- Fix bug where refreshing a list of posts caused it to jump around multiple times

## [1.14.0] - 2022-12-12

### Changed

- Rework how often saved posts are updated
- Collapsing a comment now collapses all comments under it similar to how it works on the website

## [1.13.0] - 2022-12-10

### Changed

- Fix crash when viewing [jcs](https://lobste.rs/u/jcs)' comments

## [1.12.0] - 2022-12-09

### Changed

- Do not schedule post update job every time the app starts
- Improve favicon loading to reduce unnecessary redraws
- Fix post lists being reloaded unnecessarily

## [1.11.0] - 2022-12-07

### Changed

- Upgrade to Compose 2022.12.00 release
- Refresh packaged baseline profile
- Improve how saved state of posts is queried

## [1.10.0] - 2022-11-30

### Changed

- Configure Coil image loader with better caching settings
- Refresh packaged baseline profile

## [1.9.0] - 2022-11-28

### Changed

- A loading indicator is added to the bottom of the post list to identify when new posts are being fetched

## [1.8.0] - 2022-11-21

### Changed

- The release pipeline now automatically generates Play Store release notes from the changelog file

### Fixed

- Downgrade AGP to 8.0.0-alpha07 to fix Baseline Profiles not being packaged

## [1.7.0] - 2022-11-19

### Changed

- Switch to Compose Material's swipe refresh feature
- Change metadata extractor logic to no longer prefer canonical URLs. The submitted URL will be retained as-is now.
- Remove navigation transition animations

## [1.6.0] - 2022-11-14

### Changed

- Migrate dependency injection from Hilt to Anvil + Whetstone

## [1.5.0] - 2022-11-10

### Changed

- Upgrade to Compose 2022.11.00 release

## [1.4.0] - 2022-11-02

### Changed

- Misc fixes to link metadata extractor

## [1.3.0] - 2022-10-24

### Changed

- Prevent errors during link metadata retrieval from crashing the app
- Update Jetpack libraries to 2022-10-24 release

## [1.2.0] - 2022-10-17

### Changed

- Lazily load link metadata to improve comment page loading speed
- Upgrade to latest Compose release
- Refactor code to align with Twitter's public Compose guidelines

## [1.1.0] - 2022-09-30

### Added

- Add score and relative time to comments

### Changed

- Update Jetpack Compose
- Declare data backup and transfer rules
- Improve legibility of links in comments and user profiles
- Commonize and improve how network errors are displayed

## [1.0.0] - 2022-09-20

- Initial Play Store release

[Unreleased]: https://github.com/msfjarvis/compose-lobsters/compare/v1.51.0...HEAD
[1.51.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.50.0...v1.51.0
[1.50.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.49.0...v1.50.0
[1.49.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.48.0...v1.49.0
[1.48.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.47.0...v1.48.0
[1.47.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.46.0...v1.47.0
[1.46.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.45.0...v1.46.0
[1.45.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.44.0...v1.45.0
[1.44.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.43.0...v1.44.0
[1.43.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.42.0...v1.43.0
[1.42.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.41.0...v1.42.0
[1.41.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.40.0...v1.41.0
[1.40.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.39.0...v1.40.0
[1.39.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.38.0...v1.39.0
[1.38.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.37.0...v1.38.0
[1.37.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.36.0...v1.37.0
[1.36.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.35.0...v1.36.0
[1.35.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.34.0...v1.35.0
[1.34.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.33.0...v1.34.0
[1.33.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.32.0...v1.33.0
[1.32.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.31.0...v1.32.0
[1.31.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.30.0...v1.31.0
[1.30.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.29.0...v1.30.0
[1.29.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.28.0...v1.29.0
[1.28.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.27.0...v1.28.0
[1.27.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.26.0...v1.27.0
[1.26.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.25.0...v1.26.0
[1.25.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.24.0...v1.25.0
[1.24.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.23.0...v1.24.0
[1.23.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.22.0...v1.23.0
[1.22.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.21.0...v1.22.0
[1.21.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.20.0...v1.21.0
[1.20.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.19.0...v1.20.0
[1.19.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.18.0...v1.19.0
[1.18.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.17.0...v1.18.0
[1.17.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.16.0...v1.17.0
[1.16.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.15.0...v1.16.0
[1.15.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.14.0...v1.15.0
[1.14.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.13.0...v1.14.0
[1.13.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.12.0...v1.13.0
[1.12.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.11.0...v1.12.0
[1.11.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.10.0...v1.11.0
[1.10.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.9.0...v1.10.0
[1.9.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.8.0...v1.9.0
[1.8.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.7.0...1.8.0
[1.7.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.6.0...v1.7.0
[1.6.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.5.0...v1.6.0
[1.5.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.4.0...v1.5.0
[1.4.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.3.0...v1.4.0
[1.3.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.2.0...v1.3.0
[1.2.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/msfjarvis/compose-lobsters/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/msfjarvis/compose-lobsters/compare/29c374859b17c5fcef03585b8a01c00070de9097...v1.0.0
