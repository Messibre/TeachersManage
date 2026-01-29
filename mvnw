#!/usr/bin/env sh

# Teachera - Maven Wrapper (Unix)
# This wrapper downloads the Maven Wrapper JAR and a Maven distribution if needed.
# Requires Java to be installed and available on PATH.

BASEDIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
WRAPPER_DIR="$BASEDIR/.mvn/wrapper"
WRAPPER_JAR="$WRAPPER_DIR/maven-wrapper.jar"
WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

mkdir -p "$WRAPPER_DIR"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Downloading Maven Wrapper JAR..."
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$WRAPPER_URL" -o "$WRAPPER_JAR"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$WRAPPER_URL" -O "$WRAPPER_JAR"
  else
    echo "ERROR: curl or wget is required to download $WRAPPER_URL" >&2
    exit 1
  fi
fi

exec java -classpath "$WRAPPER_JAR" org.apache.maven.wrapper.MavenWrapperMain "$@"

