select sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60,2 )) as 'Hours',
       user_name
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 2 day) and
       disconnected_at !=0 and
       application_name = 'chat' and
       room_name != "Sample Game"
group by user_name
order by Hours desc
;

