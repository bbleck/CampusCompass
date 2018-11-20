CREATE TABLE IF NOT EXISTS `Token`(
  `title` TEXT COLLATE NOCASE,
  `building_num` TEXT,
  `abbr` TEXT,
  `campus` TEXT,
  `keywords` TEXT,
  `image` TEXT,
  `link` TEXT,
  `token_id` TEXT NOT NULL,
  `description` TEXT,
  `token_type` INTEGER,
  `mLongitude` REAL,
  `mLatitude` REAL,
  PRIMARY KEY(`token_id`)
);
