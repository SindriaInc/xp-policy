#!/usr/bin/env bash

CONTAINER_NAME=xp-policy

docker exec -t ${CONTAINER_NAME} su sindria -c "mvn compile; mvn package"