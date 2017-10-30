package districts;

import java.util.Set;
import java.util.StringJoiner;

class DistrictEntry {

    public DistrictEntry(District district, String street, EntryMode mode, Set<Integer> numbers) {
        this.district = district;
        this.mode = mode;
        this.numbers = numbers;
        this.street = street;
    }

    public District getDistrict() {
        return district;
    }

    public String getStreet() { return street; }

    public boolean containsNumber(int houseNumber) {
        return mode == EntryMode.ALL_NUMBERS || numbers.contains(houseNumber); // take care: short-circuiting 
    }

    public EntryMode getMode() {
        return mode;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        joiner.add("district: " + district);
        joiner.add("mode: " + mode);
        if(mode == EntryMode.ALL_NUMBERS)
            return joiner.toString();
        
        StringJoiner numbersJoiner = new StringJoiner(",", "{", "}");
        for(Integer number : numbers)
            numbersJoiner.add(number.toString());
        joiner.add("numbers: " + numbersJoiner.toString());
        return joiner.toString();
    }

    private District district;
    private EntryMode mode;
    private Set<Integer> numbers;
    private String street;
    
    /* Flag indicating if districts.DistrictEntry concerns all local numbers on given street
          or only specific set of local numbers on that street: */
    enum EntryMode { ALL_NUMBERS, SPECIFIC_NUMBERS }
}
