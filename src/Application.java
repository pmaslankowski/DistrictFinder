import districts.DistrictsRepository;
import districts.DistrictsRepositoryLoader;

import javax.swing.*;

public class Application {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        JFrame frame = new JFrame("Wyszukiwanie osiedli i ośrodków POZ");
        frame.setContentPane(new MainForm().getView());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        test();
    }

    private static void test() {
        DistrictsRepositoryLoader loader = new DistrictsRepositoryLoader("data/districts/");
        try {
            loader.load();
            DistrictsRepository repo = new DistrictsRepository(loader);
            System.out.println(repo);
            System.out.println();
            System.out.println(repo.get("Dworcowa", 20));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
