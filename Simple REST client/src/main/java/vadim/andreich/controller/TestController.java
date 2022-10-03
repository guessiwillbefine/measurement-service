package vadim.andreich.controller;

import java.util.*;
import org.slf4j.event.Level;
import vadim.andreich.DTO.MeasurementDTO;
import vadim.andreich.model.Measure;
import vadim.andreich.services.SensorService;
import vadim.andreich.util.Parser;
import java.io.FileNotFoundException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

    @GetMapping("/showErrors")
    public String showLogs(@RequestBody(required = false) Map<String, List<String>> values) throws FileNotFoundException {
        if (values == null) return Parser.createParser("/home/vadim/docs/Diploma/test/logs/logfile.log").getParsedLogs(Level.ERROR, Parser.Logs.FULL);
        return Parser.createParser("/home/vadim/docs/Diploma/test/logs/logfile.log").getParsedLogs(Level.ERROR, values.get("keys"));
    }
}
