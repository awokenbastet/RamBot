#!/usr/bin/env sh

echo ""
echo "--------- Welcome to the Shiro container! \o/ ---------"

cd /data

java \
    -server \
    -XX:+UseCompressedOops \
    -XX:+UseConcMarkSweepGC \
    -Dsun.io.useCanonCaches=false \
    -Djava.net.preferIPv4Stack=true \
    -jar /shiro.jar
