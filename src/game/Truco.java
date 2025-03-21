package game;

import entities.Jogador;

public class Truco {
    private final int pontos;
    private final int pontosAnteriores;
    private final Jogador quemPediu;
    private boolean aceito;

    public Truco(Jogador quemPediu) {
        this.pontos = 3;
        this.pontosAnteriores = 1;
        this.quemPediu = quemPediu;
    }

    public Truco(int pontos, int pontosAnteriores, Jogador quemPediu) {
        this.pontos = pontos;
        this.pontosAnteriores = pontosAnteriores;
        this.quemPediu = quemPediu;
    }

    public int getPontos() {
        return pontos;
    }

    public int getPontosAnteriores() {
        return pontosAnteriores;
    }

    public Jogador getQuemPediu() {
        return quemPediu;
    }

    public boolean isAceito() {
        return aceito;
    }

    public void aceitar() {
        aceito = true;
    }
}
