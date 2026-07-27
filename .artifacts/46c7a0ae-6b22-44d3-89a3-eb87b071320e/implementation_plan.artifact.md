# Implementation Plan - Fix Foreground Service Notification Exception

The application is crashing with `CannotPostForegroundServiceNotificationException: Bad notification for startForeground`. This is primarily due to missing Android 14+ foreground service requirements and the absence of a Notification Channel.

## User Review Required

> [!IMPORTANT]
> The app targets API 35 (Android 15). Android 14+ requires foreground services to specify their type in `startForeground()` if a type is declared in the manifest.

## Proposed Changes

### Audio Service

#### [MODIFY] [AudioService.kt](file:///Users/2100860/AndroidStudioProjects/RFIplayer/app/src/main/java/com/craiovadata/rfiplayer/AudioService.kt)
- Implement `createNotificationChannel()` to ensure the notification channel exists before starting the foreground service.
- Update `onStartCommand()` to call `createNotificationChannel()`.
- Update `startForeground()` to use the overload that accepts `foregroundServiceType`, specifying `ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK`.
- Improve `createNotification()` to include a title and content text.

## Verification Plan

### Manual Verification
- Deploy the app to an Android 14+ device/emulator.
- Start playback and verify that the foreground service starts without crashing.
- Verify that the notification appears in the notification drawer with the "Stop" action.
