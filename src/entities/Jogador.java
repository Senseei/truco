package entities;

import game.Jogada;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Jogador {
    private String name;
    private int pontos;
    private List<Carta> mao = new ArrayList<>();

    public Jogador(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPontos() {
        return pontos;
    }

    public List<Carta> getMao() {
        return mao;
    }

    public void pontuar(int pontos) {
        this.pontos += pontos;
    }

    public void pegarCarta(Carta carta) {
        mao.add(carta);
    }

    public Jogada jogarCarta(int index) {
        return new Jogada(this, mao.remove(index));
    }

    public boolean hasManilha() {
        return mao.stream().anyMatch(Carta::isManilha);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jogador jogador = (Jogador) o;
        return Objects.equals(getName(), jogador.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
