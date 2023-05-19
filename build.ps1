$env:JAVA_HOME=$env:JAVA_17_HOME
echo "----------------------------------------"
echo "JAVA_17_HOME=$env:JAVA_17_HOME"
echo "----------------------------------------"
mvn clean compile assembly:single