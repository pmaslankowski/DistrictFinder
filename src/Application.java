import javax.swing.*;

public class Application {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        JFrame frame = new JFrame("Wyszukiwanie osiedli i ośrodków POZ");
        frame.setContentPane(new MainForm().getView());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
