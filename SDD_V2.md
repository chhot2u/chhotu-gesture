# Chhotu Gesture v2.0 — Advanced SDD (Spec-Driven Development)

> Generated using CocoIndex-Code MCP: ultra_thinking + plan_optimizer + premortem + inversion_thinking + effort_estimator

---

## 1. Bug Audit — 7 Bugs Found

### BUG-001: No SYSTEM_ALERT_WINDOW permission (CRITICAL)
- **Location**: AndroidManifest.xml
- **Issue**: Permission not declared. No code to request it. Floating overlay will crash.
- **Fix**: Add to manifest + PermissionManager with Settings.canDrawOverlays() check

### BUG-002: Accessibility Service never enabled (CRITICAL)
- **Location**: NavigationActions.kt, SettingsScreen.kt
- **Issue**: No guided enablement flow. No check if service is running. Button only opens Settings.
- **Fix**: PermissionManager checks isAccessibilityServiceEnabled(). Onboarding guides user.

### BUG-003: NavigationActions are non-functional stubs (HIGH)
- **Location**: NavigationActions.kt
- **Issue**: scroll_up, scroll_down, go_back, go_home all return `true` but do NOTHING
- **Fix**: Wire to GestureAccessibilityService.performScroll/performBack/performHome

### BUG-004: No POST_NOTIFICATIONS runtime request for Android 13+ (MEDIUM)
- **Location**: Missing from onboarding flow
- **Issue**: Notification permission required at runtime on API 33+ but never requested
- **Fix**: Add to PermissionManager + onboarding

### BUG-005: No WRITE_SETTINGS check for brightness (MEDIUM)
- **Location**: SystemActions.kt
- **Issue**: Settings.System.putInt for brightness needs Settings.System.canWrite()
- **Fix**: Check and request ACTION_MANAGE_WRITE_SETTINGS

### BUG-006: No overlay permission or overlay service (NEW FEATURE GAP)
- **Location**: Entire codebase
- **Issue**: Zero floating overlay code exists. User requested scroll overlay with speed controls.
- **Fix**: Build ScrollOverlayService with WindowManager TYPE_APPLICATION_OVERLAY

### BUG-007: Scroll is one-shot, not continuous (FEATURE GAP)
- **Location**: GestureAccessibilityService.performScroll()
- **Issue**: Single 300ms swipe gesture, not continuous auto-scroll
- **Fix**: Build ContinuousScrollEngine with coroutine loop + speed levels

---

## 2. Specifications

### SPEC-001: Permission Management System
**Priority**: P0 (Blocker) | **PERT**: 1.08 days

**Behavior**:
- `PermissionManager` utility class with methods:
  - `hasCameraPermission(): Boolean`
  - `hasOverlayPermission(): Boolean` (Settings.canDrawOverlays)
  - `hasNotificationPermission(): Boolean` (API 33+ check)
  - `hasWriteSettingsPermission(): Boolean` (Settings.System.canWrite)
  - `isAccessibilityServiceEnabled(): Boolean`
  - `requestOverlayPermission(activity)` (ACTION_MANAGE_OVERLAY_PERMISSION intent)
  - `requestWriteSettingsPermission(activity)` (ACTION_MANAGE_WRITE_SETTINGS intent)
  - `openAccessibilitySettings(context)` (deep link to A11y settings)
- Onboarding flow updated: Welcome -> Camera -> Overlay -> Accessibility -> Notifications
- Settings screen: permission status indicators (green check / red X)

**Files**: `PermissionManager.kt` (new), `OnboardingScreen.kt` (update), `SettingsScreen.kt` (update), `AndroidManifest.xml` (update)

**Edge Cases**: Permission revoked while app running, "Don't ask again" selected, split-screen mode

---

### SPEC-002: Wire NavigationActions to AccessibilityService
**Priority**: P0 (Blocker) | **PERT**: 0.55 days

**Behavior**:
```
nav.scroll_up  -> GestureAccessibilityService.instance?.performScroll(down=false) ?: false
nav.scroll_down -> GestureAccessibilityService.instance?.performScroll(down=true) ?: false
nav.go_back    -> GestureAccessibilityService.instance?.performBack() ?: false
nav.go_home    -> GestureAccessibilityService.instance?.performHome() ?: false
```
- If AccessibilityService not enabled, show toast: "Enable Accessibility Service in Settings"
- Return false on failure so stats don't count it as successful

**Files**: `NavigationActions.kt` (rewrite)

---

### SPEC-003: Continuous Scroll Engine
**Priority**: P1 (High) | **PERT**: 1.08 days

**Behavior**:
- Three speed levels persisted in DataStore:
  - `SLOW`: 800ms interval, 200px scroll distance
  - `MEDIUM`: 400ms interval, 350px scroll distance  
  - `FAST`: 150ms interval, 500px scroll distance
