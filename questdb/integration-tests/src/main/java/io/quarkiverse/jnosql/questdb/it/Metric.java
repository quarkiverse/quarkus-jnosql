package io.quarkiverse.jnosql.questdb.it;

import java.time.Instant;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public class Metric {

    @Id
    private Instant timestamp;

    @Column
    private String source;

    @Column
    private double value;

    public Metric() {
    }

    public Metric(Instant timestamp, String source, double value) {
        this.timestamp = timestamp;
        this.source = source;
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
