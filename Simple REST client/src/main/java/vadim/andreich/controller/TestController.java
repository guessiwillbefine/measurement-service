package vadim.andreich.controller;

import java.util.*;
import org.slf4j.event.Level;
import vadim.andreich.util.Parser;
import vadim.andreich.model.Measure;
import java.io.FileNotFoundException;
import org.apache.logging.log4j.Logger;
import vadim.andreich.DTO.MeasurementDTO;
import org.apache.logging.log4j.LogManager;
import vadim.andreich.services.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {
    private final SensorService sensorService;
    private final Logger logger;
    
    public TestController(SensorService sensorService) {
        this.sensorService = sensorService;
        logger = LogManager.getLogger(TestController.class);
    }

    @PostMapping("/save")
    public ResponseEntity<Boolean> save(@RequestBody MeasurementDTO dto){
        System.err.println(dto);
        if (dto.getValue() <= 22){
            if (logger.isDebugEnabled()) {
                logger.info(String.format("everything is ok, current temperature is %d C°", dto.getValue()));
            }
        }
        else if(dto.getValue() > 22 && dto.getValue() < 25){
            if (logger.isDebugEnabled()) {
                logger.warn(String.format("everything is almost ok, but current temperature is %d C°", dto.getValue()));
            }
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.error(String.format("Critical temperature, pls do something %d C°", dto.getValue()));
            }
        }
        sensorService.saveMeasurement(new Measure(dto.getValue(), dto.getSensor()));
        return ResponseEntity.of(Optional.of(true));
    }
    @GetMapping("/getMeasure/{id}")
    public ResponseEntity<List<MeasurementDTO>> getAllMeasuresById(@PathVariable int id){
        List<Measure> measures = sensorService.getAllMeasurementsByIdSensor(id);
        List<MeasurementDTO> measurementDTOList = measures.stream()
                .map(measure -> {
                    MeasurementDTO m = new MeasurementDTO();
                    m.setSensor(id);
                    m.setValue(measure.getValue());
                    m.setLocalDateTime(measure.getDateTime());
                    return m;
                })
                .toList();
        return ResponseEntity.of(Optional.of(measurementDTOList));
    }

    @PostMapping("/register")
    public void saveNewSensor(){
        sensorService.saveNew();
    }

    @GetMapping("/showErrors")
    public String showLogs(@RequestBody(required = false) Map<String, List<String>> values) throws FileNotFoundException {
        if (values == null) return Parser.createParser("/home/vadim/docs/Diploma/test/logs/logfile.log").getParsedLogs(Level.ERROR, Parser.Logs.FULL);
        return Parser.createParser("/home/vadim/docs/Diploma/test/logs/logfile.log").getParsedLogs(Level.ERROR, values.get("keys"));
    }
}
