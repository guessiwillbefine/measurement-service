package vadim.andreich.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "measure")
public class Measure {

    @Id
    @Column(name = "measure_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "measure_value")
    private int value;
    @Column(name = "time")
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "sensor_id", referencedColumnName = "sensor_id")
    private Sensor sensor;

    public Measure(int value, int id) {
        this.value = value;
        this.dateTime = LocalDateTime.now();
        this.sensor = new Sensor(id);
    }
    public Measure(int value, Sensor sensor) {
        this.value = value;
        this.dateTime = LocalDateTime.now();
        this.sensor = sensor;
    }

    public Measure() {}

    public Measure(int id, int value, LocalDateTime dateTime, Sensor sensor) {
        this.id = id;
        this.value = value;
        this.dateTime = dateTime;
        this.sensor = sensor;
    }
}
