#!/bin/bash
DEST="//home/artemis/Documents/Creation/Source-Framework/temp"
TEST_FRAMEWORK="//home/artemis/Documents/Creation/Test-Framework"
CLASSES="$TEST_FRAMEWORK/WEB-INF/classes"
TEST_LIB="$TEST_FRAMEWORK/WEB-INF/lib"
TOMCAT="/home/artemis/apache-tomcat-10.1.2/webapps"

cd ./Source-Framework/src/java
FILES=$(find . -name "*.java")
for FILE in $FILES
do
    cp $FILE $DEST
done
cd $DEST
javac -d . *.java
rm *.java
jar -cf framework.jar etu2000
cp framework.jar $TEST_LIB
cd $CLASSES
javac -cp $TEST_LIB/framework.jar -d . *.java
cd $TEST_FRAMEWORK
jar cvf Employees.war .
cp Employees.war $TOMCAT
rm Employees.war
/home/artemis/apache-tomcat-10.1.2/bin/shutdown.sh
/home/artemis/apache-tomcat-10.1.2/bin/startup.sh