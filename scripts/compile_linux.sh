#!/bin/bash

CATALINA_HOME=/home/juggernaut/apache-tomcat-7.0.52
APP_HOME=$CATALINA_HOME/webapps/Jaggy/WEB-INF

# Coge ficheros jar
find $APP_HOME/lib/ -name "*.jar" > /tmp/jarfiles.txt
echo $CATALINA_HOME/lib/servlet-api.jar >> /tmp/jarfiles.txt
echo $CATALINA_HOME/webapps/Jaggy/WEB-INF/java >> /tmp/jarfiles.txt
echo $CATALINA_HOME/webapps/Jaggy/WEB-INF/classes >> /tmp/jarfiles.txt
echo '.' >> /tmp/jarfiles.txt
sed -i ':a;N;$!ba;s/\n/:/g' /tmp/jarfiles.txt


# Coge ficheros java
find $APP_HOME/java/ -name "*.java" > /tmp/javafiles.txt

JAR_PATH=@/tmp/jarfiles.txt
JAVA_PATH=@/tmp/javafiles.txt

javac -cp $JAR_PATH -d $APP_HOME/classes $JAVA_PATH 
