package exceptions;

import entities.Jogador;

public class GameWinnerException extends WinnerException {

    public GameWinnerException(Jogador winner, String message) {
        super(winner, message);
    }

    public GameWinnerException(Jogador winner) {
        super(winner, "Jogo finalizado, vencedor: " + winner.getName());
    }
}