- Coroutine loop: dispatch scroll gesture -> await callback completion -> delay(interval) -> repeat
- `startContinuousScroll(direction: ScrollDirection, speed: ScrollSpeed)`
- `stopContinuousScroll()`
- `pauseScroll()` / `resumeScroll()` (for touch coexistence)
- Uses `suspendCancellableCoroutine` to await GestureDescription callback

**Files**: `ContinuousScrollEngine.kt` (new), `ScrollSpeed.kt` (new enum), `SettingsDataStore.kt` (update), `GestureAccessibilityService.kt` (update)

**Performance**: Must not cause ANR. Single-threaded dispatcher for sequential gesture dispatch.

---

### SPEC-004: Scroll Overlay Service (Floating Window)
**Priority**: P1 (High) | **PERT**: 2.17 days

**Behavior**:
- `ScrollOverlayService` extends Service
- Shows floating overlay via WindowManager with TYPE_APPLICATION_OVERLAY
- Overlay UI (small pill-shaped, semi-transparent):
  ```
  +------------------------------------------+
  |  [arrow_up] SCROLLING  [S] [M] [F]  [X]  |
  +------------------------------------------+
  ```
  - Direction arrow icon (up/down)
  - "SCROLLING" status text
  - Speed buttons: [S]low [M]edium [F]ast (highlighted = active)
  - [X] Close button to stop scroll
- Overlay is DRAGGABLE (user can reposition)
- WindowManager flags: `FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_LAYOUT_IN_SCREEN`
- Touches PASS THROUGH to app underneath (except on overlay itself)
- Appears when scroll gesture detected, hides when stopped
- Requires SYSTEM_ALERT_WINDOW permission (checked via PermissionManager)

**Files**: `ScrollOverlayService.kt` (new), `scroll_overlay_layout.xml` (new), `AndroidManifest.xml` (update)

**Risk**: Overlay steals focus -> Mitigated with FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL

---

### SPEC-005: Touch Coexistence During Active Scroll
**Priority**: P1 (High) | **PERT**: 1.08 days

**Behavior**:
- When continuous scroll is running AND user touches screen:
  1. Finger DOWN -> `ContinuousScrollEngine.pauseScroll()` immediately
  2. User can tap, click, drag normally (scroll engine is paused)
  3. Finger UP -> start 300ms cooldown timer
  4. After 300ms no touch -> `ContinuousScrollEngine.resumeScroll()`
  5. If finger DOWN again during cooldown -> cancel timer, stay paused
- Detection via AccessibilityService `onAccessibilityEvent(TYPE_VIEW_CLICKED)` or via GestureAccessibilityService's `isUserTouchActive` volatile flag
- Overlay shows visual indicator: "Paused (touching screen)" in yellow

**Files**: `ContinuousScrollEngine.kt` (update), `GestureAccessibilityService.kt` (update), `ScrollOverlayService.kt` (update)

**Edge Case**: Rapid tap-tap-tap should not cause scroll-pause-resume jitter (solved by 300ms cooldown)

---

### SPEC-006: Fix Brightness Permission
**Priority**: P2 (Medium) | **PERT**: 0.28 days

**Behavior**:
- Before `Settings.System.putInt(SCREEN_BRIGHTNESS)`, check `Settings.System.canWrite()`
- If false, show toast and open `ACTION_MANAGE_WRITE_SETTINGS`
- Add permission check to PermissionManager

**Files**: `SystemActions.kt` (update), `PermissionManager.kt` (update)

---

### SPEC-007: Fix Notification Permission (Android 13+)
**Priority**: P2 (Medium) | **PERT**: 0.28 days

**Behavior**:
- On API 33+, request POST_NOTIFICATIONS at runtime
- Add to onboarding flow after camera permission
- If denied, ForegroundService still runs but with no visible notification
- Show warning on Settings screen

**Files**: `PermissionManager.kt` (update), `OnboardingScreen.kt` (update)

---

## 3. Architecture Diagram

```
  [Hand Gesture Detected: Thumbs Up = Scroll Up]
           |
           v
  [NavigationActions.scroll_up]  (SPEC-002: wired to real service)
           |
           v
  [ContinuousScrollEngine]  (SPEC-003: coroutine loop with speed)
     |              |
     |   [Touch Detected?]  (SPEC-005: coexistence)
     |      YES -> PAUSE (finger down)
     |      NO  -> SCROLL (dispatch gesture)
     |              |
     v              v
  [GestureAccessibilityService.performScroll()]
           |
           v
  [ScrollOverlayService]  (SPEC-004: floating UI)
     Shows: speed controls [S][M][F], direction, pause status
     Flags: NOT_FOCUSABLE | NOT_TOUCH_MODAL (clicks pass through)
           |
           v
  [PermissionManager]  (SPEC-001: checks all permissions)
     CAMERA, OVERLAY, ACCESSIBILITY, NOTIFICATIONS, WRITE_SETTINGS
```

