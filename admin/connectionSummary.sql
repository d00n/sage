select round(timestampdiff(second, connected_at, disconnected_at)/60/60, 1) as Hours,
       room_name
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 60 day) and
       application_name = 'chat' and
       room_name != "Sample Game" and 
       user_id != 'muldoon'
;


select sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60, 2)) as Hours
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 61 day) and
       application_name = 'chat' and
       room_name != "Sample Game" and
       user_id != 'muldoon'
;



select count(distinct(user_name)) as 'Unique users'
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 61 day) and
       application_name = 'chat' and
       room_name != "Sample Game" and
       user_id != 'muldoon'
;







