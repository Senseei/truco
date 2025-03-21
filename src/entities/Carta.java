package entities;

import enums.Naipe;

import java.util.Objects;

public class Carta implements Comparable<Carta> {
    private Naipe naipe;
    private Figura figura;
    private boolean manilha;

    public Carta(Naipe naipe, Figura figura) {
        this.naipe = naipe;
        this.figura = figura;
        manilha = false;
    }

    public Carta(Naipe naipe, Figura figura, boolean manilha) {
        this.naipe = naipe;
        this.figura = figura;
        this.manilha = manilha;
    }

    public Naipe getNaipe() {
        return naipe;
    }

    public Figura getFigura() {
        return figura;
    }

    public boolean isManilha() {
        return manilha;
    }

    public void setManilha(boolean manilha) {
        this.manilha = manilha;
    }

    @Override
    public int compareTo(Carta carta) {
        if (manilha) {
            return naipe.getPower().compareTo(carta.getNaipe().getPower());
        }
        return figura.compareTo(carta.getFigura());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carta carta = (Carta) o;
        return getNaipe() == carta.getNaipe() && Objects.equals(getFigura(), carta.getFigura());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNaipe(), getFigura());
    }

    @Override
    public String toString() {
        return "|" + figura.getLabel() + naipe.getLabel() + "|";
    }
}