---

## 4. Effort Estimation (PERT)

```
SPEC   | Task                                    | Best | Likely | Worst | PERT  | StdDev
-------|------------------------------------------+------+--------+-------+-------+-------
001    | PermissionManager + onboarding           | 0.5  |  1.0   |  2.0  | 1.08  |  0.25
002    | Wire NavigationActions                   | 0.3  |  0.5   |  1.0  | 0.55  |  0.12
003    | Continuous Scroll Engine                  | 0.5  |  1.0   |  2.0  | 1.08  |  0.25
004    | Scroll Overlay Service                   | 1.0  |  2.0   |  4.0  | 2.17  |  0.50
005    | Touch Coexistence                        | 0.5  |  1.0   |  2.0  | 1.08  |  0.25
006+7  | Brightness + notification perms          | 0.3  |  0.5   |  1.0  | 0.55  |  0.12
-------|------------------------------------------+------+--------+-------+-------+-------
TOTAL  |                                          | 3.1  |  6.0   | 12.0  | 6.52  |  0.68

95% Confidence: 5.2 - 7.9 days
```

---

## 5. Risk Analysis (Premortem)

| Risk | L | I | Score | Mitigation |
|------|---|---|-------|------------|
| Overlay steals focus, blocks clicks | 60% | 90% | 0.54 | FLAG_NOT_FOCUSABLE + FLAG_NOT_TOUCH_MODAL + FLAG_LAYOUT_IN_SCREEN |
| Continuous scroll causes ANR | 50% | 80% | 0.40 | suspendCancellableCoroutine awaiting callback + single-threaded dispatcher |

---

## 6. Inversion Analysis

**Inverted Goal**: "How to guarantee the scroll overlay is terrible"

| Failure Cause | Severity | Must-Do (Inverted) |
|---------------|----------|--------------------|
| Overlay full-screen + focusable | 0.95 | Small pill + FLAG_NOT_FOCUSABLE |
| No SYSTEM_ALERT_WINDOW check | 0.90 | PermissionManager.hasOverlayPermission() |
| Scroll gestures pile up (no await) | 0.85 | Await callback before next dispatch |
| No pause on user touch | 0.80 | Pause on DOWN, resume on UP+300ms |

---

## 7. New Files to Create

| File | Package | Description |
|------|---------|-------------|
| `PermissionManager.kt` | `com.chhotu.gesture.util` | Central permission checker |
| `ContinuousScrollEngine.kt` | `com.chhotu.gesture.engine` | Scroll loop with speed control |
| `ScrollSpeed.kt` | `com.chhotu.gesture.domain.model` | SLOW/MEDIUM/FAST enum |
| `ScrollOverlayService.kt` | `com.chhotu.gesture.service` | Floating overlay window |
| `scroll_overlay_layout.xml` | `res/layout` | Overlay UI layout |

## 8. Files to Update

| File | Changes |
|------|---------|
| `AndroidManifest.xml` | Add SYSTEM_ALERT_WINDOW permission + ScrollOverlayService declaration |
| `NavigationActions.kt` | Wire stubs to real AccessibilityService calls |
| `SystemActions.kt` | Add WRITE_SETTINGS permission check |
| `GestureAccessibilityService.kt` | Add continuous scroll support + touch detection |
| `SettingsDataStore.kt` | Add scrollSpeed preference |
| `SettingsScreen.kt` | Add permission status indicators + scroll speed control |
| `OnboardingScreen.kt` | Add overlay + notification permission steps |

---

## 9. CocoIndex-Code MCP Tools Used

| Tool | Purpose |
|------|---------|
| `ultra_thinking` (explore + hypothesize) | Deep bug audit + architecture design |
| `plan_optimizer` (submit + analyze) | Plan quality scoring, 2 anti-patterns found |
| `premortem` (5 phases) | 2 risks identified + mitigated |
| `inversion_thinking` (6 phases) | 4 guaranteed-failure causes found + inverted |
| `effort_estimator` (6 tasks) | PERT: 6.52 days (95% CI: 5.2-7.9 days) |
| `learning_loop` | 6 insights captured |
| `grep_code` (4 searches) | Audited permissions, overlays, scroll code |
| `read_file` (4 files) | Analyzed AccessibilityService, NavigationActions, Settings, Manifest |
| `find_files` | Cataloged all 67 Kotlin files |

**Total CocoIndex tools used: 9 of 25**
