FROM apache/skywalking-java-agent:8.16.0-java17

MAINTAINER zhoushuai zs11195310@163.com

ADD ruoyi-auth.jar /app/ruoyi-auth.jar

EXPOSE 9210

CMD java $JAVA_TOOL_OPTIONS $JAVA_OPTS -jar /app/ruoyi-auth.jar
