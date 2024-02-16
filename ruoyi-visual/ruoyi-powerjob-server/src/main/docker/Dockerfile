FROM apache/skywalking-java-agent:8.16.0-java17

MAINTAINER zhoushuai zs11195310@163.com

ADD ruoyi-powerjob-server.jar /app/ruoyi-powerjob-server.jar

EXPOSE 7700 10086 10010

CMD java $JAVA_TOOL_OPTIONS $JAVA_OPTS -jar /app/ruoyi-powerjob-server.jar
