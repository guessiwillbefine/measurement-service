package vadim.andreich.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vadim.andreich.dto.MeasurementDTO;
import vadim.andreich.model.Measure;
import vadim.andreich.model.Sensor;
import vadim.andreich.services.ChartService;
import vadim.andreich.services.SensorService;
import vadim.andreich.util.Parser;
import vadim.andreich.util.convert.Converter;
import vadim.andreich.util.exceptions.MeasuresException;
import vadim.andreich.util.exceptions.SensorNotFoundException;
import vadim.andreich.util.response.NullMeasuresResponse;
import vadim.andreich.util.response.SensorNotFoundResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class MainController {
    private final SensorService sensorService;
    private final ChartService chartService;
    private final Logger logger;

    @Autowired
    public MainController(SensorService sensorService, ChartService chartService) {
        this.sensorService = sensorService;
        this.chartService = chartService;
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
                                                                             @PathVariable("sensor") String sensorName) throws IOException, MeasuresException {

        ByteArrayResource inputStream = chartService.getChartBySensor(sensorName, length);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .contentType(MediaType.IMAGE_JPEG)
                .body(inputStream);
    }

    @ExceptionHandler(SensorNotFoundException.class)
    private ResponseEntity<SensorNotFoundResponse> handleSensorNotFoundException(SensorNotFoundException exception) {
        SensorNotFoundResponse response = new SensorNotFoundResponse(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MeasuresException.class)
    private ResponseEntity<NullMeasuresResponse> handleMeasuresException(MeasuresException exception) {
        NullMeasuresResponse response = new NullMeasuresResponse(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
