select count(distinct(user_name)) as 'Unique user count'
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 2 day) and
       application_name = 'whiteboard'
;

