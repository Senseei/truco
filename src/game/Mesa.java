package game;

import entities.Baralho;
import entities.Carta;
import entities.Dealer;
import entities.Jogador;
import exceptions.GameWinnerException;
import exceptions.RoundTieException;
import exceptions.RoundWinnerException;

import java.util.*;
import java.util.function.Function;

public class Mesa {
    private final Queue<Jogador> filaParaJogarPrimeiro;
    private final Queue<Jogador> jogadores;
    private final Dealer dealer;
    private final Baralho baralho;
    private Carta vira;
    private final List<Rodada> rodadas = new ArrayList<>();
    private Rodada rodadaAtual;

    private static final int PONTOS_FUGA_MAO_ONZE = 1;
    private static final int PONTOS_VITORIA_MAO_ONZE = 3;
    private static final int PONTOS_VITORIA_JOGO = 12;

    public Mesa(Queue<Jogador> jogadores, Dealer dealer) {
        this(jogadores, dealer, Baralho.prepararBaralho(true));
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

    public void jogada(int index) throws GameWinnerException, RoundWinnerException, RoundTieException {
        validarIndiceCartaJogada(index);
        jogarCarta(index);
        processarResultadoJogada();
    }

    private void validarIndiceCartaJogada(int index) {
        if (index < 0 || index >= rodadaAtual.getVez().getMao().size()) {
            throw new IllegalArgumentException("Carta inválida");
        }
    }

    private void jogarCarta(int index) {
        rodadaAtual.atualizaDisuputa(rodadaAtual.getVez().jogarCarta(index));
    }

    private void processarResultadoJogada() throws GameWinnerException, RoundWinnerException, RoundTieException {
        Jogador vencedorRodada = rodadaAtual.verificaVencedor();

        if (vencedorRodada != null) {
            pontuarVencedor(vencedorRodada);
            throw new RoundWinnerException(vencedorRodada);
        }

        if (rodadaAtual.isFull()) {
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
        while (!jogador.equals(jogadores.peek())) {
            jogadores.add(jogadores.poll());
        }
        rodadaAtual.setVez(jogadores.peek());
    }

    private void pontuarVencedor(Jogador vencedor) throws GameWinnerException {
        Function<Jogador, Integer> estrategiaPontuacao = isMaoDeOnze() ?
                jogador -> PONTOS_VITORIA_MAO_ONZE :
                this::calcularPontosPadrao;

        int pontos = estrategiaPontuacao.apply(vencedor);
        vencedor.pontuar(pontos);
        verificarVencedorJogo(vencedor);
        finalizarRodada();
    }

    private int calcularPontosPadrao(Jogador jogador) {
        Truco truco = rodadaAtual.getTruco();
        return truco == null ? 1 :
                truco.isAceito() ? truco.getPontos() :
                        truco.getPontosAnteriores();
    }

    private void verificarVencedorJogo(Jogador jogador) throws GameWinnerException {
        if (jogador.getPontos() >= PONTOS_VITORIA_JOGO) {
            throw new GameWinnerException(jogador);
        }
    }

    private void finalizarRodada() {
        rodadas.add(rodadaAtual);
        filaParaJogarPrimeiro.add(filaParaJogarPrimeiro.poll());
        baralho.addCard(vira);
        dealer.juntarBaralho(new ArrayList<>(jogadores), rodadaAtual.getCartasUsadas(), baralho);

        prepararNovaRodada();
    }

    private void prepararNovaRodada() {
        rodadaAtual = new Rodada(jogadores.peek());

        if (isFinalRound()) {
            rodadaAtual.hideCards();
        }

        dealer.distribuirCartas(jogadores, baralho);
        virarCarta();
    }

    public void aceitarTruco() {
        rodadaAtual.getTruco().aceitar();
    }

    public void fugir() throws GameWinnerException {
        if (isMaoDeOnze()) {
            handleMaoDeOnzeFuga();
            return;
        }
        handleFugaNormal();
    }

    private void handleMaoDeOnzeFuga() throws GameWinnerException {
        Jogador vencedor = getProximoJogador();
        vencedor.pontuar(PONTOS_FUGA_MAO_ONZE);
        verificarVencedorJogo(vencedor);
        finalizarRodada();
    }

    private void handleFugaNormal() throws GameWinnerException {
        Truco truco = rodadaAtual.getTruco();
        Jogador vencedor = truco != null ? truco.getQuemPediu() : getProximoJogador();
        pontuarVencedor(vencedor);
    }

    private Jogador getProximoJogador() {
        proximoJogador();
        return rodadaAtual.getVez();
    }

    public void truco(int pontos) throws GameWinnerException {
        if (isMaoDeOnze()) {
            handleMaoDeOnzeTruco();
            return;
        }
        handleTrucoNormal(pontos);
    }

    private void handleMaoDeOnzeTruco() throws GameWinnerException {
        pontuarVencedor(getProximoJogador());
    }

    private void handleTrucoNormal(int pontos) {
        Truco truco = rodadaAtual.getTruco();
        boolean isNovoPedido = truco == null;

        rodadaAtual.setTruco(isNovoPedido ?
                new Truco(rodadaAtual.getVez()) :
                new Truco(pontos, truco.getPontos(), rodadaAtual.getVez()));
    }

    public boolean isMaoDeOnze() {
        return jogadores.stream().anyMatch(jogador -> jogador.getPontos() == 11);
    }

    public boolean isFinalRound() {
        return jogadores.stream().allMatch(jogador -> jogador.getPontos() == 11);
    }

    // toString methods remain unchanged
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        appendHeader(sb);
        appendPlayers(sb);
        appendVira(sb);
        appendDisputas(sb);
        appendFooter(sb);

        return sb.toString();
    }

    private void appendHeader(StringBuilder sb) {
        sb.append("╔════════════════════════════════════════════════╗\n");
    }

    private void appendPlayers(StringBuilder sb) {
        sb.append("║ PLAYERS:                                       ║\n");
        for (Jogador jogador : jogadores) {
            String currentTurn = jogador.equals(rodadaAtual.getVez()) ? " (current turn) " : "";
            sb.append(String.format("║ %s: %d points%s\n",
                    jogador.getName(),
                    jogador.getPontos(),
                    currentTurn));
        }
    }

    private void appendVira(StringBuilder sb) {
        String viraText = vira == null ? "Not turned yet" : vira.toString();
        sb.append("║ Vira: ").append(viraText).append("\n");
    }

    private void appendDisputas(StringBuilder sb) {
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
    }

    private void appendFooter(StringBuilder sb) {
        sb.append("╚════════════════════════════════════════════════╝");
    }
}