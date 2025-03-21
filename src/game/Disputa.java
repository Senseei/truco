package game;

import entities.Carta;
import entities.Jogador;
import interfaces.CartasUsadas;

import java.util.ArrayList;
import java.util.List;

public class Disputa implements CartasUsadas {
    private final Jogada[] jogadas = new Jogada[2];
    private int index = 0;

    public Jogada[] getJogadas() {
        return jogadas;
    }

    @Override
    public List<Carta> getCartasUsadas() {
        List<Carta> cartasUsadas = new ArrayList<>();
        for (Jogada jogada : jogadas) {
            if (jogada != null) {
                cartasUsadas.add(jogada.getCarta());
            }
        }
        return cartasUsadas;
    }

    public void addJogada(Jogada jogada) {
        if (index == 2) {
            throw new IllegalStateException("Disputa já está completa");
        }
        jogadas[index++] = jogada;
    }

    public boolean isFull() {
        return jogadas[1] != null;
    }

    public Jogador getVencedor() {
        if (!isFull()) {
            return null;
        }

        int comparison = jogadas[0].getCarta().compareTo(jogadas[1].getCarta());
        return comparison == 0 ? null : (comparison > 0 ? jogadas[0].getJogador() : jogadas[1].getJogador());
    }

    @Override
    public String toString() {
        return (jogadas[0] != null ? jogadas[0].getCarta().toString() : "") + (jogadas[1] != null ? ", " + jogadas[1].getCarta().toString() : "");
    }
}
