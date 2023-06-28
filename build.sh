#!/bin/bash
DEST="//home/artemis/Documents/Creation/Source-Framework/temp"
TEST_FRAMEWORK="//home/artemis/Documents/Creation/Test-Framework"
CLASSES="$TEST_FRAMEWORK/WEB-INF/classes"
TEST_LIB="$TEST_FRAMEWORK/WEB-INF/lib"
TOMCAT="/home/artemis/apache-tomcat-10.1.2/webapps"
TEMP="/home/artemis/Documents/Creation/Temp"
MODEL="/home/artemis/Documents/Creation/Temp/WEB-INF/classes"
LIB="/home/artemis/Documents/Creation/Temp/WEB-INF/lib"

FILES=$(find . -name "*.java")
for FILE in $FILES
do
    cp $FILE $DEST
done

cd $DEST
javac -d . *.java
rm *.java
jar -cf framework.jar etu2000
cp framework.jar $LIB

cd $TEST_FRAMEWORK
JAVA=$(find . -name "*.java")
for CLASS in $JAVA
do
    cp $JAVA $MODEL
done

cd $MODEL
javac -parameters -cp $LIB/framework.jar -d . *.java

cp $TEST_FRAMEWORK/*.jsp $TEMP
cp $TEST_FRAMEWORK/WEB-INF/web.xml $TEMP/WEB-INF
cd $TEMP
jar cvf Employees.war .
cp Employees.war $TOMCAT
rm Employees.war
/home/artemis/apache-tomcat-10.1.2/bin/shutdown.sh
/home/artemis/apache-tomcat-10.1.2/bin/startup.sh