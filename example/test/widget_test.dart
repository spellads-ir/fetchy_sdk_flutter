import 'package:fetchy_sdk_flutter_example/main.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('Example app renders', (WidgetTester tester) async {
    await tester.pumpWidget(const FetchyExampleApp());

    expect(find.text('نمونه Fetchy SDK'), findsOneWidget);
    expect(find.text('وضعیت SDK'), findsOneWidget);
    expect(find.text('دسترسی نوتیفیکیشن'), findsOneWidget);
  });
}
