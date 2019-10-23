import 'dart:async';
import 'dart:ui' as ui;

import 'package:flutter/widgets.dart';
import 'package:flutter/services.dart';

const _allType = ['text', 'image', 'video', 'audio', 'file', 'email'];

@immutable
class SharedImage extends _SharedFile {
  /// Share image. [path] is file path, must be not null.
  SharedImage(String path) : super(path);

  @override
  String get _type => _allType[1];
}

@immutable
class SharedVideo extends _SharedFile {
  /// Share video. [path] is file path, must be not null.
  SharedVideo(String path) : super(path);

  @override
  String get _type => _allType[2];
}

@immutable
class SharedAudio extends _SharedFile {
  /// Share audio. [path] is file path, must be not null.
  SharedAudio(String path) : super(path);

  @override
  String get _type => _allType[3];
}

@immutable
class SharedFile extends _SharedFile {
  /// Share file. [path] is file path, must be not null.
  SharedFile(String path) : super(path);

  @override
  String get _type => _allType[4];
}

@immutable
class SharedEmail extends SharedData {
  /// Share email.
  SharedEmail(this.subject);

  final String subject;

  @override
  String get _data => subject;

  @override
  String get _type => _allType[5];
}

/// Plugin for summoning a platform share sheet.
/// Support iOS, Android.
class Share {
  static const MethodChannel _channel = const MethodChannel('LimeVista:Share');

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
  }) {
    assert(text != null && text.isNotEmpty);
    assert(extra != null || text != null);

    final params = <String, dynamic>{
      'text': text,
      'type': extra?._type ?? _allType[0],
      'data': extra?._data,
    };

    if (sharePositionOrigin != null) {
      params['originX'] = sharePositionOrigin.left;
      params['originY'] = sharePositionOrigin.top;
      params['originWidth'] = sharePositionOrigin.width;
      params['originHeight'] = sharePositionOrigin.height;
    }

    return _channel.invokeMethod('share', params);
  }

  /// share image, eq: share(extra: SharedImage(path),...); see [share]
  /// The optional [path] parameter is file path
  /// The optional [sharePositionOrigin] parameter can be used to specify a global
  /// origin rect for the share sheet to popover from on iPads. It has no effect
  /// on non-iPads.
  static Future<void> shareImage(String path, {ui.Rect sharePositionOrigin}) {
    return share(
      extra: SharedImage(path),
      sharePositionOrigin: sharePositionOrigin,
    );
  }

  /// share video, eq: share(extra: SharedVideo(path),...); see [share]
  /// The optional [path] parameter is file path
  /// The optional [sharePositionOrigin] parameter can be used to specify a global
  /// origin rect for the share sheet to popover from on iPads. It has no effect
  /// on non-iPads.
  static Future<void> shareVideo(String path, {ui.Rect sharePositionOrigin}) {
    return share(
      extra: SharedVideo(path),
      sharePositionOrigin: sharePositionOrigin,
    );
  }

  /// share audio, eq: share(extra: SharedVideo(path),...); see [share]
  /// The optional [path] parameter is file path
  /// The optional [sharePositionOrigin] parameter can be used to specify a global
  /// origin rect for the share sheet to popover from on iPads. It has no effect
  /// on non-iPads.
  static Future<void> shareAudio(String path, {ui.Rect sharePositionOrigin}) {
    return share(
      extra: SharedAudio(path),
      sharePositionOrigin: sharePositionOrigin,
    );
  }

  /// share text, eq: share(text: text,...); see [share]
  /// The optional [text] parameter is shared text
  /// The optional [sharePositionOrigin] parameter can be used to specify a global
  /// origin rect for the share sheet to popover from on iPads. It has no effect
  /// on non-iPads.
  static Future<void> shareText(String text, {ui.Rect sharePositionOrigin}) {
    assert(text != null && text.isNotEmpty);
    return share(text: text, sharePositionOrigin: sharePositionOrigin);
  }

  /// send email, eq: share(text: text,...); see [share]
  /// The optional [text] parameter is email title
  /// The optional [subject] parameter is email subject.
  /// The optional [sharePositionOrigin] parameter can be used to specify a global
  /// origin rect for the share sheet to popover from on iPads. It has no effect
  /// on non-iPads.
  static Future<void> sendEmail(
    String text,
    String subject, {
    ui.Rect sharePositionOrigin,
  }) {
    assert(text != null && text.isNotEmpty);
    return share(
      text: text,
      extra: SharedEmail(subject),
      sharePositionOrigin: sharePositionOrigin,
    );
  }
}

/// share data.
/// impl: [SharedImage], [SharedVideo], [SharedFile], [SharedEmail], [SharedAudio]
@immutable
abstract class SharedData {
  const SharedData();

  String get _data;

  String get _type;
}

abstract class _SharedFile extends SharedData {
  _SharedFile(this.path) : assert(path != null && path.isNotEmpty);

  final String path;

  @override
  String get _data => path;
}
