drop table if exists room;
drop table if exists session;
drop table if exists session_member;
drop table if exists session_report;

create table room (
  room_id int auto_increment,
  created_at timestamp default now(),
  primary key (room_id)
);

create table session (
  session_id int auto_increment,
  application_name varchar(255),
  room_id varchar(255),
  room_name varchar(255),
  session_started_at timestamp default now(),
  session_ended_at timestamp,
  user_count int,
  total_bytes int,
  primary key (session_id),
  index session_room_id_idx (session_id, room_id)
);


create table session_member (
  session_id int,
  wowza_client_id int,
  room_name varchar(255),
  room_id varchar(255),
  user_name varchar(255),
  user_id varchar(255),

  application_name varchar(255),
  application_version varchar(255),
  avHardwareDisable char(1),				
  localFileReadDisable char(1),
  windowless char(1),
  hasTLS char(1),
  hasAudio char(1),
  hasStreamingAudio char(1),
  hasStreamingVideo char(1),
  hasEmbeddedVideo char(1),
  hasMP3 char(1),
  hasAudioEncoder char(1),
  hasVideoEncoder char(1),
  hasAccessibility char(1),
  hasPrinting char(1),
  hasScreenPlayback char(1),
  isDebugger char(1),
  hasIME char(1),
  p32bit_support char(1),
  p64bit_support char(1),				
  hasScreenBroadcast char(1),
  version varchar(255),
  manufacturer varchar(255),
  screenResolution varchar(255),
  screenDPI varchar(255),
  screenColor varchar(255),
  os  varchar(255),
  arch  varchar(255),
  language varchar(255),
  playerType varchar(255),
  maxLevelIDC varchar(255),
  pixelAspectRatio varchar(255),

  connected_at timestamp default current_timestamp,
  disconnected_at timestamp,

  index session_member_room_id_idx (session_id, room_id),
  index session_member_wowza_client_id_idx (session_id, wowza_client_id)
);

create table session_report (
  session_id int not null,
  application_name varchar(255),
  room_name varchar(255),
  room_id varchar(255),
  user_name varchar(255),
  user_id varchar(255),
  reported_at timestamp default now(),

  c_audioBytesPerSecond int,
  c_videoBytesPerSecond int,
  c_dataBytesPerSecond int,
  c_currentBytesPerSecond int,
  c_maxBytesPerSecond int,
  c_byteCount int,
  c_dataByteCount int,
  c_videoByteCount int,
  c_audioLossRate int,
  c_droppedFrames int,
  c_srtt int,
  c_wowzaProtocol varchar(255),

  s_lastValidatedTime timestamp,
  s_pingRtt int,
  s_fileInBytesRate int,
  s_fileOutBytesRate int,
  s_messagesInBytesRate int,
  s_messagesInCountRate int,
  s_messagesLossBytesRate int,
  s_messagesLossCountRate int,
  s_messagesOutBytesRate int,
  s_messagesOutCountRate int,

  index session_report_session_id_room_id_idx (session_id, room_id)
);
