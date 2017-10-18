package districts;

import districts.exceptions.DistrictLoadingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DistrictsRepositoryLoader {
    public DistrictsRepositoryLoader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public void load() throws DistrictLoadingException {
        repository = new TreeMap<>();
        File directory = new File(directoryPath);
        if(!directory.isDirectory())
            throw new DistrictLoadingException("Given district directory: " + directoryPath + " is not a directory");

        DocumentBuilderFactory buildersFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = buildersFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new DistrictLoadingException("Wrong XML parser configuration.\n" + e.getMessage());
        }
        List<File> districtFiles = Arrays.asList(directory.listFiles());

        for(File districtFile : districtFiles) {
            if(districtFile.isFile()) {
                currentFile = districtFile.getName();
                try (InputStream districtInputStream = Files.newInputStream(Paths.get(districtFile.getPath()))) {
                    loadDistrict(districtInputStream, docBuilder);
                } catch (IOException e) {
                    throw new DistrictLoadingException("Error during opening " + districtFile.getPath() + " file.\n" + e.getMessage());
                }
            }
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
        int evenStart = evenStartStr.isEmpty() ? -1 : Integer.parseInt(evenStartStr);
        int evenEnd = evenEndStr.isEmpty() ? -1 : Integer.parseInt(evenEndStr);
        int oddStart = oddStartStr.isEmpty() ? -1 : Integer.parseInt(oddStartStr);
        int oddEnd = oddEndStr.isEmpty() ? -1 : Integer.parseInt(oddEndStr);
        List<String> additional_splited;
        if(additionalStr.isEmpty())
            additional_splited = new LinkedList<>();
        else
            additional_splited = Arrays.asList(additionalStr.split(","));
        List<Integer> additional = additional_splited.stream()
                .map(number -> Integer.parseInt(number.trim()))
                .collect(Collectors.toList());

        if(evenStart == -1 && evenEnd == -1 && oddStart == -1 && oddEnd == -1 && additional.isEmpty())
            addToRepository(street, new DistrictEntry(district, DistrictEntry.EntryMode.ALL_NUMBERS, null));
        else
            addToRepository(street, new DistrictEntry(district, DistrictEntry.EntryMode.SPECIFIC_NUMBERS,
                    createNumbersSet(street, evenStart, evenEnd, oddStart, oddEnd, additional)));
    }

    private void addToRepository(String street, DistrictEntry entry) throws DistrictLoadingException {
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

    private Set<Integer> createNumbersSet(String street, int evenStart, int evenEnd, int oddStart, int oddEnd, List<Integer> additional)
            throws DistrictLoadingException {
        if((evenStart != -1 && evenEnd == -1) || (evenStart == -1 && evenEnd != -1))
            throw new DistrictLoadingException(
                    String.format("Wrong configuration of even range:\n street: %s, file: %s", street, currentFile));
        if((oddStart != -1 && oddEnd == -1) || (oddStart == -1 && oddEnd != -1))
            throw new DistrictLoadingException(
                    String.format("Wrong configuration of odd range:\n street: %s, file: %s", street, currentFile));

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

    private String directoryPath;
    private String currentFile;
    private Map<String, List<DistrictEntry>> repository;
}
