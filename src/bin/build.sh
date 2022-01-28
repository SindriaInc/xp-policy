#!/usr/bin/env bash

CONTAINER_NAME=cyr-policy

docker exec -t ${CONTAINER_NAME} su sindria -c "mvn compile; mvn package"