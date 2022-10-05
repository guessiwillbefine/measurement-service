CREATE TABLE sensor
(
    sensor_id INT AUTO_INCREMENT NOT NULL,
    CONSTRAINT pk_sensor PRIMARY KEY (sensor_id)
);
CREATE TABLE measure
(
    measure_id    INT AUTO_INCREMENT NOT NULL,
    measure_value INT                NOT NULL,
    time          TIMESTAMP,
    sensor_id     INT,
    CONSTRAINT pk_measure PRIMARY KEY (measure_id)
);

ALTER TABLE measure
    ADD CONSTRAINT FK_MEASURE_ON_SENSOR FOREIGN KEY (sensor_id) REFERENCES sensor (sensor_id);