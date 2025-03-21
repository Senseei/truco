package enums;

public enum Naipe {
    COPAS(3, "\u2661"),
    ESPADAS(2, "\u2660"),
    PICAFUMO(1, "\u2662"),
    ZAP(4, "\u2663");

    private Integer power;
    private String label;

    Naipe(Integer power, String label) {
        this.power = power;
        this.label = label;
    }

    public Integer getPower() {
        return power;
    }

    public String getLabel() {
        return label;
    }
}
