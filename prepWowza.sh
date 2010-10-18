#!/bin/bash

WOWZA_BASE_DIR=/usr/local/WowzaMediaServer


cp -r ./wowza_config/* $WOWZA_BASE_DIR
chown -R wowza:wowza $WOWZA_BASE_DIR
