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

    private Scanner sc;
    private Screen screen;

    public TrucoUI(Scanner sc, Screen screen) {
        this.sc = sc;
        this.screen = screen;
    }

    public Screen getScreenBuilder() {
        return screen;
    }

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
        System.out.println(screen);
    }

    public Jogador createJogador() {
        System.out.print("Digite o nome do jogador: ");
        String nome = sc.nextLine();
        return new Jogador(nome);
    }

    public Queue<Jogador> prepararJogadores() {
        Queue<Jogador> jogadores = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            jogadores.add(createJogador());
        }
        return jogadores;
    }

    public String getAction() {
        System.out.print("Escolha uma ação (digite o número): ");
        try {
            int actionIndex = Integer.parseInt(sc.nextLine());
            if (actionIndex >= 0 && actionIndex < screen.getActions().size()) {
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

    public List<String> getAvailableActions(Mesa mesa) {
        List<String> actions = new ArrayList<>();
        Truco truco = mesa.getRodadaAtual().getTruco();

        if (truco != null && !truco.isAceito() &&
                !mesa.getRodadaAtual().getVez().equals(truco.getQuemPediu())) {
            actions.add("Aceitar");
            actions.add("Fugir");

            if (truco.getPontos() < 12) {
                actions.add(getNextTrucoAction(truco.getPontos()));
            }
        } else {
            actions.add("Jogar Carta");

            if (truco == null || truco.isAceito()) {
                int nextValue = truco == null ? 3 : getNextTrucoValue(truco.getPontos());
                if (nextValue <= 12) {
                    actions.add(getNextTrucoAction(truco == null ? 0 : truco.getPontos()));
                }
            }
        }

        actions.add("Sair");
        return actions;
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

    public void handleAction(String action, Mesa mesa) {
        try {
            if (action.equals("Jogar Carta")) {
                playCard(mesa);
            } else if (action.equals("Aceitar")) {
                mesa.aceitarTruco();
                Jogador quemPediu = mesa.getRodadaAtual().getTruco().getQuemPediu();
                if (!mesa.getRodadaAtual().getVez().equals(quemPediu)) {
                    mesa.passarVezPara(quemPediu);
                }
                updateScreen(mesa);
            } else if (action.equals("Fugir")) {
                mesa.fugir();
                updateScreen(mesa);
            } else if (action.contains("Truco") || action.contains("Seis") ||
                    action.contains("Nove") || action.contains("Doze")) {
                int points = extractPoints(action);
                Jogador quemPediu = mesa.getRodadaAtual().getVez();
                mesa.truco(points);
                mesa.proximoJogador();
                updateScreen(mesa);
            } else if (action.equals("Sair")) {
                System.exit(0);
            } else {
                screen.setError("Ação desconhecida");
            }
        } catch (RoundWinnerException | GameWinnerException | RoundTieException e) {
            throw e;
        } catch (Exception e) {
            screen.setError("Erro: " + e.getMessage());
        }
    }

    private int extractPoints(String action) {
        if (action.contains("3")) return 3;
        if (action.contains("6")) return 6;
        if (action.contains("9")) return 9;
        if (action.contains("12")) return 12;
        return 3; // Default to truco
    }

    private void playCard(Mesa mesa) {
        System.out.print("Digite o número da carta para jogar: ");
        try {
            int index = Integer.parseInt(sc.nextLine());
            mesa.jogada(index);
            updateScreen(mesa);
        } catch (RoundWinnerException | GameWinnerException | RoundTieException e) {
            throw e;
        } catch (NumberFormatException e) {
            screen.setError("Índice inválido");
        } catch (Exception e) {
            screen.setError(e.getMessage());
        }
    }

    public void updateScreen(Mesa mesa) {
        screen.setMesa(mesa);
        screen.setMaoDoJogador(mesa.getRodadaAtual().getVez().getMao());
        screen.setActions(getAvailableActions(mesa));
    }

    public void showRoundTie() {
        screen.setActions(new ArrayList<>());

        StringBuilder footer = new StringBuilder("═════════════════════════════════════\n");
        footer.append("         🎮 RODADA FINALIZADA!\n");
        footer.append("         RESULTADO: EMPATE!\n");

        Rodada ultimaRodada = screen.getMesa().getUltimaRodada();
        int index = 1;
        for (Disputa disputa : ultimaRodada.getDisputas()) {
            if (disputa != null) {
                footer.append("         Disputa ").append(index).append(": ").append(disputa).append("\n");
                index++;
            }
        }
        footer.append("═════════════════════════════════════");
        screen.setFooter(footer.toString());
    }

    public void showRoundWinner(Jogador winner) {
        screen.setActions(new ArrayList<>());


        StringBuilder footer = new StringBuilder("═════════════════════════════════════\n");
        footer.append("         🎮 RODADA FINALIZADA!\n");
        footer.append("         VENCEDOR: ").append(winner.getName()).append("\n");
        Rodada ultimaRodada = screen.getMesa().getUltimaRodada();
        int index = 1;
        for (Disputa disputa : ultimaRodada.getDisputas()) {
            if (disputa != null) {
                footer.append("         Disputa ").append(index).append(": ").append(disputa).append("\n");
                index++;
            }
        }
        footer.append("═════════════════════════════════════");
        screen.setFooter(footer.toString());
    }

    public void showGameWinner(Jogador winner) {
        screen.setActions(new ArrayList<>());

        String footer = "═════════════════════════════════════\n";
        footer += "🏆 FIM DE JOGO!\n";
        footer += "VENCEDOR: " + winner.getName() + "\n";
        footer += "Pontuação final: " + winner.getPontos() + " pontos\n";
        footer += "═════════════════════════════════════";
        screen.setFooter(footer);
    }

    public void waitForContinue() {
        System.out.println("\nPressione ENTER para continuar...");
        sc.nextLine();
        screen.setFooter(null);
    }
}