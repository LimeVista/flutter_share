# flutter_share

Plugin for summoning a platform share sheet.

## Getting Started

```yaml
  flutter_share:
    git:
      url: https://github.com/LimeVista/flutter_share.git
      ref: 1.x
```
## Import

```dart
import 'package:flutter_share/share.dart';
```

## Usage

```dart
 Share.share(text: "Flutter");                  // share text
 Share.share(extra: SharedImage("file path"));  // share image
```

## Docs
> See [share.dart](https://github.com/LimeVista/flutter_share/blob/master/lib/share.dart)

```dart
  /// share
  /// The optional [text]  parameter is shared text
  /// The optional [extra] parameter is shared extra,
  /// see [SharedImage], [SharedVideo], [SharedFile], [SharedEmail], [SharedAudio]
  /// The optional [sharePositionOrigin] parameter can be used to specify a global
  /// origin rect for the share sheet to popover from on iPads. It has no effect
  /// on non-iPads.
  static Future<void> share({
    String text,
    SharedData extra,
    ui.Rect sharePositionOrigin,
  });

  /// share image, eq: share(extra: SharedImage(path),...); see [share]
  /// The optional [path] parameter is file path
  /// The optional [sharePositionOrigin] parameter can be used to specify a global
  /// origin rect for the share sheet to popover from on iPads. It has no effect
  /// on non-iPads.
  static Future<void> shareImage(String path, {ui.Rect sharePositionOrigin});

  /// share video, eq: share(extra: SharedVideo(path),...); see [share]
  /// The optional [path] parameter is file path
  /// The optional [sharePositionOrigin] parameter can be used to specify a global
  /// origin rect for the share sheet to popover from on iPads. It has no effect
  /// on non-iPads.
  static Future<void> shareVideo(String path, {ui.Rect sharePositionOrigin});

  /// share audio, eq: share(extra: SharedVideo(path),...); see [share]
  /// The optional [path] parameter is file path
  /// The optional [sharePositionOrigin] parameter can be used to specify a global
  /// origin rect for the share sheet to popover from on iPads. It has no effect
  /// on non-iPads.
  static Future<void> shareAudio(String path, {ui.Rect sharePositionOrigin});

  /// share text, eq: share(text: text,...); see [share]
  /// The optional [text] parameter is shared text
  /// The optional [sharePositionOrigin] parameter can be used to specify a global
  /// origin rect for the share sheet to popover from on iPads. It has no effect
  /// on non-iPads.
  static Future<void> shareText(String text, {ui.Rect sharePositionOrigin});
```


