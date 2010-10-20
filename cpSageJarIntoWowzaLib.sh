#!/bin/bash

WOWZA_LIB_DIR=/usr/local/WowzaMediaServer/lib

cp -v ./wowza/jar/sage.jar $WOWZA_LIB_DIR
chown wowza:wowza $WOWZA_LIB_DIR/sage.jar
