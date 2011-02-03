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
echo "Top users by hours played"
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < gameTableStats_top_users_by_hours_played.sql
echo
echo "Top games by hours played"
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < gameTableStats_top_games_by_hours_played.sql

