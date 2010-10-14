

DROP TABLE IF EXISTS `session_flap_report`;
DROP TABLE IF EXISTS `session_flap`;
CREATE TABLE `session_flap` (
  `session_id` int(11) NOT NULL,
  `application_name` varchar(255) DEFAULT NULL,
  `room_name` varchar(255) DEFAULT NULL,
  `room_id` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `reported_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `peer_connection_status` enum("peer_netconnection_connected","peer_netconnection_connecting","peer_netconnection_disconnected","unknown") default "unknown",
  KEY `session_flap_report_session_id_room_id_idx` (`session_id`,`room_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
