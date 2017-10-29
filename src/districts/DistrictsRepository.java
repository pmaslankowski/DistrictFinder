package districts;

import districts.exceptions.InvalidAddressFormatException;
import districts.exceptions.StreetNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DistrictsRepository {

    public DistrictsRepository(DistrictsRepositoryLoader loader) {
        parsePattern = Pattern.compile("(?<street>([^\\W\\d]|-|\\s|\\.)+)(?<number>(\\d+))",
                Pattern.UNICODE_CHARACTER_CLASS);
        this.repository = loader.getRepository();
    }

    public District get(String address) throws StreetNotFoundException, InvalidAddressFormatException {
        Matcher matcher = parsePattern.matcher(address.toLowerCase());
        if(matcher.matches()) {
            String street = matcher.group("street").trim();
            int number = Integer.parseInt(matcher.group("number"));
            return get(street, number);
        } else {
            throw new InvalidAddressFormatException("Invalid adress format: " + address);
        }
    }

    private District get(String street, int number) throws StreetNotFoundException {
        List<DistrictEntry> candidatingDistricts = repository.get(street);
        if (candidatingDistricts == null)
            throw new StreetNotFoundException("Street: " + street + " not found in districts repository.");
        for (DistrictEntry districtEntry : candidatingDistricts)
            if (districtEntry.containsNumber(number))
                return districtEntry.getDistrict();
        throw new StreetNotFoundException(String.format("Number: %d is not assigned to street: %s", number, street));
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",\n", "[", "]");
        for(String key: repository.keySet())
            joiner.add(key + " : " + repository.get(key).toString());
        return joiner.toString();
    }
    
    private Map<String, List<DistrictEntry>> repository;
    private Pattern parsePattern;
}
