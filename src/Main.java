import entities.Dealer;
import entities.Jogador;
import exceptions.GameWinnerException;
import exceptions.RoundTieException;
import exceptions.RoundWinnerException;
import game.Mesa;
import ui.Screen;
import ui.TrucoUI;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            TrucoUI ui = new TrucoUI(sc);

            Queue<Jogador> jogadores = ui.prepararJogadores();
            Mesa mesa = Mesa.prepararMesa(jogadores, new Dealer());

            Screen screen = new Screen(mesa, mesa.getRodadaAtual().getVez().getMao());
            ui.setScreen(screen);

            // Initial screen update with proper actions
            ui.updateScreen(mesa);

            try {
                while (true) {
                    try {
                        ui.printScreen();
                        String action = ui.getAction();
                        ui.handleAction(action, mesa);
                    } catch (RoundWinnerException e) {
                        ui.showRoundWinner(e.getWinner());
                        ui.printScreen();
                        ui.waitForContinue();
                        ui.updateScreen(mesa);
                    } catch (RoundTieException e) {
                        ui.showRoundTie();
                        ui.printScreen();
                        ui.waitForContinue();
                        ui.updateScreen(mesa);
                    }
                }
            } catch (GameWinnerException e) {
                ui.showGameWinner(e.getWinner());
                ui.printScreen();
            }
        }
    }
}