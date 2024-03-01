FROM apache/skywalking-java-agent:8.16.0-java17

MAINTAINER zhoushuai zs11195310@163.com

ADD ruoyi-rocketmq-monitor.jar /app/ruoyi-rocketmq-monitor.jar

EXPOSE 9101

CMD java $JAVA_TOOL_OPTIONS $JAVA_OPTS -jar /app/ruoyi-rocketmq-monitor.jar
