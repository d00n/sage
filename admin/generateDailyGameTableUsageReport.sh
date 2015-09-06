#!/bin/bash

ADMIN_DIR=/home/ec2-user/sage/admin

hostname
echo

/usr/bin/mysql sage -u sage_rw -psk00bysnack99 --host=sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com < $ADMIN_DIR/gameTableStats_hours_played.sql 
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 --host=sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com < $ADMIN_DIR/gameTableStats_hours_played_total.sql 
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 --host=sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com < $ADMIN_DIR/gameTableStats_hours_played_last_30_days.sql 
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 --host=sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com < $ADMIN_DIR/gameTableStats_hours_per_day_last_30_days.sql 
echo
echo 'Games in progress'
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 --host=sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com < $ADMIN_DIR/gameTableStats_games_in_progress.sql
echo
echo 'Zombie connections'
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 --host=sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com < $ADMIN_DIR/gameTableStats_zombie_connections.sql
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 --host=sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com < $ADMIN_DIR/gameTableStats_unique_users.sql
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 --host=sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com < $ADMIN_DIR/gameTableStats_hours_by_game.sql
echo
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 --host=sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com < $ADMIN_DIR/gameTableStats_hours_by_user.sql
echo
echo "Top users by hours played"
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 --host=sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com < $ADMIN_DIR/gameTableStats_top_users_by_hours_played.sql
echo
echo "Top games by hours played"
/usr/bin/mysql sage -u sage_rw -psk00bysnack99 --host=sage.c5p5kzhtwqwh.us-west-2.rds.amazonaws.com < $ADMIN_DIR/gameTableStats_top_games_by_hours_played.sql
