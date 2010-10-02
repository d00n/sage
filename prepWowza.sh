#!/bin/bash

WOWZA_BASE_DIR=/usr/local/WowzaMediaServer


cp -r ./wowza_config/* $WOWZA_BASE_DIR
sudo chown -R wowza:wowza $WOWZA_BASE_DIR
