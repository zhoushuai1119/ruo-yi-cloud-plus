FROM apache/skywalking-java-agent:8.16.0-java17

MAINTAINER zhoushuai zs11195310@163.com

ADD ruoyi-easyretry-server.jar /app/ruoyi-easyretry-server.jar

EXPOSE 8800

CMD java $JAVA_TOOL_OPTIONS $JAVA_OPTS -jar /app/ruoyi-easyretry-server.jar
