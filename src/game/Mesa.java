package game;

import entities.Baralho;
import entities.Carta;
import entities.Dealer;
import entities.Jogador;
import exceptions.GameWinnerException;
import exceptions.RoundTieException;
import exceptions.RoundWinnerException;

import java.util.*;

public class Mesa {
    private final Queue<Jogador> filaParaJogarPrimeiro;
    private final Queue<Jogador> jogadores;
    private final Dealer dealer;
    private final Baralho baralho;
    private Carta vira;
    private List<Rodada> rodadas = new ArrayList<>();
    private Rodada rodadaAtual;

    public Mesa(Queue<Jogador> jogadores, Dealer dealer) {
        this.jogadores = jogadores;
        this.filaParaJogarPrimeiro = new LinkedList<>(jogadores);
        this.dealer = dealer;
        this.baralho = Baralho.prepararBaralho(true);
        rodadaAtual = new Rodada(filaParaJogarPrimeiro.peek());
    }

    public Mesa(Queue<Jogador> jogadores, Dealer dealer, Baralho baralho) {
        this.jogadores = jogadores;
        this.filaParaJogarPrimeiro = new LinkedList<>(jogadores);
        this.dealer = dealer;
        this.baralho = baralho;
        rodadaAtual = new Rodada(filaParaJogarPrimeiro.peek());
    }

    public static Mesa prepararMesa(Queue<Jogador> jogadores, Dealer dealer) {
        Mesa mesa = new Mesa(jogadores, dealer);
        mesa.dealer.distribuirCartas(jogadores, mesa.baralho);
        mesa.virarCarta();
        return mesa;
    }

    public Rodada getRodadaAtual() {
        return rodadaAtual;
    }

    public Rodada getUltimaRodada() {
        return rodadas.get(rodadas.size() - 1);
    }

    public void virarCarta() {
        vira = baralho.comprarCarta();
        baralho.definirManilhas(vira);
    }

    public void jogada(int index) {
        if (index < 0 || index >= rodadaAtual.getVez().getMao().size()) {
            throw new IllegalArgumentException("Carta inválida");
        }
        rodadaAtual.atualizaDisuputa(rodadaAtual.getVez().jogarCarta(index));

        Jogador vencedorRodada = rodadaAtual.verificaVencedor();
        if (vencedorRodada != null) {
            pontuarJogador(vencedorRodada);
            throw new RoundWinnerException(vencedorRodada);
        } else if (rodadaAtual.isFull()) {
            finalizarRodada();
            throw new RoundTieException("Rodada empatada");
        }

        proximoJogador();
    }

    public void proximoJogador() {
        jogadores.add(jogadores.poll());
        rodadaAtual.setVez(jogadores.peek());
    }

    public void passarVezPara(Jogador jogador) {
        while (!jogadores.peek().equals(jogador)) {
            jogadores.add(jogadores.poll());
        }
        rodadaAtual.setVez(jogadores.peek());
    }

    private void pontuarJogador(Jogador jogador) {
        Truco truco = rodadaAtual.getTruco();

        if (truco == null) {
            jogador.pontuar(1);
        } else {
            jogador.pontuar(truco.isAceito() ? truco.getPontos() : truco.getPontosAnteriores());
        }

        if (jogador.getPontos() >= 12) {
            throw new GameWinnerException(jogador);
        }

        finalizarRodada();
    }

    private void finalizarRodada() {
        rodadas.add(rodadaAtual);
        filaParaJogarPrimeiro.add(filaParaJogarPrimeiro.poll());
        baralho.addCard(vira);
        dealer.juntarBaralho(new ArrayList<>(jogadores), rodadaAtual.getCartasUsadas(), baralho);

        rodadaAtual = new Rodada(jogadores.peek());

        dealer.distribuirCartas(jogadores, baralho);
        virarCarta();
    }

    public void aceitarTruco() {
        rodadaAtual.getTruco().aceitar();
    }

    public void fugir() {
        pontuarJogador(rodadaAtual.getTruco().getQuemPediu());
    }

    public void truco(int pontos) {
        Truco truco = rodadaAtual.getTruco();
        rodadaAtual.setTruco(truco == null ? new Truco(rodadaAtual.getVez()) : new Truco(pontos, truco.getPontos(), rodadaAtual.getVez()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Header with game information
        sb.append("╔════════════════════════════════════════════════╗\n");

        // Display all players and their scores
        sb.append("║ PLAYERS:                                       ║\n");
        for (Jogador jogador : jogadores) {
            sb.append(String.format("║ %s: %d points%s\n",
                    jogador.getName(),
                    jogador.getPontos(),
                    jogador.equals(rodadaAtual.getVez()) ? " (current turn) " : ""));
        }

        // Display vira card
        sb.append("║ Vira: ").append(vira == null ? "Not turned yet" : vira).append("\n");

        // Display current dispute/table
        sb.append("║                                                ║\n");
        sb.append("║ CARDS ON TABLE:                                ║\n");

        if (rodadaAtual != null && rodadaAtual.getDisputas() != null) {
            int disputeNum = 1;
            for (Disputa disputa : rodadaAtual.getDisputas()) {
                if (disputa != null) {
                    sb.append(String.format("║ Dispute %d: %s\n", disputeNum++, disputa));
                }
            }
            sb.append(String.format("║ Dispute %d: %s\n", disputeNum, rodadaAtual.getDisputaAtual()));
        }

        sb.append("╚════════════════════════════════════════════════╝");

        return sb.toString();
    }
}
