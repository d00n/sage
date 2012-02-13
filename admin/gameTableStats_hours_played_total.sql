select sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60, 2)) as 'Total hours'
  from session_member 
 where disconnected_at !=0 and
       application_name = 'whiteboard'
;

