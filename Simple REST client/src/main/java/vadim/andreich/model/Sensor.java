package vadim.andreich.model;

import lombok.*;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "sensor")
public class Sensor {

    @Id
    @Column(name = "sensor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    public Sensor(int sensor) {
        this.id = sensor;
    }
    public Sensor(){}
    public Sensor(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public Sensor(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "sensor", fetch = FetchType.EAGER)
    private List<Measure> measures;

    @Override
    public String toString() {
        return "Sensor{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return id == sensor.id && Objects.equals(measures, sensor.measures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, measures);
    }
}

