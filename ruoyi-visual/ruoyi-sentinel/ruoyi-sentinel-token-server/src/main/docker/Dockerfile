FROM apache/skywalking-java-agent:8.16.0-java17

MAINTAINER zhoushuai zs11195310@163.com

ADD ruoyi-sentinel-token-server.jar /app/ruoyi-sentinel-token-server.jar

EXPOSE 8769  18730

CMD java $JAVA_TOOL_OPTIONS $JAVA_OPTS -jar /app/ruoyi-sentinel-token-server.jar
