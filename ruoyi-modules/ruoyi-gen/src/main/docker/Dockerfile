FROM apache/skywalking-java-agent:8.16.0-java17

MAINTAINER zhoushuai zs11195310@163.com

ADD ruoyi-gen.jar /app/ruoyi-gen.jar

EXPOSE 9202

CMD java $JAVA_TOOL_OPTIONS $JAVA_OPTS -jar /app/ruoyi-gen.jar
