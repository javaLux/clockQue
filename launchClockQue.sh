#!/bin/sh

chmod +x ClockQue-*.jar

./lib/customJDK-18-ubuntu/bin/java -jar -Dfile.encoding=UTF-8 -Djdk.gtk.version=3 --module-path ./lib --add-modules javafx.controls,javafx.fxml,javafx.graphics ClockQue-*.jar


