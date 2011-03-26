
DROP TABLE IF EXISTS `image`;
CREATE TABLE `image` (
  `session_id` int(11) PRIMARY KEY DEFAULT 1,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `path` varchar(255),
  KEY `image_created_on_idx` (`created_on`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
