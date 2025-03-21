package entities;

import enums.Naipe;

import java.util.*;

public class Baralho {
    private List<Carta> referencias;
    private Stack<Carta> cartas;
    private static String[] labels = new String[] {"7", "Q", "J", "K", "A", "2", "3"};

    private Baralho(Stack<Carta> cartas) {
        this.cartas = cartas;
        this.referencias = new ArrayList<>(cartas);
    }

    public static Baralho prepararBaralho(boolean embaralhado) {
        Stack<Carta> cartas = new Stack<>();
        List<Naipe> naipes = List.of(Naipe.values());

        for (Naipe naipe: naipes) {
            for (int i = 1; i <= labels.length; i++) {
                cartas.add(new Carta(naipe, new Figura(i, labels[i - 1])));
            }
        }

        if (embaralhado) {
            Collections.shuffle(cartas);
        }

        return new Baralho(cartas);
    }

    public void definirManilhas(Carta carta) {
        referencias.forEach(c -> c.setManilha(false));
        List<Carta> manilhas = referencias.stream().filter(c -> c.getFigura().getPower().equals((carta.getFigura().getPower() + 1) % labels.length)).toList();
        for (Carta manilha: manilhas) {
            manilha.setManilha(true);
        }
    }

    public void embaralhar() {
        Collections.shuffle(cartas);
    }

    public Carta comprarCarta() {
        return cartas.pop();
    }

    public void addCards(List<Carta> cartas) {
        this.cartas.addAll(cartas);
    }

    public void addCard(Carta carta) {
        this.cartas.add(carta);
    }

    @Override
    public String toString() {
        return cartas.toString();
    }
}
