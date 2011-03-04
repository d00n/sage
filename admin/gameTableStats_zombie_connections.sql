select connected_at, 
       user_name, 
       room_name 
  from session_member 
 where timestampdiff(hour, connected_at, now()) > 24 and disconnected_at = 0 
limit 50;
