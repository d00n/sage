select sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60, 2)) as 'Hours played',
       date_sub(now(), interval 1 day) as 'starting',
       date_sub(now(), interval 2 day) as 'ending'
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 2 day) and
       disconnected_at !=0 and
       application_name = 'chat' and
       room_name != "Sample Game" and
       user_id != 'muldoon'
;


select count(distinct(user_name)) as 'Unique user count'
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 2 day) and
       application_name = 'chat' and
       room_name != "Sample Game" and
       user_id != 'muldoon'
;


select sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60, 1)) as Hours,
       room_name
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 2 day) and
       disconnected_at !=0 and
       application_name = 'chat' and
       room_name != "Sample Game"
group by room_name
order by Hours desc
;


select sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60,2 )) as 'Hours',
       user_name
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 2 day) and
       disconnected_at !=0 and
       application_name = 'chat' and
       room_name != "Sample Game" and
       user_name != 'guest'
group by user_name
order by Hours desc
;


select sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60,2 )) as 'Hours',
       user_name as 'Top users'
  from session_member 
 where disconnected_at !=0 and
       application_name = 'chat' and
       room_name != "Sample Game" and
       user_name != 'guest'
group by user_name
order by Hours desc
limit 30
;



