import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DistrictsRepository {
    public void load(String pathToDirectory) {
        
    }

    public District get(String street, int number) {
        return null;
    }

    private void loadDistrict(InputStream districtStream, DocumentBuilder builder) throws DistrictLoadingException {
        try {
            Document document = builder.parse(districtStream);
            District district = parseDistrict(document);
            NodeList streetNodes = document.getElementsByTagName("street");
            for(int i = 0; i < streetNodes.getLength(); i++)
                loadStreet((Element) streetNodes.item(i), district);
        } catch (SAXException e) {
            throw new DistrictLoadingException("SAXException: " + e.getMessage());
        } catch (IOException e) {
            throw new DistrictLoadingException("IOException: " + e.getMessage());
        }
    }

    private District parseDistrict(Document document) {
        Element districtEl = (Element) document.getElementsByTagName("district").item(0);
        String districtName = districtEl.getAttribute("name");
        String districtLabel = districtEl.getAttribute("label");
        return new District(districtName, districtLabel);
    }

    private void loadStreet(Element streetEl, District district) throws DistrictLoadingException {
        String street = streetEl.getTextContent();
        String evenStartStr = streetEl.getAttribute("even-start");
        String evenEndStr = streetEl.getAttribute("even-end");
        String oddStartStr = streetEl.getAttribute("odd-start");
        String oddEndStr = streetEl.getAttribute("odd-end");
        String additionalStr = streetEl.getAttribute("additional");
        int evenStart = evenStartStr == null ? -1 : Integer.parseInt(evenStartStr);
        int evenEnd = evenEndStr == null ? -1 : Integer.parseInt(evenEndStr);
        int oddStart = oddStartStr == null ? -1 : Integer.parseInt(oddStartStr);
        int oddEnd = oddEndStr == null ? -1 : Integer.parseInt(oddEndStr);
        List<String> additional_splited;
        if(additionalStr == null)
            additional_splited = new LinkedList<>();
        else
            additional_splited = Arrays.asList(additionalStr.split(","));
        List<Integer> additional = additional_splited.stream()
                .map(number -> Integer.parseInt(number))
                .collect(Collectors.toList());
        
        if(evenStart == -1 && evenEnd == -1 && oddStart == -1 && oddEnd == -1 && additional.isEmpty())
            addToRepository(street, new DistrictEntry(district, EntryMode.ALL_NUMBERS, null));
        else
            addToRepository(street, new DistrictEntry(district, EntryMode.SPECIFIC_NUMBERS,
                        createNumbersSet(evenStart, evenEnd, oddStart, oddEnd, additional)));
    }

    private void addToRepository(String street, DistrictEntry entry) {
        List<DistrictEntry> currentEntry = repository.getOrDefault(street, new LinkedList<DistrictEntry>());
        currentEntry.add(entry);
        repository.put(street, currentEntry);
    }
    
    private Set<Integer> createNumbersSet(int evenStart, int evenEnd, int oddStart, int oddEnd, List<Integer> additional)
        throws DistrictLoadingException {
        if((evenStart != -1 && evenEnd == -1) || (evenStart == -1 && evenEnd != -1))
            throw new DistrictLoadingException("Wrong configuration of even range");
        if((oddStart != -1 && oddEnd == -1) || (oddStart == -1 && oddEnd != -1))
            throw new DistrictLoadingException("Wrong configuration of odd range.");

        List<Integer> evenNumbers = new LinkedList<>();
        List<Integer> oddNumbers = new LinkedList<>();
        Set<Integer> result = new HashSet<Integer>();
        if(evenStart != -1 && evenEnd != -1)
            evenNumbers = IntStream.rangeClosed(evenStart, evenEnd)
                    .filter(n -> n % 2 == 0)
                    .boxed().collect(Collectors.toList());
        if(oddStart != -1 && oddEnd != -1)
            oddNumbers = IntStream.rangeClosed(oddStart, oddEnd)
                    .filter(n -> n % 2 == 1)
                    .boxed().collect(Collectors.toList());

        result.addAll(evenNumbers);
        result.addAll(oddNumbers);
        result.addAll(additional);
        return result;
    }
    
    private Map<String, List<DistrictEntry>> repository;

    /* Flag indicating if DistrictEntry concerns all local numbers on given street
      or only specific set of local numbers on that street: */
    private enum EntryMode { ALL_NUMBERS, SPECIFIC_NUMBERS }

    private static class DistrictEntry {
        public District district;
        public EntryMode mode;
        public Set<Integer> numbers;

        public DistrictEntry(District district, EntryMode mode, Set<Integer> numbers) {
            this.district = district;
            this.mode = mode;
            this.numbers = numbers;
        }
    }
}
