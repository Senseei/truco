package ui;

import entities.Jogador;
import exceptions.GameWinnerException;
import exceptions.RoundTieException;
import exceptions.RoundWinnerException;
import game.Disputa;
import game.Mesa;
import game.Rodada;
import game.Truco;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Scanner;

public class TrucoUI {
    private final Scanner sc;
    private final Screen screen;

    public TrucoUI(Scanner sc, Screen screen) {
        this.sc = sc;
        this.screen = screen;
    }

    public Screen getScreenBuilder() {
        return screen;
    }

    // ===== Screen Management Methods =====

    public void clearScreen() {
        System.out.print("\033[H\033[2J\033[3J");
        System.out.flush();
    }

    public void printScreen(String content) {
        clearScreen();
        System.out.println(content);
    }

    public void printScreen(String... content) {
        clearScreen();
        for (String line : content) {
            System.out.println(line);
        }
    }

    public void printScreen() {
        clearScreen();
        checkMaoDeOnze(screen.getMesa().getRodadaAtual().getVez());
        System.out.println(screen);
    }

    // ===== Player Management Methods =====

    public Jogador createJogador() {
        System.out.print("Digite o nome do jogador: ");
        return new Jogador(sc.nextLine());
    }

    public Queue<Jogador> prepararJogadores() {
        Queue<Jogador> jogadores = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            jogadores.add(createJogador());
        }
        return jogadores;
    }

    // ===== Action Management Methods =====

    public String getAction() {
        System.out.print("Escolha uma ação (digite o número): ");
        try {
            int actionIndex = Integer.parseInt(sc.nextLine());
            if (isValidActionIndex(actionIndex)) {
                String action = screen.getActions().get(actionIndex);
                screen.setError(null);
                return action;
            }
        } catch (NumberFormatException e) {
            // Handle non-numeric input
        }

        screen.setError("Ação inválida");
        printScreen();
        return getAction();
    }

    private boolean isValidActionIndex(int actionIndex) {
        return actionIndex >= 0 && actionIndex < screen.getActions().size();
    }

    public List<String> getAvailableActions(Mesa mesa) {
        List<String> actions = new ArrayList<>();
        Truco truco = mesa.getRodadaAtual().getTruco();
        Jogador currentPlayer = mesa.getRodadaAtual().getVez();

        if (isTrucoResponsePending(mesa, truco)) {
            addTrucoResponseActions(actions, truco);
        } else {
            actions.add("Jogar Carta");
            addTrucoRequestActions(actions, truco, currentPlayer);
        }

        actions.add("Fugir");
        actions.add("Sair");
        return actions;
    }

    private boolean isTrucoResponsePending(Mesa mesa, Truco truco) {
        return truco != null &&
                !truco.isAceito() &&
                !mesa.getRodadaAtual().getVez().equals(truco.getQuemPediu());
    }

    private void addTrucoResponseActions(List<String> actions, Truco truco) {
        actions.add("Aceitar");

        if (truco.getPontos() < 12) {
            actions.add(getNextTrucoAction(truco.getPontos()));
        }
    }

    private void addTrucoRequestActions(List<String> actions, Truco truco, Jogador currentPlayer) {
        if (truco == null) {
            actions.add(getNextTrucoAction(0));
        } else if (truco.isAceito() && !currentPlayer.equals(truco.getQuemPediu())) {
            int nextValue = getNextTrucoValue(truco.getPontos());
            if (nextValue <= 12) {
                actions.add(getNextTrucoAction(truco.getPontos()));
            }
        }
    }

    private String getNextTrucoAction(int currentPoints) {
        switch (currentPoints) {
            case 0: return "Truco (3)";
            case 3: return "Seis (6)";
            case 6: return "Nove (9)";
            case 9: return "Doze (12)";
            default: return "Truco";
        }
    }

    private int getNextTrucoValue(int currentPoints) {
        switch (currentPoints) {
            case 0: return 3;
            case 3: return 6;
            case 6: return 9;
            case 9: return 12;
            default: return 3;
        }
    }

    public void handleAction(String action, Mesa mesa) throws RoundTieException, RoundWinnerException, GameWinnerException {
        try {
            if (action.equals("Jogar Carta")) {
                playCard(mesa);
            } else if (action.equals("Aceitar")) {
                handleTrucoAcceptance(mesa);
            } else if (action.equals("Fugir")) {
                mesa.fugir();
                updateScreen(mesa);
            } else if (isTrucoAction(action)) {
                handleTrucoRequest(action, mesa);
            } else if (action.equals("Sair")) {
                System.exit(0);
            } else {
                screen.setError("Ação desconhecida");
            }
        } catch (RuntimeException e) {
            screen.setError("Erro: " + e.getMessage());
        }
    }

    private boolean isTrucoAction(String action) {
        return action.contains("Truco") || action.contains("Seis") ||
                action.contains("Nove") || action.contains("Doze");
    }

    private void handleTrucoAcceptance(Mesa mesa) {
        mesa.aceitarTruco();
        Jogador quemPediu = mesa.getRodadaAtual().getTruco().getQuemPediu();
        if (!mesa.getRodadaAtual().getVez().equals(quemPediu)) {
            mesa.passarVezPara(quemPediu);
        }
        updateScreen(mesa);
    }

    private void handleTrucoRequest(String action, Mesa mesa) throws GameWinnerException {
        int points = extractPoints(action);
        mesa.truco(points);
        mesa.proximoJogador();
        updateScreen(mesa);
    }

    private int extractPoints(String action) {
        if (action.contains("3")) return 3;
        if (action.contains("6")) return 6;
        if (action.contains("9")) return 9;
        if (action.contains("12")) return 12;
        return 3; // Default to truco
    }

    private void playCard(Mesa mesa) throws RoundTieException, RoundWinnerException, GameWinnerException {
        System.out.print("Digite o número da carta para jogar: ");
        try {
            int index = Integer.parseInt(sc.nextLine());
            mesa.jogada(index);
            updateScreen(mesa);
        } catch (NumberFormatException e) {
            screen.setError("Índice inválido");
        } catch (RuntimeException e) {
            screen.setError(e.getMessage());
        }
    }

    // ===== Game State Display Methods =====

    public void updateScreen(Mesa mesa) {
        screen.setMesa(mesa);
        screen.setMaoDoJogador(mesa.getRodadaAtual().getVez().getMao());
        screen.setActions(getAvailableActions(mesa));
    }

    public void showRoundTie() {
        screen.setActions(new ArrayList<>());
        screen.setFooter(buildRoundEndFooter("EMPATE!"));
    }

    public void showRoundWinner(Jogador winner) {
        screen.setActions(new ArrayList<>());
        screen.setFooter(buildRoundEndFooter("VENCEDOR: " + winner.getName()));
    }

    private String buildRoundEndFooter(String resultMessage) {
        StringBuilder footer = new StringBuilder("═════════════════════════════════════\n");
        footer.append("         🎮 RODADA FINALIZADA!\n");
        footer.append("         RESULTADO: ").append(resultMessage).append("\n");

        appendDisputeDetails(footer);
        footer.append("═════════════════════════════════════");

        return footer.toString();
    }

    private void appendDisputeDetails(StringBuilder footer) {
        Rodada ultimaRodada = screen.getMesa().getUltimaRodada();
        int index = 1;
        for (Disputa disputa : ultimaRodada.getDisputas()) {
            if (disputa != null) {
                footer.append("         Disputa ").append(index).append(": ")
                        .append(disputa).append("\n");
                index++;
            }
        }
    }

    public void showGameWinner(Jogador winner) {
        screen.setActions(new ArrayList<>());

        StringBuilder footer = new StringBuilder("═════════════════════════════════════\n");
        footer.append("🏆 FIM DE JOGO!\n");
        footer.append("VENCEDOR: ").append(winner.getName()).append("\n");
        footer.append("Pontuação final: ").append(winner.getPontos()).append(" pontos\n");
        footer.append("═════════════════════════════════════");

        screen.setFooter(footer.toString());
    }

    public void waitForContinue() {
        System.out.println("\nPressione ENTER para continuar...");
        sc.nextLine();
        screen.setFooter(null);
    }

    public void checkMaoDeOnze(Jogador currentPlayer) {
        if (currentPlayer.getPontos() == 11) {
            StringBuilder footer = new StringBuilder("═════════════════════════════════════\n");
            footer.append("🎮 MÃO DE ONZE!\n");
            footer.append("Você está com 11 pontos. Decida se deseja continuar jogando ou fugir.\n");
            footer.append("═════════════════════════════════════\n");

            screen.setFooter(footer.toString());
        }
    }
}