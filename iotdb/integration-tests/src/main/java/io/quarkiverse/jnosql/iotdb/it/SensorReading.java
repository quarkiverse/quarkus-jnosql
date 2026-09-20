package io.quarkiverse.jnosql.iotdb.it;

import java.time.Instant;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public class SensorReading {

    @Id
    private Instant timestamp;

    @Column
    private String sensor;

    @Column
    private double value;

    public SensorReading() {
    }

    public SensorReading(Instant timestamp, String sensor, double value) {
        this.timestamp = timestamp;
        this.sensor = sensor;
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
