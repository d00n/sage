-- MySQL dump 10.13  Distrib 5.1.48, for Win64 (unknown)
--
-- Host: localhost    Database: sage
-- ------------------------------------------------------
-- Server version	5.1.48-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `room` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=429 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session` (
  `session_id` int(11) NOT NULL AUTO_INCREMENT,
  `application_name` varchar(255) DEFAULT NULL,
  `room_id` varchar(255) DEFAULT NULL,
  `room_name` varchar(255) DEFAULT NULL,
  `session_started_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `session_ended_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `user_count` int(11) DEFAULT NULL,
  `total_bytes` int(11) DEFAULT NULL,
  PRIMARY KEY (`session_id`),
  KEY `session_room_id_idx` (`session_id`,`room_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2957 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session_flap`
--

DROP TABLE IF EXISTS `session_flap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session_flap` (
  `session_id` int(11) NOT NULL,
  `application_name` varchar(255) DEFAULT NULL,
  `room_name` varchar(255) DEFAULT NULL,
  `room_id` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `reported_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `peer_connection_status` enum('peer_netconnection_connected','peer_netconnection_connecting','peer_netconnection_disconnected','unknown') DEFAULT 'unknown',
  KEY `session_flap_report_session_id_room_id_idx` (`session_id`,`room_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session_member`
--

DROP TABLE IF EXISTS `session_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session_member` (
  `session_id` int(11) DEFAULT NULL,
  `wowza_client_id` int(11) DEFAULT NULL,
  `room_name` varchar(255) DEFAULT NULL,
  `room_id` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `application_name` varchar(255) DEFAULT NULL,
  `application_version` varchar(255) DEFAULT NULL,
  `avHardwareDisable` char(1) DEFAULT NULL,
  `localFileReadDisable` char(1) DEFAULT NULL,
  `windowless` char(1) DEFAULT NULL,
  `hasTLS` char(1) DEFAULT NULL,
  `hasAudio` char(1) DEFAULT NULL,
  `hasStreamingAudio` char(1) DEFAULT NULL,
  `hasStreamingVideo` char(1) DEFAULT NULL,
  `hasEmbeddedVideo` char(1) DEFAULT NULL,
  `hasMP3` char(1) DEFAULT NULL,
  `hasAudioEncoder` char(1) DEFAULT NULL,
  `hasVideoEncoder` char(1) DEFAULT NULL,
  `hasAccessibility` char(1) DEFAULT NULL,
  `hasPrinting` char(1) DEFAULT NULL,
  `hasScreenPlayback` char(1) DEFAULT NULL,
  `isDebugger` char(1) DEFAULT NULL,
  `hasIME` char(1) DEFAULT NULL,
  `p32bit_support` char(1) DEFAULT NULL,
  `p64bit_support` char(1) DEFAULT NULL,
  `hasScreenBroadcast` char(1) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  `manufacturer` varchar(255) DEFAULT NULL,
  `screenResolution` varchar(255) DEFAULT NULL,
  `screenDPI` varchar(255) DEFAULT NULL,
  `screenColor` varchar(255) DEFAULT NULL,
  `os` varchar(255) DEFAULT NULL,
  `arch` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `playerType` varchar(255) DEFAULT NULL,
  `maxLevelIDC` varchar(255) DEFAULT NULL,
  `pixelAspectRatio` varchar(255) DEFAULT NULL,
  `connected_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `disconnected_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  KEY `session_member_room_id_idx` (`session_id`,`room_id`),
  KEY `session_member_wowza_client_id_idx` (`session_id`,`wowza_client_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session_report`
--

DROP TABLE IF EXISTS `session_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session_report` (
  `session_id` int(11) NOT NULL,
  `application_name` varchar(255) DEFAULT NULL,
  `room_name` varchar(255) DEFAULT NULL,
  `room_id` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `reported_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `c_audioBytesPerSecond` int(11) DEFAULT NULL,
  `c_videoBytesPerSecond` int(11) DEFAULT NULL,
  `c_dataBytesPerSecond` int(11) DEFAULT NULL,
  `c_currentBytesPerSecond` int(11) DEFAULT NULL,
  `c_maxBytesPerSecond` int(11) DEFAULT NULL,
  `c_byteCount` int(11) DEFAULT NULL,
  `c_dataByteCount` int(11) DEFAULT NULL,
  `c_videoByteCount` int(11) DEFAULT NULL,
  `c_audioLossRate` int(11) DEFAULT NULL,
  `c_droppedFrames` int(11) DEFAULT NULL,
  `c_srtt` int(11) DEFAULT NULL,
  `c_wowzaProtocol` varchar(255) DEFAULT NULL,
  `s_lastValidatedTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `s_pingRtt` int(11) DEFAULT NULL,
  `s_fileInBytesRate` int(11) DEFAULT NULL,
  `s_fileOutBytesRate` int(11) DEFAULT NULL,
  `s_messagesInBytesRate` int(11) DEFAULT NULL,
  `s_messagesInCountRate` int(11) DEFAULT NULL,
  `s_messagesLossBytesRate` int(11) DEFAULT NULL,
  `s_messagesLossCountRate` int(11) DEFAULT NULL,
  `s_messagesOutBytesRate` int(11) DEFAULT NULL,
  `s_messagesOutCountRate` int(11) DEFAULT NULL,
  KEY `session_report_session_id_room_id_idx` (`session_id`,`room_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-10-14 14:47:01
