package vadim.andreich.services;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import vadim.andreich.model.Measure;
import vadim.andreich.model.Sensor;
import vadim.andreich.util.convert.Converter;
import vadim.andreich.util.exceptions.MeasuresException;
import vadim.andreich.util.exceptions.SensorNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChartService {
    private final SensorService sensorService;

    @Autowired
    public ChartService(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    public ByteArrayResource getChartBySensor (String sensorName, int length) throws IOException, MeasuresException, SensorNotFoundException {
        Optional<Sensor> optionalSensor = sensorService.findSensorByName(sensorName);
        if (optionalSensor.isEmpty())
            throw new SensorNotFoundException(String.format("Sensor with name[%s] was not found", sensorName));

        Converter<Map<Date, Double>, List<Measure>> converter = obj -> obj.stream()
                .collect(Collectors.toMap(time -> java.util.Date
                        .from(time.getDateTime().atZone(ZoneId.systemDefault())
                                .toInstant()), value -> (double) value.getMeasureValue()));

        List<Measure> allMeasures = sensorService.getAllMeasurementsBySensorId(optionalSensor.get().getId());

        if (allMeasures.isEmpty()) throw new MeasuresException(String.format("Sensor[%s] has 0 measures", sensorName));
        else if (allMeasures.size() < length) throw new MeasuresException(String.format("Sensor [%s] has only %d measures", sensorName, allMeasures.size()));

        List<Measure> measuresOnlyNeeded = allMeasures.subList(0, length);
        Map<Date, Double> mapOfMeasures = converter.convert(measuresOnlyNeeded);


        List<Double> measures = new ArrayList<>();
        List<Date> time = new ArrayList<>();

        for (Map.Entry<Date, Double> entry : mapOfMeasures.entrySet()) {
            measures.add(entry.getValue());
            time.add(entry.getKey());
        }

        time.sort(Date::compareTo);
        XYChart chart = new XYChartBuilder().width(1200).height(600).title("test").xAxisTitle("X").yAxisTitle("Y").build();
        chart.addSeries("test", time, measures);
        BitmapEncoder.saveBitmap(chart,"img", BitmapEncoder.BitmapFormat.JPG);

        return new ByteArrayResource(Files.readAllBytes(Paths.get("img.jpg")));
    }
}
