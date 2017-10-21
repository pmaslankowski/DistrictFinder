import districts.DistrictsRepository;
import districts.DistrictsRepositoryLoader;
import hospitals.HospitalsRepository;
import hospitals.HospitalsRepositoryLoader;
import hospitals.exceptions.HospitalsLoadingException;

import javax.swing.*;

public class Application {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        JFrame frame = new JFrame("Wyszukiwanie osiedli i ośrodków POZ");
        frame.setContentPane(new MainForm().getView());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        testDistrictsRepository();
        testHospitalsRepository();
    }

    private static void testDistrictsRepository() {
        DistrictsRepositoryLoader loader = new DistrictsRepositoryLoader("data/districts/");
        try {
            loader.load();
            DistrictsRepository repo = new DistrictsRepository(loader);
            System.out.println(repo);
            System.out.println();
            System.out.println(repo.get("Zdrowa", 20).label);
            System.out.println(repo.get("Podwale", 28).label);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testHospitalsRepository() {
        HospitalsRepositoryLoader loader = new HospitalsRepositoryLoader("data/hospitals.xml");
        try {
            loader.load();
            HospitalsRepository repo = new HospitalsRepository(loader);
            System.out.println(repo);
            System.out.println();
            System.out.println(repo.get("TEST_HOSPITAL1").getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
