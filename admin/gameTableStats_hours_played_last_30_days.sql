select sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60, 2)) as 'Hours',
       date_sub(now(), interval 31 day) as 'starting',
       date_sub(now(), interval 1 day) as 'ending'
  from session_member
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 32 day) and
       disconnected_at !=0 and
       application_name = 'whiteboard'
;

select sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60/30, 2)) as 'Hours/day',
       date_sub(now(), interval 31 day),
       date_sub(now(), interval 1 day)
  from session_member
 where connected_at <= date_sub(now(), interval 1 day) and
       connected_at > date_sub(now(), interval 32 day) and
       disconnected_at !=0 and
       application_name = 'whiteboard'
;

