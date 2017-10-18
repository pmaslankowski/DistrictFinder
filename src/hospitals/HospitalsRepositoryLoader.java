package hospitals;

import hospitals.exceptions.HospitalsLoadingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class HospitalsRepositoryLoader {
    public HospitalsRepositoryLoader(String path) {
        this.path = path;
    }

    public void load() throws HospitalsLoadingException {
        repository = new TreeMap<>();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new HospitalsLoadingException("Wrong XML parser configuration.\n" + e.getMessage());
        }
        try (InputStream inputStream = Files.newInputStream(Paths.get(path))) {
            loadHospitals(inputStream, builder);
        } catch (IOException e) {
            throw new HospitalsLoadingException("Error during opening " + path + " file.\n" + e.getMessage());
        }
    }

    public Map<String, Hospital> getRepository() {
        return repository;
    }

    private void loadHospitals(InputStream hospitalsStream, DocumentBuilder builder) throws HospitalsLoadingException {
        try {
            Document document = builder.parse(hospitalsStream);
            NodeList hospitalNodes = document.getElementsByTagName("hospital");
            for(int i=0; i < hospitalNodes.getLength(); i++)
                loadHospital((Element)hospitalNodes.item(i));
        } catch (SAXException e) {
            throw new HospitalsLoadingException("SAXException: " + e.getMessage());
        } catch (IOException e) {
            throw new HospitalsLoadingException("IOException: " + e.getMessage());
        }
    }

    private void loadHospital(Element hospitalEl) throws HospitalsLoadingException {
        String id = hospitalEl.getElementsByTagName("id").item(0).getTextContent();
        String name = hospitalEl.getElementsByTagName("name").item(0).getTextContent();
        String address = hospitalEl.getElementsByTagName("address").item(0).getTextContent();
        String phone = hospitalEl.getElementsByTagName("phone").item(0).getTextContent();
        if(id.isEmpty() || name.isEmpty() || address.isEmpty() || phone.isEmpty())
            throw new HospitalsLoadingException("Wrong hospital description in file: " + path);
        if(repository.containsKey(id))
            throw new HospitalsLoadingException("Many hospitals with the same id: " + id + " in file: " + path);
        repository.put(id, new Hospital(id, name, address, phone));
    }
    
    private String path;
    private Map<String, Hospital> repository;
}
