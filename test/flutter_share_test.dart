import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_share/share.dart';

const _text = "test text";
const _path = "this is a path";

void main() {
  const MethodChannel channel = MethodChannel('LimeVista:Share');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      expect(methodCall.arguments['text'], _text);
      expect(methodCall.arguments['data'], _path);
      expect(methodCall.arguments['type'], "image");
      expect(methodCall.arguments['originWidth'], 1024);
      return null;
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('share', () async {
    await Share.share(
      text: _text,
      extra: SharedImage(_path),
      sharePositionOrigin: Rect.fromLTWH(0, 0, 1024, 1024),
    );
  });
}
