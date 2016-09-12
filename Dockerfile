FROM sn0w/shiro-base

MAINTAINER Lukas Breuer <lukas.breuer@outlook.com>

RUN youtube-dl -U

COPY build/libs/shiro.jar /

COPY docker-entrypoint.sh /
RUN chmod a+x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
