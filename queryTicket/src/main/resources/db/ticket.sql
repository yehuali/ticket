CREATE TABLE `ticket`.`station_code` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `station_code` VARCHAR(45) NOT NULL DEFAULT '车站码值',
  `station_value` VARCHAR(45) NOT NULL DEFAULT '车站中文',
  `station_spell` VARCHAR(45) NOT NULL DEFAULT '车站拼音',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `station_code_UNIQUE` (`station_code` ASC),
  UNIQUE INDEX `station_value_UNIQUE` (`station_value` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = '车站码值表';
