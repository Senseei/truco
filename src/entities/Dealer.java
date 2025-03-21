package entities;

import java.util.List;
import java.util.Queue;

public class Dealer {

    public void distribuirCartas(Queue<Jogador> jogadores, Baralho baralho) {
        for (Jogador jogador: jogadores) {
            for (int i = 0; i < 3; i++) {
                jogador.pegarCarta(baralho.comprarCarta());
            }
        }
    }

    public void juntarBaralho(List<Jogador> jogadores, List<Carta> cartasUsadas, Baralho baralho) {
        for (Jogador jogador: jogadores) {
            cartasUsadas.addAll(jogador.getMao());
            jogador.getMao().clear();
        }
        baralho.addCards(cartasUsadas);
        embaralharBaralho(baralho);
    }

    public void embaralharBaralho(Baralho baralho) {
        baralho.embaralhar();
    }
}
