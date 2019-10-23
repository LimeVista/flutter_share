#import "FlutterSharePlugin.h"

#define TYPE_TEXT @"text"
#define TYPE_FILE @"file"
#define TYPE_IMAGE @"image"
#define TYPE_VIDEO @"video"
#define TYPE_AUDIO @"audio"
#define TYPE_EMIAL @"email"


@implementation FlutterSharePlugin

+ (void)registerWithRegistrar:(NSObject <FlutterPluginRegistrar> *)registrar {
    FlutterMethodChannel *channel = [FlutterMethodChannel
            methodChannelWithName:@"LimeVista:Share" binaryMessenger:[registrar messenger]];

    [channel setMethodCallHandler:^(FlutterMethodCall *call, FlutterResult result) {
        if ([@"share" isEqualToString:call.method]) {
            [self share:[call arguments]];
        } else {
            result(FlutterMethodNotImplemented);
        }
    }];
}

+ (void)share:(NSDictionary *)arguments {
    NSString *text = arguments[@"text"];
    NSString *type = arguments[@"type"];
    NSString *data = arguments[@"data"];

    NSNumber *originX = arguments[@"originX"];
    NSNumber *originY = arguments[@"originY"];
    NSNumber *originWidth = arguments[@"originWidth"];
    NSNumber *originHeight = arguments[@"originHeight"];

    CGRect origin = CGRectZero;
    if (originX != nil && originY != nil && originWidth != nil && originHeight != nil) {
        origin = CGRectMake([originX doubleValue], [originY doubleValue],
                [originWidth doubleValue], [originHeight doubleValue]);
    }

    if ([type isEqualToString:TYPE_TEXT]) {
        [self startShare:@[text] atSourceRect:origin];
    } else if ([type isEqualToString:TYPE_FILE]) {
        NSURL *url = [NSURL fileURLWithPath:data];
        NSArray *items = text == nil ? @[url] : @[url, text];
        [self startShare:items atSourceRect:origin];
    } else if ([type isEqualToString:TYPE_IMAGE]) {
        UIImage *image = [UIImage imageWithContentsOfFile:data];
        NSArray *items = text == nil ? @[image] : @[image, text];
        [self startShare:items atSourceRect:origin];
    } else if ([type isEqualToString:TYPE_VIDEO]) {
        NSURL *url = [NSURL fileURLWithPath:data];
        NSArray *items = text == nil ? @[url] : @[url, text];
        [self startShare:items atSourceRect:origin];
    } else if ([type isEqualToString:TYPE_AUDIO]) {
        NSURL *url = [NSURL fileURLWithPath:data];
        NSArray *items = text == nil ? @[url] : @[url, text];
        [self startShare:items atSourceRect:origin];
    } else if ([type isEqualToString:TYPE_EMIAL]) {
        NSArray *items = data == nil ? @[text] : @[text, data];
        [self startShare:items atSourceRect:origin];
    }
}

+ (void)startShare:(NSArray *)items atSourceRect:(CGRect)rect {
    UIActivityViewController *controller = [[UIActivityViewController alloc] initWithActivityItems:items applicationActivities:nil];
    UIViewController *viewController = [UIApplication sharedApplication].keyWindow.rootViewController;
    controller.popoverPresentationController.sourceView = viewController.view;
    if (!CGRectIsEmpty(rect)) controller.popoverPresentationController.sourceRect = rect;
    [viewController presentViewController:controller animated:YES completion:nil];
}

@end
