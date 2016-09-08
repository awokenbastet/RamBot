#!/usr/bin/env sh

set -x
set -f

echo "--------- Welcome to the Shiro container! \o/ ---------"

cd /data

exec "$@" bin/shiro.jar
