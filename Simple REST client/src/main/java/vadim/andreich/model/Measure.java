package vadim.andreich.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@ToString
@Entity
@Table(name = "measure")
public class Measure {

    @Id
    @Column(name = "measure_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "measure_value")
    private int measureValue;
    @Column(name = "time")
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "sensor_id", referencedColumnName = "sensor_id")
    private Sensor sensor;

    public Measure(int value, int id) {
        this.measureValue = value;
        this.dateTime = LocalDateTime.now();
        this.sensor = new Sensor(id);
    }
    public Measure(int value, Sensor sensor) {
        this.measureValue = value;
        this.dateTime = LocalDateTime.now();
        this.sensor = sensor;
    }

    public Measure() {}

    public Measure(int id, int value, LocalDateTime dateTime, Sensor sensor) {
        this.id = id;
        this.measureValue = value;
        this.dateTime = dateTime;
        this.sensor = sensor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Measure measure = (Measure) o;
        return Objects.equals(id, measure.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
