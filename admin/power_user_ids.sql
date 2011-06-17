select user_id
  from session_member 
 where disconnected_at !=0 and
       application_name = 'whiteboard'
group by user_name
order by sum(round(timestampdiff(second, connected_at, disconnected_at)/60/60,2 )) desc
limit 100
;



