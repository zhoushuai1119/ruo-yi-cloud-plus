FROM apache/skywalking-java-agent:8.16.0-java17

MAINTAINER zhoushuai zs11195310@163.com

ADD ruoyi-gateway.jar /app/ruoyi-gateway.jar

EXPOSE 8080

CMD java $JAVA_TOOL_OPTIONS $JAVA_OPTS -jar /app/ruoyi-gateway.jar
