drop table if exists room;
drop table if exists session;
drop table if exists session_report;

create table room (
  room_id int auto_increment,
  room_name varchar(255),
  primary key (room_id)
);

create table session (
  session_id int auto_increment,
  room_id int not null,
  room_name varchar(255) not null,
  session_started_at timestamp default now(),
  session_ended_at timestamp,
  user_count int,
  total_bytes int,
  primary key (session_id),
  index session_room_id_idx (room_id)
);

create table session_report (
  session_id int not null,
  room_id int not null,
  room_name varchar(255) not null,
  user_id int not null,
  user_name varchar(255) not null,
  reported_at timestamp default now(),
  audio_bytes_per_second int,
  video_bytes_per_second int,
  data_bytes_per_second int,
  current_bytes_per_second int,
  max_bytes_per_second int,
  byte_count int,
  data_byte_count int,
  video_byte_count int,
  audio_loss_rate int,
  dropped_frames int,
  index session_report_session_id_room_id_idx (session_id, room_id),
  index session_report_session_id_user_id_idx (session_id, user_id)
);
