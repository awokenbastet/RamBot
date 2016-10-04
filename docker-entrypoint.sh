#!/usr/bin/env sh

echo ""
echo "--------- Welcome to the Shiro container! \o/ ---------"

cd /data

java -server -XX:+UseConcMarkSweepGC -jar /shiro.jar
