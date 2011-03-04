select sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60, 1)) as Hours,
       room_name as 'Game'
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 2 day) and
       disconnected_at !=0 and
       application_name = 'whiteboard'
group by room_name
order by Hours desc
;
