# FROM ubuntu:latest
# LABEL Leigh West "leighwest@hotmail.com"
# RUN apt-get update && apt-get install -y openjdk-11-jdk

FROM mysql
ENV MYSQL_ALLOW_EMPTY_PASSWORD=yes
ENV MYSQL_DATABASE=orders
EXPOSE 3306

