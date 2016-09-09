FROM ubuntu:xenial

MAINTAINER Lukas Breuer <lukas.breuer@outlook.com>

RUN apt-get update && apt-get install -y apt-utils
RUN apt-get install -y openjdk-8-jre python ffmpeg libav-tools libavcodec-extra wget ca-certificates
RUN apt-get clean -y

RUN wget https://yt-dl.org/downloads/latest/youtube-dl -O /usr/local/bin/youtube-dl
RUN chmod a+x /usr/local/bin/youtube-dl
RUN youtube-dl -U

RUN mkdir -p /data

COPY build/libs/Shiro.jar /

COPY docker-entrypoint.sh /
RUN chmod a+x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
