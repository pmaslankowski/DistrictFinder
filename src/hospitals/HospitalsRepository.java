package hospitals;

import hospitals.exceptions.HospitalNotFoundException;

import java.util.Map;
import java.util.StringJoiner;

public class HospitalsRepository {
    public HospitalsRepository(HospitalsRepositoryLoader loader) {
        repository = loader.getRepository();
    }

    public Hospital get(String id) throws HospitalNotFoundException {
        Hospital res = repository.get(id);
        if(res == null)
            throw new HospitalNotFoundException("Hospital " + id + " not found in repository.");
        return res;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",\n", "[", "]");
        for(String key: repository.keySet())
            joiner.add(key + " : " + repository.get(key).toString());
        return joiner.toString();
    }

    private Map<String, Hospital> repository;
}
