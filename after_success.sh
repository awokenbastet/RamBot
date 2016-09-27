#!/usr/bin/env bash

set -ev

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
    docker login -u="$DU" -p="$DP"
    docker build -t "sn0w/shiro:$TRAVIS_COMMIT" --build-arg VCS_REF="$TRAVIS_COMMIT" .
    docker push "sn0w/shiro:$TRAVIS_COMMIT"

    docker tag "sn0w/shiro:$TRAVIS_COMMIT" "sn0w/shiro:$TRAVIS_BRANCH"
    docker push "sn0w/shiro:$TRAVIS_COMMIT"
fi
