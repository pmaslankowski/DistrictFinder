import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainForm {
    private JPanel panel1;
    private JButton searchButton;
    private JTextField addressText;
    private JTable resultTable;
    private Application app;

    public MainForm(Application app) {
        this.app = app;

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.resolveStreet(addressText.getText());
            }
        });
    }

    public JPanel getView() {
        return panel1;
    }
    public JTable getResultTable() { return resultTable; }
}
