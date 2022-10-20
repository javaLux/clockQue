# Example of custom Java runtime using jlink in a multi-stage container build
# FROM eclipse-temurin:18 as jre-build

# Create a custom Java runtime with jlink
RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base,java.compiler,java.datatransfer,java.xml,java.prefs,java.desktop,java.logging,java.management,java.security.sasl,java.naming,java.rmi,java.scripting,java.transaction.xa,java.sql,jdk.localedata,jdk.charsets,jdk.unsupported,jdk.crypto.ec \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

# Define your base image (ubuntu linux)
FROM ubuntu
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME

# with this bash command we can create multiple dir's with one command
RUN bash -c 'mkdir -p /opt/app/{lib,resources}'

COPY /target/lib /opt/app/lib/
COPY /target/resources /opt/app/resources/
COPY /target/ClockQue-1.0.1.jar /opt/app/

# switch work directory
WORKDIR /opt/app

CMD ["java", "-jar" , "-Dfile.encoding=UTF-8", "-Djdk.gtk.version=3", "--module-path", "./lib", "--add-modules", "javafx.controls,javafx.fxml,javafx.graphics", "ClockQue-1.0.1.jar"]