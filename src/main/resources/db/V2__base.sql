DROP TABLE IF EXISTS `areas`;

CREATE TABLE `areas` (
  `id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `discriminator` int(11) NOT NULL,
  `name` char(128) NOT NULL DEFAULT '',
  `source` tinyint(2) NOT NULL,
  `validity_start` datetime DEFAULT NULL,
  `validity_end` datetime DEFAULT NULL,
  `kml` blob NOT NULL,
  `police_force` char(128) NOT NULL DEFAULT '',
  `police_neighborhood` char(128) NOT NULL DEFAULT ''
);

DROP TABLE IF EXISTS `information`;

CREATE TABLE `information` (
  `id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `discriminator` int(11) NOT NULL,
  `area_id` int(11) NOT NULL,
  `validity_start` datetime DEFAULT NULL,
  `validity_end` datetime DEFAULT NULL,
  CONSTRAINT `fk_area` FOREIGN KEY (`area_id`) REFERENCES `areas` (`id`) ON UPDATE CASCADE
);

DROP TABLE IF EXISTS `police_crime_data`;

CREATE TABLE `police_crime_data` (
  `id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `information_id` int(11) unsigned NOT NULL,
  `all_crime` mediumint(9) NOT NULL,
  `anti_social_behavior` mediumint(9) NOT NULL,
  `burglary` mediumint(9) NOT NULL,
  `criminal_damage` mediumint(9) NOT NULL,
  `drugs` mediumint(9) NOT NULL,
  `other_theft` mediumint(9) NOT NULL,
  `public_disorder` mediumint(9) NOT NULL,
  `robbery` mediumint(9) NOT NULL,
  `shoplifting` mediumint(9) NOT NULL,
  `vehicle_crime` mediumint(9) NOT NULL,
  `violent_crime` mediumint(9) NOT NULL,
  `other_crime` mediumint(9) NOT NULL,
  CONSTRAINT `fk_information` FOREIGN KEY (`information_id`) REFERENCES `information` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);