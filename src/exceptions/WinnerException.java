package exceptions;

import entities.Jogador;

public class WinnerException extends RuntimeException {
    private final Jogador winner;

    public WinnerException(Jogador winner) {
        this.winner = winner;
    }

    public WinnerException(Jogador winner, String message) {
        super(message);
        this.winner = winner;
    }

    public Jogador getWinner() {
        return winner;
    }
}
