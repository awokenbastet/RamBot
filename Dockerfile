FROM alpine:edge

RUN apk update
RUN apk add openjdk7-jre
RUN apk add python
RUN apk add ffmpeg ffmpeg-libs

RUN apk add wget ca-certificates

RUN wget https://yt-dl.org/downloads/latest/youtube-dl -O /usr/local/bin/youtube-dl
RUN chmod a+x /usr/local/bin/youtube-dl
RUN youtube-dl -U

RUN mkdir -p /data

COPY build/libs/Shiro.jar /

COPY docker-entrypoint.sh /
RUN chmod a+x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
