#!/bin/bash


/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < gameTableStats_hours_played.sql 
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < gameTableStats_unique_users.sql
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < gameTableStats_hours_by_game.sql
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < gameTableStats_hours_by_user.sql
echo
echo "Power users"
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 < gameTableStats_power_users.sql

