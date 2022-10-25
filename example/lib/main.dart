import 'dart:async';
import 'dart:io';
import 'dart:ui' as ui;

import 'package:flutter/material.dart';
import 'package:flutter_share/share.dart';
import 'package:path_provider/path_provider.dart';

void main() => runApp(ExampleApp());

class ExampleApp extends StatefulWidget {
  @override
  _ExampleAppState createState() => _ExampleAppState();
}

class _ExampleAppState extends State<ExampleApp> {
  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Share Plugin')),
        body: Center(
          child: Wrap(
            children: <Widget>[
              buildButton("ShareText"),
              buildButton("ShareFile"),
            ],
          ),
        ),
      ),
    );
  }

  Widget buildButton(String key) => ElevatedButton(
        onPressed: () => _onPressed(key),
        child: Text(key),
      );

  void _onPressed(String tag) async {
    switch (tag) {
      case 'ShareText':
        Share.share(text: "Share Example");
        break;
      case 'ShareFile':
        final tmp = await getTemporaryDirectory();
        final file = File(tmp.path + "/tmp.png");
        final recorder = ui.PictureRecorder();
        Canvas cvs = Canvas(recorder);
        cvs.drawColor(Colors.green, BlendMode.color);
        final p = recorder.endRecording();
        final data = await (await p.toImage(512, 512))
            .toByteData(format: ui.ImageByteFormat.png);
        await file.create();
        await file.writeAsBytes(data!.buffer.asUint8List());
        Share.share(
          text: "ShareFile",
          extra: SharedFile(file.path),
        );
        break;
    }
  }
}
