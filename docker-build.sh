#!/bin/bash

IMAGE_NAME="jar-obfuscator:latest"

echo "building docker image..."
sudo docker build -t $IMAGE_NAME .

if [[ $? -ne 0 ]]; then
    echo "docker image build failed"
    exit 1
fi

CONTAINER_ID=$(sudo docker run -d $IMAGE_NAME)

if [[ $? -ne 0 ]]; then
    echo "docker container failed to start"
    exit 1
fi

sudo docker wait "$CONTAINER_ID"

sudo docker cp "$CONTAINER_ID":/jar-obfuscator.jar ./jar-obfuscator.jar

sudo docker rm "$CONTAINER_ID"

echo "file jar-obfuscator.jar has been copied to the current directory"
