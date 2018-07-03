FROM jingzing/jdk8

MAINTAINER jinggo<jinggo@sohu.com>

VOLUME /tmp

ADD target/jinggeBlog-1.0.0-SNAPSHOT.jar /data/service.jar

RUN sh -c 'touch /data/service.jar'

ENV JAVA_OPTS=""
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /data/service.jar --spring.profiles.active=dev"]