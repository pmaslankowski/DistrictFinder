package districts;

import districts.exceptions.StreetNotFoundException;

import java.util.*;

public class DistrictsRepository {

    public DistrictsRepository(DistrictsRepositoryLoader loader) {
        this.repository = loader.getRepository();
    }
    
    public District get(String street, int number) throws StreetNotFoundException {
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
}
