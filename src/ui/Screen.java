package ui;

import entities.Carta;
import game.Mesa;
import game.Truco;

import java.util.List;

public class Screen {
    private Mesa mesa;
    private List<Carta> maoDoJogador;
    private List<String> actions;
    private String footer;
    private String error;

    public Screen(Mesa mesa, List<Carta> maoDoJogador, List<String> actions) {
        this.mesa = mesa;
        this.maoDoJogador = maoDoJogador;
        this.actions = actions;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public List<Carta> getMaoDoJogador() {
        return maoDoJogador;
    }

    public void setMaoDoJogador(List<Carta> maoDoJogador) {
        this.maoDoJogador = maoDoJogador;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        StringBuilder screen = new StringBuilder();
        screen.append(mesa.toString());
        screen.append("\n");

        // Display player's cards in a more readable format
        screen.append("Suas cartas: \n");
        for (int i = 0; i < maoDoJogador.size(); i++) {
            screen.append(String.format("[%d] %s   ", i, maoDoJogador.get(i)));
        }
        screen.append("\n\n");

        // Display truco information if active
        Truco truco = mesa.getRodadaAtual().getTruco();
        if (truco != null) {
            screen.append("═════════════════════════════════════════\n");
            screen.append(String.format("TRUCO PEDIDO POR: %s\n", truco.getQuemPediu().getName()));
            screen.append(String.format("VALOR: %d pontos\n", truco.getPontos()));

            if (!truco.isAceito() && !mesa.getRodadaAtual().getVez().equals(truco.getQuemPediu())) {
                screen.append("AGUARDANDO RESPOSTA!\n");
            } else if (truco.isAceito()) {
                screen.append("TRUCO ACEITO!\n");
            }
            screen.append("═════════════════════════════════════════\n\n");
        }

        // Display error messages
        if (error != null) {
            screen.append("⚠ ").append(error).append(" ⚠\n\n");
        }

        // Display footer if present
        if (footer != null) {
            screen.append(footer);
        }

        // Display available actions
        if (!actions.isEmpty()) {
            screen.append("Ações disponíveis:\n");
            for (int i = 0; i < actions.size(); i++) {
                screen.append(String.format("[%d] %s   ", i, actions.get(i)));
            }
        }

        return screen.toString();
    }
}