package entities;

import java.util.Objects;

public class Figura implements Comparable<Figura> {
    private Integer power;
    private String label;

    Figura(Integer power, String label) {
        this.power = power;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getPower() {
        return power;
    }

    @Override
    public int compareTo(Figura figura) {
        return power.compareTo(figura.power);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Figura figura)) return false;
        return Objects.equals(power, figura.power) && Objects.equals(label, figura.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(power, label);
    }
}
