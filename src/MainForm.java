import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainForm {
    private JPanel panel1;
    private JButton searchButton;
    private JTextField addressText;
    private JTable resultTable;
    private JLabel resultLabel;
    private Application app;

    public MainForm(Application app) {
        this.app = app;
        
        resultTable.setRowHeight(50);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.resolveStreet(addressText.getText());
            }
        });
        addressText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.resolveStreet(addressText.getText());
            }
        });
        InputMap inputMap = panel1.getInputMap();
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "enter");
        panel1.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.resolveStreet(addressText.getText());
            }
        });
    }

    public JPanel getView() {
        return panel1;
    }
    public JTextField getAddressText() { return addressText; }
    public JTable getResultTable() { return resultTable; }
}
