#!/bin/bash

source /etc/profile

# MYIMAGE=192.168.4.100:8082/docker-demo-service

# uncomment if you need push
# docker login 192.168.4.100:8082 -u admin -p admin123

# remove old images
#docker images | grep 192.168.4.100:8082/docker/docker-demo-service | awk '{print $3}' | xargs docker rmi
#docker rmi $(docker images | grep "^<none>" | awk "{print $3}")

# build jar and image mvn package -e -X docker:build -DskipTest
mvn clean package -e -X -DskipTests

# push image
# docker push ${MYIMAGE}

# /usr/sbin/sshd

# echo "Service sshd started!"

# java -jar /usr/service/service.jar
