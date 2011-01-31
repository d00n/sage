select room_name,
       timestampdiff(second, connected_at, disconnected_at) as seconds,
       timestampdiff(minute, connected_at, disconnected_at)/60 as hours
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 2 day) and
       application_name = 'chat' and
       room_name != "Sample Game" and 
       user_id != 'muldoon'
;


select sum(timestampdiff(minute, connected_at, disconnected_at)/60) as hours
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 60 day) and
       application_name = 'chat' and
       room_name != "Sample Game" and
       user_id != 'muldoon'
;



select count(distinct(user_name)) as 'Unique Users'
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 60 day) and
       application_name = 'chat' and
       room_name != "Sample Game" and
       user_id != 'muldoon'
;







