#!/bin/sh
set -e

sudo modprobe nbd

make -j 4 $@

cd dist
    mkfifo buse_requests_write
    mkfifo buse_requests_read
    sudo ./busefifo /dev/nbd0
cd ..
