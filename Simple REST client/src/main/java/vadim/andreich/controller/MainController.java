package vadim.andreich.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.MediaType;
import vadim.andreich.model.Sensor;
import vadim.andreich.services.MeasureService;
import vadim.andreich.util.Parser;
import vadim.andreich.model.Measure;

import java.io.FileNotFoundException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import vadim.andreich.dto.MeasurementDTO;
import org.springframework.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import vadim.andreich.services.SensorService;
import vadim.andreich.util.convert.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vadim.andreich.util.exceptions.SensorNotFoundResponse;
import vadim.andreich.util.exceptions.SensorNotFoundException;


@RestController
@RequestMapping("/api")
public class MainController {
    private final SensorService sensorService;
    private final MeasureService measureService;
    private final Logger logger;

    @Autowired
    public MainController(SensorService sensorService, MeasureService measureService) {
        this.sensorService = sensorService;
        this.measureService = measureService;
        logger = LogManager.getLogger(MainController.class);
    }

    @PostMapping("/save")
    public ResponseEntity<Boolean> save(@RequestBody MeasurementDTO dto) {
        if (logger.isDebugEnabled()) {
            if (dto.getValue() <= 22) {
                logger.info(String.format("everything is ok, current temperature is %d C°", dto.getValue()));
            } else if (dto.getValue() > 22 && dto.getValue() < 25) {
                logger.warn(String.format("everything is almost ok, but current temperature is %d C°", dto.getValue()));
            }
        } else {
            logger.error(String.format("Critical temperature, pls do something %d C°", dto.getValue()));
        }
        Measure newMeasure = new Measure(dto.getValue(), dto.getSensor());
        return ResponseEntity.of(Optional.of(sensorService.saveMeasurement(newMeasure)));
    }

    @PostMapping("/register")
    public Sensor saveNewSensor(@RequestParam(value = "name", required = false) String name) {
        if (name == null) {
            return sensorService.saveNew();
        }
        return sensorService.saveNew(name);
    }

    @GetMapping(value = "/showErrors")
    public String showLogs(@RequestBody(required = false) Map<String, List<String>> values) throws FileNotFoundException {
        if (values == null) {
            return Parser.createParser("/home/vadim/docs/Diploma/test/logs/logfile.log").getParsedLogs(Level.ERROR, Parser.Logs.FULL);
        }
        return Parser.createParser("/home/vadim/docs/Diploma/test/logs/logfile.log").getParsedLogs(Level.ERROR, values.get("keys"));
    }

    @GetMapping(value = "/getMeasure/{id}")
    public ResponseEntity<List<MeasurementDTO>> getAllMeasuresById(@PathVariable int id) {
        List<Measure> measures = sensorService.getAllMeasurementsBySensorId(id);
        Converter<MeasurementDTO, Measure> converter = original -> new MeasurementDTO(
                original.getValue(),
                original.getDateTime());

        List<MeasurementDTO> measurementDTOList = measures.stream()
                .map(converter::convert)
                .toList();

        return ResponseEntity.of(Optional.of(measurementDTOList));
    }

    @GetMapping(value = "stats/{sensor}/{length}",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody ResponseEntity<Resource> getStatsOfLastMeasurements(@PathVariable("length") int length,
                                                                             @PathVariable("sensor") String sensorName) throws IOException {
        Optional<Sensor> optionalSensor = sensorService.findSensorByName(sensorName);
        if (optionalSensor.isEmpty()) throw new SensorNotFoundException(String.format("Sensor with name[%s] was not found", sensorName));

        Converter<Map<LocalDateTime, Double>,List<Measure>> converter = obj -> obj.stream()
                .collect(Collectors.toMap(Measure::getDateTime, value -> Double.valueOf(value.getValue())));

        List<Measure> measuresOnlyNeeded = sensorService.getAllMeasurementsBySensorId(optionalSensor.get().getId()).subList(0, length);
        Map<LocalDateTime, Double> mapOfMeasures = converter.convert(measuresOnlyNeeded);

        List<Double> measures = mapOfMeasures.values().stream().toList();
        List<Double> time = Stream.iterate(0.0,  x -> x + 1).limit(measures.size()).toList();
        XYChart chart = QuickChart.getChart("weather", "day", "temperature", "Y(X)", time, measures);
        BitmapEncoder.saveBitmap(chart, "img", BitmapEncoder.BitmapFormat.JPG);
        final ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(Paths.get("img.jpg")));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .body(inputStream);
    }

    @ExceptionHandler(SensorNotFoundException.class)
    private ResponseEntity<SensorNotFoundResponse> handle(SensorNotFoundException exception) {
        SensorNotFoundResponse response = new SensorNotFoundResponse(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
