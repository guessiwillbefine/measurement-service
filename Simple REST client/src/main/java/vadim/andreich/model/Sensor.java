package vadim.andreich.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "sensor")
public class Sensor {

    @Id
    @Column(name = "sensor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public Sensor(int sensor) {
        this.id = sensor;
    }
    public Sensor(){}

    @OneToMany(mappedBy = "sensor", fetch = FetchType.EAGER)
    private List<Measure> measures;

    @Override
    public String toString() {
        return "Sensor{" +
                "id=" + id +
                '}';
    }
}

