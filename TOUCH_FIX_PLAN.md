# 🐛 Bug Fix Plan: Touch Blocked During Gesture Detection

## Problem
User cannot click, scroll, or interact with the screen while gesture detection is running.

## Root Causes Found (4)

| # | Root Cause | Severity | File |
|---|-----------|----------|------|
| 1 | **GestureOverlay Canvas** covers full screen, intercepts ALL touch events | 🔴 CRITICAL | `GestureOverlay.kt` |
| 2 | **CameraPreview** SurfaceView steals touch focus | 🟡 HIGH | `CameraPreview.kt` |
| 3 | **AccessibilityService** `typeAllMask` too aggressive + synthetic gestures conflict with real touch | 🟡 MEDIUM | `accessibility_service_config.xml` |
| 4 | **DetectScreen layout** z-ordering puts overlay between camera and interactive UI | 🟡 MEDIUM | `DetectScreen.kt` |

## Fixes (5)

### Fix 1: Make GestureOverlay touch-transparent
- Add `Modifier.graphicsLayer()` — makes Canvas render-only, no touch interception
- Canvas draws landmarks visually but passes ALL touches through

### Fix 2: Fix CameraPreview touch passthrough
- Set `PreviewView.implementationMode = COMPATIBLE` (TextureView)
- Disable clickable/focusable on the PreviewView
- This stops the camera view from stealing touch events

### Fix 3: Reduce AccessibilityService aggression
- Change `accessibilityEventTypes` from `typeAllMask` to only `typeWindowStateChanged`
- Add `android:flagDefault="flagDefault"` to avoid intercepting all events
- Add touch-active guard in `GestureAccessibilityService` — skip synthetic gestures when user is touching

### Fix 4: Fix DetectScreen z-ordering
- Restructure Box layers so overlay is touch-transparent
- Interactive elements (Card, FAB) always receive touches

### Fix 5: Add touch guard in DetectViewModel
- Track `isUserTouching` state
- When user is touching → still detect gestures visually but suppress action execution
- Resume actions 500ms after last touch-up
- Only guard nav/scroll actions, not media/volume

## Effort Estimate (PERT)
- **PERT**: 1.08 days | **95% CI**: 0.6 – 1.6 days

## Risk
- Touch guard too aggressive → Mitigate by only suppressing conflicting actions during touch
