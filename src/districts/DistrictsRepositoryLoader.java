package districts;

import districts.exceptions.DistrictLoadingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DistrictsRepositoryLoader {
    public DistrictsRepositoryLoader(String districtsListPath) {
        this.districtsListPath = districtsListPath;
    }

    public void load() throws DistrictLoadingException {
        repository = new TreeMap<>();
        DocumentBuilderFactory buildersFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = buildersFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new DistrictLoadingException("Wrong XML parser configuration.\n" + e.getMessage());
        }
        
        try (BufferedReader districtsReader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(districtsListPath), "UTF-8"))
        ) {
            String currentFile = null;
            while((currentFile = districtsReader.readLine()) != null) {
                try (InputStream districtInputStream =
                             getClass().getResourceAsStream("/data/districts/" + currentFile)) {
                    if(districtInputStream == null ) {
                        String msg = String.format(
                                "Błąd podczas otwierania pliku z opisem dzielnicy: %s. Taki plik nie istnieje.",
                                currentFile);
                        throw new DistrictLoadingException(msg);
                    }
                    loadDistrict(districtInputStream, docBuilder);
                } catch (IOException e) {
                    throw new DistrictLoadingException("Error during opening " + currentFile +
                            " file.\n" + e.getMessage());
                }
            }
        } catch (UnsupportedEncodingException e) {
            String msg = String.format("Nieprawidłowe kodowanie pliku z listą dzielnic.\nPlik: %s\nSzczegóły: %s",
                    districtsListPath, e.getMessage());
            throw new DistrictLoadingException(msg);
        } catch (IOException e) {
            String msg = String.format("Błąd podczas otwierania pliku z listą dzielnic.\nPlik: %s\nSzczegóły: %s",
                    districtsListPath, e.getMessage());
            throw new DistrictLoadingException(e.getMessage());
        }
    }

    public Map<String, List<DistrictEntry>> getRepository() {
        return repository;
    }
    
    private void loadDistrict(InputStream districtStream, DocumentBuilder builder) throws DistrictLoadingException {
        try {
            Document document = builder.parse(districtStream);
            District district = parseDistrict(document);
            NodeList streetNodes = document.getElementsByTagName("street");
            for(int i = 0; i < streetNodes.getLength(); i++)
                loadStreet((Element) streetNodes.item(i), district);
        } catch (SAXException e) {
            throw new DistrictLoadingException("SAXException: " + e.getMessage() + "\nFile: " + currentFile);
        } catch (IOException e) {
            throw new DistrictLoadingException("IOException: " + e.getMessage() + "\nFile: " + currentFile);
        }
    }

    private District parseDistrict(Document document) throws DistrictLoadingException {
        try {
            Element districtEl = (Element) document.getElementsByTagName("district").item(0);
            String districtName = districtEl.getAttribute("name");
            String districtLabel = districtEl.getAttribute("label");
            String hospitalId = districtEl.getElementsByTagName("hospital").item(0).getTextContent();
            return new District(districtName, districtLabel, hospitalId);
        } catch (NullPointerException e) {
            String msg = String.format("Błąd podczas ładowania repozytorium: " +
                    "brak jednego z tagów <district> lub <hospital>.\nPlik: %s", currentFile);
            throw new DistrictLoadingException(msg);
        }
    }

    private void loadStreet(Element streetEl, District district) throws DistrictLoadingException {
        String street = streetEl.getTextContent();
        String evenStartStr = streetEl.getAttribute("even-start");
        String evenEndStr = streetEl.getAttribute("even-end");
        String oddStartStr = streetEl.getAttribute("odd-start");
        String oddEndStr = streetEl.getAttribute("odd-end");
        String startStr = streetEl.getAttribute("start");
        String endStr = streetEl.getAttribute("end");
        String additionalStr = streetEl.getAttribute("additional");
        int evenStart = evenStartStr.isEmpty() ? -1 : Integer.parseInt(evenStartStr);
        int evenEnd = evenEndStr.isEmpty() ? -1 : Integer.parseInt(evenEndStr);
        int oddStart = oddStartStr.isEmpty() ? -1 : Integer.parseInt(oddStartStr);
        int oddEnd = oddEndStr.isEmpty() ? -1 : Integer.parseInt(oddEndStr);
        int start = startStr.isEmpty() ? -1 : Integer.parseInt(startStr);
        int end = endStr.isEmpty() ? -1 : Integer.parseInt(endStr);
        List<String> additional_splited;
        if(additionalStr.isEmpty())
            additional_splited = new LinkedList<>();
        else
            additional_splited = Arrays.asList(additionalStr.split(","));
        List<Integer> additional = additional_splited.stream()
                .map(number -> Integer.parseInt(number.trim()))
                .collect(Collectors.toList());

        if(evenStart == -1 && evenEnd == -1 && oddStart == -1 && oddEnd == -1 && start == -1 && end == -1
                && additional.isEmpty())
            addToRepository(street, new DistrictEntry(district, DistrictEntry.EntryMode.ALL_NUMBERS, null));
        else
            addToRepository(street, new DistrictEntry(district, DistrictEntry.EntryMode.SPECIFIC_NUMBERS,
                    createNumbersSet(street, evenStart, evenEnd, oddStart, oddEnd, start, end, additional)));
    }

    private void addToRepository(String str, DistrictEntry entry) throws DistrictLoadingException {
        String street = str.toLowerCase();
        List<DistrictEntry> currentEntry = repository.getOrDefault(street, new LinkedList<DistrictEntry>());
        if(entry.getMode() == DistrictEntry.EntryMode.ALL_NUMBERS && currentEntry.size() > 0) {
            String msg = String.format(
                    "Unconsistient district database: %s. Same house number belongs to many districts:\n" +
                    "street: %s, file: %s", street, currentFile);
            throw new DistrictLoadingException(msg);
        }
        currentEntry.add(entry);
        repository.put(street, currentEntry);
    }

    private Set<Integer> createNumbersSet(String street, int evenStart, int evenEnd,
                                          int oddStart, int oddEnd, int start, int end,
                                          List<Integer> additional)
            throws DistrictLoadingException {
        if((evenStart != -1 && evenEnd == -1) || (evenStart == -1 && evenEnd != -1))
            throw new DistrictLoadingException(
                    String.format("Wrong configuration of even range:\n street: %s, file: %s", street, currentFile));
        if((oddStart != -1 && oddEnd == -1) || (oddStart == -1 && oddEnd != -1))
            throw new DistrictLoadingException(
                    String.format("Wrong configuration of odd range:\n street: %s, file: %s", street, currentFile));
        if((start != -1 && end == -1))
            throw new DistrictLoadingException(
                    String.format("Wrong configuration of range:\n street: %s, file: %s", street, currentFile));

        List<Integer> evenNumbers = new LinkedList<>();
        List<Integer> oddNumbers = new LinkedList<>();
        List<Integer> otherNumbers = new LinkedList<>();
        Set<Integer> result = new HashSet<>();
        if(evenStart != -1 && evenEnd != -1)
            evenNumbers = IntStream.rangeClosed(evenStart, evenEnd)
                    .filter(n -> n % 2 == 0)
                    .boxed().collect(Collectors.toList());
        if(oddStart != -1 && oddEnd != -1)
            oddNumbers = IntStream.rangeClosed(oddStart, oddEnd)
                    .filter(n -> n % 2 == 1)
                    .boxed().collect(Collectors.toList());
        if(start != -1 && end != -1)
            otherNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        result.addAll(evenNumbers);
        result.addAll(oddNumbers);
        result.addAll(otherNumbers);
        result.addAll(additional);
        return result;
    }

    private String districtsListPath;
    private String currentFile;
    private Map<String, List<DistrictEntry>> repository;
}
