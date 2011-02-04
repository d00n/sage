select round(sum(timestampdiff(second, connected_at, disconnected_at))/60/60,2) as hours, 
       room_name 
  from session_member 
 where application_name = 'whiteboard' 
group by room_name 
order by hours desc
limit 50
;

