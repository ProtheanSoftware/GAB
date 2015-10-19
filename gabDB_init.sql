-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2+deb7u1
-- http://www.phpmyadmin.net
--
-- Värd: localhost
-- Skapad: 18 okt 2015 kl 21:11
-- Serverversion: 5.5.44
-- PHP-version: 5.4.45-0+deb7u1

-- Generated and modified by Oscar Hall

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Databas: `gab`
--

-- --------------------------------------------------------

-- --------------------------------------------------------

DROP TABLE IF EXISTS `t_busses`, `t_sessions`, `t_messages`, `t_likes`, `t_dislikes`;

DROP TABLES IF EXISTS t_users;


--
-- Tabellstruktur `t_busses`
--

CREATE TABLE IF NOT EXISTS `t_busses` (
  `buss_id` int(11) NOT NULL AUTO_INCREMENT,
  `system_id` varchar(42) NOT NULL,
  `dgw` varchar(42) NOT NULL,
  PRIMARY KEY (`buss_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=17 ;

-- --------------------------------------------------------


--
-- Tabellstruktur `t_users`
--

CREATE TABLE IF NOT EXISTS `t_users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `fb_id` bigint(11) NOT NULL,
  `interests` text NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=303 ;
--
-- Tabellstruktur `t_dislikes`
--

CREATE TABLE IF NOT EXISTS `t_dislikes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `origin_id` int(11) DEFAULT NULL,
  `like_id` int(11) DEFAULT NULL,
  `like_name` text,
  PRIMARY KEY (`id`),
  KEY `origin_id` (`origin_id`),
  KEY `like_id` (`like_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=39 ;
-- --------------------------------------------------------

--
-- Tabellstruktur `t_likes`
--

CREATE TABLE IF NOT EXISTS `t_likes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `origin_id` int(11) DEFAULT NULL,
  `like_id` int(11) DEFAULT NULL,
  `like_name` text,
  PRIMARY KEY (`id`),
  KEY `origin_id` (`origin_id`),
  KEY `like_id` (`like_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=94 ;

-- --------------------------------------------------------

--
-- Tabellstruktur `t_messages`
--

CREATE TABLE IF NOT EXISTS `t_messages` (
  `message_id` int(11) NOT NULL AUTO_INCREMENT,
  `sender_id` int(11) DEFAULT NULL,
  `reciever_id` int(11) DEFAULT NULL,
  `message` text,
  `sinch_id` text NOT NULL,
  PRIMARY KEY (`message_id`),
  KEY `sender_id` (`sender_id`),
  KEY `reciever_id` (`reciever_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=39 ;

-- --------------------------------------------------------



--
-- Tabellstruktur `t_sessions`
--

CREATE TABLE IF NOT EXISTS `t_sessions` (
  `session_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `dgw` text,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`session_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=35 ;




-- --------------------------------------------------------

--
-- Restriktioner för dumpade tabeller
--

--
-- Restriktioner för tabell `t_users` men vi kommer nog inte använda denna pga att man kan ha flera enheter med gab installerat
--
-- ALTER TABLE `t_users`
--  ADD CONSTRAINT `t_users_unique_fbid` UNIQUE (fb_id);
--
-- Restriktioner för tabell `t_dislikes`
--

ALTER TABLE `t_dislikes`
  ADD CONSTRAINT `t_dislikes_ibfk_1` FOREIGN KEY (`origin_id`) REFERENCES `t_users` (`user_id`),
  ADD CONSTRAINT `t_dislikes_ibfk_2` FOREIGN KEY (`like_id`) REFERENCES `t_users` (`user_id`),
  ADD CONSTRAINT `t_dislikes_unique_dislike` UNIQUE (`origin_id`, `like_id`);

--
-- Restriktioner för tabell `t_likes`
--

ALTER TABLE `t_likes`
  ADD CONSTRAINT `t_likes_ibfk_1` FOREIGN KEY (`origin_id`) REFERENCES `t_users` (`user_id`),
  ADD CONSTRAINT `t_likes_ibfk_2` FOREIGN KEY (`like_id`) REFERENCES `t_users` (`user_id`),
  ADD CONSTRAINT `t_likes_unique_like` UNIQUE (`origin_id`, `like_id`);

--
-- Restriktioner för tabell `t_messages`
--

ALTER TABLE `t_messages`
  ADD CONSTRAINT `t_messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `t_users` (`user_id`),
  ADD CONSTRAINT `t_messages_ibfk_2` FOREIGN KEY (`reciever_id`) REFERENCES `t_users` (`user_id`);

--
-- Restriktioner för tabell `t_sessions`
--

ALTER TABLE `t_sessions`
  ADD CONSTRAINT `t_sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_users` (`user_id`);



--
-- Dumpning av Data i tabell `t_users`
--

INSERT INTO `t_users` (`user_id`, `name`, `fb_id`, `interests`) VALUES
(297, 'Oskar Jedvert', 1011224945566448, '["Festkommittén FestU","digIT","Café Plantaget","DHack","Informationsteknik Chalmers","8-bIT","Team Razer","Razer","Sumoting","Game of Thrones","Humans of New York","Lucas Simonsson","HeatoN till Lets Dance 2016","Christopher \\"GeT_RiGhT\\" Alesund","Teknologsektionen Informationsteknik Chalmers","Pawel Korpulinski Drifting","TBK","Webscout","SexIT","Kommittén GUD","PRIT","Kygo","Chalmers University of Technology","Dan Bilzerian","HookIT"]'),
(298, 'Tobias Allden', 10206632921174252, '["Netflix","Chalmers Studentkår Updates","REACT","Tofsen - Chalmers Kårtidning","Bethesda","frITid","Unofficial: Kundtjänst","digIT","Ian Burrell","Lucas Simonsson","Inte Helt Hundra","Avenged Sevenfold News","Sticky Fingers","Hack Out West","Sinclairs Steakhouse","Gothenburg Startup Hack","Nordiska Kompaniet NK","Morran och Tobias","Dumle","Diplomático","IFK Göteborg","BMW","Corvette","Chalmers Studentkår Rekrytering","Silicon Valley"]'),
(299, 'David Schtek Strom', 1681304598781638, '["frITid","digIT","HeatoN till Lets Dance 2016","XiaoMing","First Hotel Grand - Nattklubben","CHARM - Chalmers Studentkårs Arbetsmarknadsdagar","Chalmersbastun","Kommittén GUD","Chalmers Teknologkonsulter AB","8-bIT","Cremona Chalmers Bokhandel","SweClockers.com","Hubben 2.1","Daniel Eineving (SE)","PRIT","Chalmers University of Technology","Chalmers Pyrotekniska Kommitté","DrawIT Chalmers","NollKIT","Gustav Fridolins kolbit","Teknologsektionen Informationsteknik Chalmers","HookIT","Webscout","SexIT","Festkommittén FestU"]'),
(300, 'Oscar Hall', 10153236412405975, '["Lucas Simonsson","Ardbeg","The Glenlivet","Reningsborg secondhand","Holmens Marknad","Whiskybroker.co.uk","Drams \\u0026 Dreams","Sockerbruket 6 Göteborg","Polisen Tynnered","S.O.P. AB","Teslasverige.nu","Laphroaig","Mickes Whisky","Apoteket Lindome","Höganäs Bryggeri","Nonjatta","Händer i Gbg","Box Single Malt Whisky","Glengoyne Highland Single Malt Scotch Whisky","Smögen Whisky","Artwood","Whiskytaste Sweden","HeatoN till Mästarnas Mästare","Butik Hildur\\u0027s","World Wide Brands"]'),
(301, 'Oscar Boking', 10204993627149215, '["Brooklyn Local","Cyanide \\u0026 Happiness","Lucas Simonsson","digIT","HookIT","HeatoN till Lets Dance 2016","Gothenburg Startup Hack","Trissfredag","INollK","NiP-Gaming","Sumoting","Vi som älskar fredagsmys","Chalmers Pubrunda","Mottagningskommittén - MK","Informationsteknik Chalmers","GasqueK","Recycled Lights UF","Christopher \\"GeT_RiGhT\\" Alesund","Kargoyle","Chalmers Film- \\u0026 FotoCommitté","frITid","Cremona Chalmers Bokhandel","FlashIT","Hubben 2.1","Chalmers Studentkår Promotion"]');




--
-- Dumpning av Data i tabell `t_sessions`
--

INSERT INTO `t_sessions` (`session_id`, `user_id`, `dgw`, `timestamp`) VALUES
(22, 300, 'Ericsson$100020', '2015-10-14 10:54:05'),
(27, 298, 'Ericsson$100020', '2015-10-14 17:59:34'),
(31, 301, 'Ericsson$100020', '2015-10-15 17:09:45'),
(33, 297, 'Ericsson$100020', '2015-10-16 19:08:12'),
(34, 299, 'Ericsson$100020', '2015-10-18 11:17:45');


--
-- Dumpning av Data i tabell `t_messages`
--

INSERT INTO `t_messages` (`message_id`, `sender_id`, `reciever_id`, `message`, `sinch_id`) VALUES
(1, 298, 297, 'Hej oskar', '5dd16043-73bc-4e6a-be54-acac8afe67d7'),
(2, 297, 298, 'hej allden', '89a369e0-c5e7-48e9-84ab-acdd6d0c25a9'),
(4, 297, 298, 'ballle', 'd0fe1c31-e5e7-403b-8e3a-8ca43e1dca97'),
(5, 298, 298, 'yo', 'cc03fb6c-865e-4895-8a92-582dde5eb86b'),
(6, 298, 298, 'hej', 'a09d4bfc-a3b9-4326-9d17-18e9a111d131'),
(7, 298, 297, 'swag', 'd1530f57-631d-4072-9574-be9412f13e4c'),
(8, 297, 297, 'asf', 'ada22cf7-856e-4b78-96d9-8e737831ef02'),
(9, 299, 299, 'gjkjj', '36d8eb73-1615-4382-b834-d673477e7350'),
(10, 299, 299, 'hjvh', '13ff337e-5812-4d36-9e62-d6144fac0e89'),
(11, 297, 297, 'asf', '5d14bef8-2707-4dd4-93c2-4fb0dac62c5b'),
(12, 301, 297, 'hej', 'ad704f05-349b-4cd6-abb2-ba698ffbc6b3'),
(13, 301, 298, 'swag', '2e223c57-8e16-4942-a08a-ae92409d6ebe'),
(14, 297, 297, 'asdf', '04810916-a60b-44bf-9b90-d031a5589125'),
(15, 297, 297, 'ood', '609bf045-d3ef-444d-af51-aa08f0d45a59'),
(16, 297, 297, 'oood', '53a61e6c-74c4-4018-9b1b-d4eff6d78719'),
(17, 297, 301, 'heej', '4e908f53-3acc-4119-bf75-9d6b4b79790c'),
(18, 297, 301, 'funkar det? ', '6b3a07e4-01ee-4964-9da7-a09c039435a5'),
(20, 297, 301, 'damn', 'ebcf1df3-52f3-47f0-867a-eeb231f861eb'),
(21, 301, 297, 'hej2', 'b04120f6-2a48-4880-951e-cba4b8229db2'),
(22, 301, 297, 'hejigen', 'ef010335-d402-45c5-a0f1-cc00064ca2a5'),
(23, 297, 297, 'wow', '8a28eea8-c8da-4a70-b1a0-a050235c2fae'),
(24, 301, 297, 'sweg', '9eb8028f-5443-421b-8b33-982a1c725a20'),
(25, 297, 297, 'such amaze', 'f6e72d81-f70a-4d8d-bc17-a51af7eea793'),
(26, 300, 297, 'cool', 'bc446ba7-55fd-491b-984f-d7701b845950'),
(27, 297, 301, 'sjs', 'e6253863-85d8-42d6-a0b6-b0100feade69'),
(28, 299, 297, 'ghjjj', 'b4daa9c1-6486-49a4-8ca9-b9d648bd22c9'),
(29, 297, 299, 'hej', 'b9df64d6-b0a5-49d6-901e-dd68e0c556b2'),
(30, 299, 297, 'haj!', '252571f4-d0be-47dc-a1eb-928843353173'),
(31, 297, 299, 'hej din tjej ', 'd5f21443-0b46-461a-b040-8d49e8675cc4'),
(32, 299, 297, 'waaat', 'e0c9814b-92a4-4be7-b7ea-3536e59591ec'),
(33, 299, 297, 'allååå', '5717b8ed-897e-400f-8ce7-6a6c389c4186'),
(34, 300, 300, 'hek', 'd42d4638-8326-4585-a86d-47d267c57ed3'),
(35, 299, 297, 'vvvvvy55555', '9d73d34c-dc7c-424c-8f78-54d624d0aa94'),
(36, 299, 297, 'iiiiiii', '2379e74e-e93e-416e-94e2-9c0073f81087'),
(37, 299, 297, 'kkllkkllkllkkk', '3a49235c-7588-4219-b9cf-2fccc28995eb'),
(38, 300, 298, 'yooo', '6ece2c1c-55b3-4b3f-85ab-a353da340c29');


--
-- Dumpning av Data i tabell `t_busses`
--

INSERT INTO `t_busses` (`buss_id`, `system_id`, `dgw`) VALUES
(5, '2501069301', 'Ericsson$100020'),
(7, '2501069758', 'Ericsson$100021'),
(9, '2501131248', 'Ericsson$100022'),
(10, '2501142922', 'Ericsson$171164'),
(11, '2501069303', 'Ericsson$171234'),
(12, '2500825764', 'Ericsson$171235'),
(13, '2501075606', 'Ericsson$171327'),
(14, '2501069756', 'Ericsson$171328'),
(15, '2501131250', 'Ericsson$171329'),
(16, '2501074720', 'Ericsson$171330');


--
-- Dumpning av Data i tabell `t_likes`
--

INSERT INTO `t_likes` (`id`, `origin_id`, `like_id`, `like_name`) VALUES
 (21, 301, 299, 'David Schtek Strom'),
 (22, 301, 298, 'Tobias Allden'),
 (23, 301, 300, 'Oscar Hall'),
 (80, 297, 299, 'David Schtek Strom'),
 (81, 297, 301, 'Oscar Boking'),
 (82, 297, 298, 'Tobias Allden'),
 (84, 298, 297, 'Oskar Jedvert'),
 (39, 298, 299, 'David Schtek Strom'),
 (85, 298, 301, 'Oscar Boking'),
 (88, 299, 297, 'Oskar Jedvert'),
 (64, 299, 300, 'Oscar Hall'),
(89, 299, 301, 'Oscar Boking');


CREATE OR REPLACE VIEW `v_matches` AS (SELECT t1.id, t1.origin_id, t1.like_id, t1.like_name
FROM t_likes t1
INNER JOIN t_likes t2 ON t1.origin_id = t2.like_id
WHERE (t1.origin_id = t2.like_id AND t1.like_id = t2.origin_id));

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
