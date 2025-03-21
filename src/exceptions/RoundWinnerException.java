package exceptions;

import entities.Jogador;

public class RoundWinnerException extends WinnerException {

    public RoundWinnerException(Jogador jogador, String message) {
        super(jogador, message);
    }

    public RoundWinnerException(Jogador jogador) {
        super(jogador, "Rodada finalizada, vencedor: " + jogador.getName());
    }
}
