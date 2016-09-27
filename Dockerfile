ARG VCS_REF

LABEL org.label-schema.build-date=$BUILD_DATE \
      org.label-schema.name="Shiro Container" \
      org.label-schema.description="Contains the discord bot 'shiro' and all needed dependencies" \
      org.label-schema.url="http://meetshiro.xyz" \
      org.label-schema.vcs-ref=$VCS_REF \
      org.label-schema.vcs-url="https://github.com/sn0w/shiro" \
      org.label-schema.vendor="sn0w" \
      org.label-schema.schema-version="1.0"

FROM sn0w/shiro-base

MAINTAINER Lukas Breuer <lukas.breuer@outlook.com>

RUN youtube-dl -U

COPY build/libs/shiro.jar /

COPY docker-entrypoint.sh /
RUN chmod a+x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
