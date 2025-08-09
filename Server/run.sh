#!/bin/sh
set -e

cd ../Plugin
    ./gradlew build
cd ../Server

mkdir -p ./plugins
cd ./plugins
    ln -fs ../../Plugin/build/libs/MCDrive-SNAPSHOT.jar
cd ..

java -Xms4096M -Xmx4096M -jar server.jar nogui
