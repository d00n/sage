select connected_at, disconnected_at, timestampdiff(second, connected_at, disconnected_at) as seconds
  from session_member 
 where connected_at >= date_sub(now(), interval 1 day) and
       connected_at < date_sub(now(), interval 2 day) and
       connected_at !=0

