select sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60, 2)) as 'Hours yesterday',
       date_sub(now(), interval 2 day) as 'starting',
       date_sub(now(), interval 1 day) as 'ending'
  from session_member 
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 2 day) and
       disconnected_at !=0 and
       application_name = 'whiteboard'
;

