select connected_at, room_name, room_id, user_name, user_id
  from session_member 
 where disconnected_at = 0
;
