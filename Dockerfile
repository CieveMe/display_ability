#基础镜像
FROM harbor.yingliduo.cn/public/java:8
#设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo 'Asia/Shanghai' >/etc/timezone
RUN export  LANG="C.UTF-8"
ENV LANG C.UTF-8
RUN apt-get update && apt-get install -y curl telnet procps
VOLUME /logs
VOLUME /logs/yld-supply-v2
#设置JAVA_OPTS参数
ENV JAVA_OPTS=" -server\
               -Xmx2g\
               -Xms1g\
               -Xmn1g\
               -XX:+DisableExplicitGC\
               -XX:+UseConcMarkSweepGC\
               -XX:+CMSParallelRemarkEnabled\
               -XX:+UseCMSCompactAtFullCollection\
               -XX:LargePageSizeInBytes=128m\
               -XX:+UseFastAccessorMethods\
               -XX:+UseCMSInitiatingOccupancyOnly\
               -XX:CMSInitiatingOccupancyFraction=80\
               -Xloggc:/logs/gc.log\
               -XX:+PrintGCDetails\
               -XX:+PrintGCDateStamps\
               -XX:+PrintTenuringDistribution\
               -XX:+HeapDumpOnOutOfMemoryError\
               -XX:HeapDumpPath=/logs/java_heapdump.hprof "
#复制jar包到容器
ARG JAR_FILE=target/yld-supply-v2.jar
ARG SKYWALK=agent
ADD target/yld-supply-v2.jar /var/www/yld-supply-v2/yld-supply-v2.jar
ADD agent /var/www/yld-supply-v2/agent
#暴露容器端口
EXPOSE 20880
#设置入口点
ENTRYPOINT exec java -javaagent:/var/www/yld-supply-v2/agent/skywalking-agent.jar  $JAVA_OPTS  -Djava.security.egd=file:/dev/./urandom -Duser.timezone=GMT+08 -jar  /var/www/yld-supply-v2/yld-supply-v2.jar