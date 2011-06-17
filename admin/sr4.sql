select
s_pingRtt as ping,

 c_audioBytesPerSecond as a,
 c_videoBytesPerSecond as b ,

 c_dataBytesPerSecond as c,
 c_currentBytesPerSecond as d,
 c_maxBytesPerSecond as e,
 c_byteCount as f,
 c_dataByteCount as g,
 c_videoByteCount as h ,
 c_droppedFrames as k,
 s_messagesInBytesRate as r ,
 s_messagesInCountRate as s ,
 s_messagesLossBytesRate as t ,
 s_messagesLossCountRate as u,
 s_messagesOutBytesRate as v,
 s_messagesOutCountRate as w
from session_report 
where application_name = 'chat' 
