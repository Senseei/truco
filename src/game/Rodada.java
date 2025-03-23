package game;

import entities.Carta;
import entities.Jogador;
import interfaces.CartasUsadas;

import java.util.*;

public class Rodada implements CartasUsadas {
    private final Disputa[] disputas = new Disputa[3];
    private int index = 0;
    private Disputa disputaAtual = new Disputa();
    private Truco truco;
    private Jogador vez;
    private boolean asCegas;

    public Rodada(Jogador vez) {
        this.vez = vez;
    }

    public Disputa[] getDisputas() {
        return disputas;
    }

    public Jogador getVez() {
        return vez;
    }

    public void setVez(Jogador vez) {
        this.vez = vez;
    }

    public Truco getTruco() {
        return truco;
    }

    public void setTruco(Truco truco) {
        this.truco = truco;
    }

    @Override
    public List<Carta> getCartasUsadas() {
        List<Carta> cartasUsadas = new ArrayList<>();
        for (Disputa disputa : disputas) {
            if (disputa != null) {
                cartasUsadas.addAll(disputa.getCartasUsadas());
            }
        }
        return cartasUsadas;
    }

    public Disputa getDisputaAtual() {
        return disputaAtual;
    }

    public void setDisputaAtual(Disputa disputaAtual) {
        this.disputaAtual = disputaAtual;
    }

    public boolean isFull() {
        return disputas[2] != null;
    }

    public boolean isAsCegas() {
        return asCegas;
    }

    public boolean hasEnoughMatchs() {
        return disputas[0] != null && disputas[0].isFull() && disputas[1] != null && disputas[1].isFull();
    }

    public void atualizaDisuputa(Jogada jogada) {
        if (isFull()) {
            throw new IllegalStateException("Rodada já finalizada");
        }
        disputaAtual.addJogada(jogada);
        if (disputaAtual.isFull()) {
            disputas[index++] = disputaAtual;
            disputaAtual = new Disputa();
        }
    }


    public Jogador verificaVencedor() {
        if (!hasEnoughMatchs()) {
            return null;
        }

        Jogador vencedorPrimeira = disputas[0].getVencedor();
        Jogador vencedorSegunda = disputas[1].getVencedor();
        Jogador vencedorTerceira = disputas[2] != null ? disputas[2].getVencedor() : null;

        // If first match is a tie, the winner is determined by the next non-tied match
        if (vencedorPrimeira == null) {
            return resolveFirstMatchTie(vencedorSegunda, vencedorTerceira);
        }

        // If first match has a winner, check if they won another match
        return resolveWithFirstMatchWinner(vencedorPrimeira, vencedorSegunda, vencedorTerceira);
    }

    private Jogador resolveFirstMatchTie(Jogador vencedorSegunda, Jogador vencedorTerceira) {
        return vencedorSegunda != null ? vencedorSegunda : vencedorTerceira;
    }

    private Jogador resolveWithFirstMatchWinner(Jogador vencedorPrimeira, Jogador vencedorSegunda, Jogador vencedorTerceira) {
        if (vencedorSegunda == null || vencedorPrimeira.equals(vencedorSegunda)) {
            return vencedorPrimeira;
        }

        if (isFull() && vencedorTerceira == null || vencedorPrimeira.equals(vencedorTerceira)) {
            return vencedorPrimeira;
        }

        return null;
    }

    public void hideCards() {
        asCegas = true;
    }

    public void showCards() {
        asCegas = false;
    }
}
