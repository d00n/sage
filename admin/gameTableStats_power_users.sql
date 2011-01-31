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



