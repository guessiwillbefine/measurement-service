-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`sensor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`sensor` (
                                               `sensor_id` INT NOT NULL,
                                               PRIMARY KEY (`sensor_id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`measure`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`measure` (
                                                `sensor_id` INT NOT NULL,
                                                `measure_value` VARCHAR(45) NOT NULL,
                                                `measure_id` INT NOT NULL,
                                                `time` DATETIME NULL,
                                                INDEX `fk_measure_sensor_idx` (`sensor_id` ASC) VISIBLE,
                                                PRIMARY KEY (`measure_id`),
                                                CONSTRAINT `fk_measure_sensor`
                                                    FOREIGN KEY (`sensor_id`)
                                                        REFERENCES `mydb`.`sensor` (`sensor_id`)
                                                        ON DELETE NO ACTION
                                                        ON UPDATE NO ACTION)
    ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
