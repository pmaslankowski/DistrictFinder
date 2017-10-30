package districts.exceptions;

public class StreetNotFoundException extends Exception {
    public StreetNotFoundException(String msg, String street, int number, boolean tryFuzzyMatching) {
        super(msg);
        this.street = street;
        this.number = number;
        this.tryFuzzyMatching = tryFuzzyMatching;
    }

    public int getNumber() {
        return number;
    }

    public String getStreet() {
        return street;
    }

    public boolean fuzzyMatching() { return tryFuzzyMatching; }
    private int number;
    private String street;
    boolean tryFuzzyMatching;
}
