import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HintForm {
    private JFrame frame;
    private JFrame parent;
    private JLabel label;
    private JPanel panel;
    private JTable hintTable;
    private DefaultTableCellRenderer centerRenderer;
    
    public HintForm() {
        hintTable.setRowHeight(40);
        centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public JPanel getView() {
        return panel;
    }

    public JTable getHintTable() {
        return hintTable;
    }

    public void centerHints() {
        hintTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
    }
}
