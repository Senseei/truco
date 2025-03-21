package game;

import entities.Carta;
import entities.Jogador;

public class Jogada {
    private final Jogador jogador;
    private final Carta carta;

    public Jogada(Jogador jogador, Carta carta) {
        this.jogador = jogador;
        this.carta = carta;
    }

    public Jogador getJogador() {
        return jogador;
    }

    public Carta getCarta() {
        return carta;
    }
}
