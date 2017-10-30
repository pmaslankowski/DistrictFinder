package districts.exceptions;

public class StreetNotFoundException extends Exception {
    public StreetNotFoundException(String msg, String street, int number) {
        super(msg);
        this.street = street;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String getStreet() {
        return street;
    }

    private int number;
    private String street;
}
