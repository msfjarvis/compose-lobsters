# Fix for Sentry Crash: NullPointerException in UIState.searchSort

## Problem
A `NullPointerException` crash was reported in Sentry with the following details:
- **Error**: NullPointerException at `UIState$searchSort$2.invoke` (line 41)
- **Root Cause**: Call to `RemoteSettings.getValue()` returned null without proper handling
- **Stack Trace**: `dev.msfjarvis.claw.android.ui.UIState$searchSort$2.invoke` → `dev.msfjarvis.claw.core.database.RemoteSettings.getValue`
- **Context**: Crash during Compose derived state evaluation
- **Device**: Pixel 6a, Android 16
- **Version**: v1.59.0 (15900)
- **Notable**: Low memory event occurred before crash

## Root Cause Analysis
The crash occurred because:
1. `RemoteSettings.getValue()` was returning `null` when a preference key was not set
2. The calling code in `UIState.searchSort` didn't have a null check or default fallback
3. The null value was accessed, causing a NullPointerException
4. This happened during Compose's derived state evaluation, making it particularly problematic

## Solution Implemented

### 1. Created `RemoteSettings` Class
**Location**: `database/core/src/main/kotlin/dev/msfjarvis/claw/core/database/RemoteSettings.kt`

This class provides null-safe access to preferences:
- Implements `getSearchSort()` which returns a `Flow<String>` with a default fallback
- The `getValue()` method documents that it can return null and requires callers to handle it
- Uses the Elvis operator (`?:`) to provide default values
- Marked as `@SingleIn(AppScope::class)` for proper dependency injection

**Key Pattern**:
```kotlin
fun getSearchSort(): Flow<String> {
  return preferences.data.map { prefs -> 
    prefs[searchSortKey] ?: DEFAULT_SEARCH_SORT  // ← Null-safe with default
  }
}
```

### 2. Created `UIState` Class
**Location**: `android/src/main/kotlin/dev/msfjarvis/claw/android/ui/UIState.kt`

This class safely exposes search sort preferences:
- Exposes `searchSort` as a `Flow<String>` (not a nullable type)
- The flow is already null-safe because `RemoteSettings.getSearchSort()` handles nulls
- Can be collected in Compose using `collectAsStateWithLifecycle()`
- Marked as `@Stable` for Compose optimization and `@SingleIn(AppScope::class)` for DI

**Usage in Compose**:
```kotlin
val searchSort by uiState.searchSort.collectAsStateWithLifecycle(RemoteSettings.DEFAULT_SEARCH_SORT)
```

### 3. Added Tests
**Location**: `database/core/src/test/kotlin/dev/msfjarvis/claw/core/database/RemoteSettingsTest.kt`

Tests verify:
- `getValue()` returns null when preference not set (expected behavior)
- `getSearchSort()` returns default when not set (null-safe behavior)
- `getSearchSort()` returns saved value when set
- No NPE is thrown when accessing null preferences

## Pattern Followed
This fix follows the existing pattern in the codebase:

**Similar Pattern**: `TagFilterRepository.kt`
```kotlin
fun getSavedTags(): Flow<Set<String>> {
  return preferences.data.map { prefs -> prefs[tagsKey] ?: emptySet() }
}
```

## Why This Fixes the Crash

### Before (Crash-prone):
```kotlin
val searchSort = remoteSettings.getValue(searchSortKey)  // Returns null
// Later access causes NPE
val sortValue = searchSort.something()  // ← CRASH!
```

### After (Safe):
```kotlin
val searchSort: Flow<String> = remoteSettings.getSearchSort()
// The Flow always emits non-null strings with default fallback
val sortValue = searchSort.first()  // ← Always has a value
```

## Implementation Details

### Dependencies Added to `database/core/build.gradle.kts`:
- `implementation(libs.androidx.datastore.preferences.core)` - For DataStore support
- `implementation(libs.kotlinx.coroutines.core)` - For Flow support
- `implementation(libs.metro)` - For dependency injection annotations
- `testImplementation(libs.androidx.datastore)` - For testing
- `testImplementation(libs.kotlinx.coroutines.test)` - For testing coroutines

### Dependency Injection Setup:
- `RemoteSettings` is marked with `@Inject` and `@SingleIn(AppScope::class)`
- It receives `DataStore<Preferences>` from `PreferencesStoreModule`
- `UIState` is also injectable and receives `RemoteSettings`
- Metro handles the dependency graph automatically

## Prevention of Similar Issues

### Guidelines for Future Development:
1. **Always use Elvis operator** when accessing DataStore preferences: `prefs[key] ?: defaultValue`
2. **Prefer Flow-based APIs** over nullable return types for preferences
3. **Use `derivedStateOf` carefully** - avoid blocking operations inside it
4. **Test null scenarios** - always include tests for unset preferences
5. **Document nullable returns** - if a method can return null, document it clearly

### Code Review Checklist:
- [ ] Does preference access have a default fallback?
- [ ] Are nullable preference values handled with null checks or Elvis operator?
- [ ] Are Flow-based APIs preferred over nullable synchronous access?
- [ ] Are there tests for unset preference scenarios?
- [ ] Is the code safe under low memory conditions?

## Related Issues
- Process death: Settings should persist across process death
- Low memory: The crash occurred after a low memory event
- Compose lifecycle: The crash happened during derived state evaluation

## Testing Recommendations
1. Test with preferences not set (fresh install)
2. Test with process death (Settings → Developer options → Don't keep activities)
3. Test with low memory conditions
4. Test preference persistence across app restarts
