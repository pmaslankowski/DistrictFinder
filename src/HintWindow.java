import districts.DistrictsFuzzyFinder;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class HintWindow {
    public HintWindow(Application app, JFrame parent, DistrictsFuzzyFinder finder, String street, int number) {
        this.app = app;
        this.parent = parent;
        this.finder = finder;
        this.street = street;
        this.number = number;
        app.setSearchButtonEnabled(false);
        createWindow();
        findBestMatches();
    }

    private void createWindow() {
        frame = new JFrame("Brak adresu w repozytorium");
        form = new HintForm();
        frame.setContentPane(form.getView());
        frame.pack();
        frame.setLocationRelativeTo(parent);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.setSearchButtonEnabled(true);
            }
        });
        JTable hintTable = form.getHintTable();
        hintTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int row = hintTable.getSelectedRow();
                String street = (String) hintTable.getModel().getValueAt(row,0 );
                app.setAddressAndResolve(street);
                app.setSearchButtonEnabled(true);
                frame.dispose();
            }
        });
    }

    private void findBestMatches() {
        List<String> matches = finder.getBestMatches(street, number);
        form.getHintTable().setModel(new AbstractTableModel() {

            @Override
            public int getRowCount() {
                return matches.size();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return matches.get(rowIndex);
            }
        });
        form.centerHints();
    }

    private Application app;
    private JFrame parent;
    private JFrame frame;
    private HintForm form;
    private DistrictsFuzzyFinder finder;
    private String street;
    private int number;
}
