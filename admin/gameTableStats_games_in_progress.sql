select connected_at, user_name, room_name
  from session_member 
 where disconnected_at = 0 and
       application_name = 'whiteboard'
;
