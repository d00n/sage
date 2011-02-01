#!/bin/bash

ADMIN_DIR=/home/muldoon/repos/sage/admin


/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < $ADMIN_DIR/gameTableStats_hours_played.sql 
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < $ADMIN_DIR/gameTableStats_unique_users.sql
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < $ADMIN_DIR/gameTableStats_hours_by_game.sql
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < $ADMIN_DIR/gameTableStats_hours_by_user.sql
echo
echo "Power users"
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < $ADMIN_DIR/gameTableStats_power_users.sql

