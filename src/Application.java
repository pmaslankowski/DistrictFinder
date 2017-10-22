import districts.District;
import districts.DistrictsRepository;
import districts.DistrictsRepositoryLoader;
import districts.exceptions.DistrictLoadingException;
import districts.exceptions.InvalidAddressFormatException;
import districts.exceptions.StreetNotFoundException;
import hospitals.Hospital;
import hospitals.HospitalsRepository;
import hospitals.HospitalsRepositoryLoader;
import hospitals.exceptions.HospitalNotFoundException;
import hospitals.exceptions.HospitalsLoadingException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class Application {
    public Application() {
        createWindow();
        loadHospitalsRepository();
        loadDistrictsRepository();
    }

    public static void main(String[] args) {
        Application app = new Application();
    }

    public void resolveStreet(String address) {
        if(address.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Nazwa ulicy nie może być pusta.",
                    "Informacja",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        Thread worker = new Thread(() -> {
            synchronized(this) {
                try {
                    District district = districtsRepository.get(address);
                    Hospital hospital = hospitalsRepository.get(district.hospitalId);
                    form.getResultTable().setModel(new AbstractTableModel() {
                        private String[][] data =
                                {
                                        {"Adres pacjenta: ", address},
                                        {"Osiedle: ", district.label},
                                        {"Nazwa przypisanego szpitala:", hospital.getName()},
                                        {"Adres szpitala:", hospital.getAddress()},
                                        {"Telefon: ", hospital.getPhone()},
                                };

                        @Override
                        public int getColumnCount() {
                            return data[0].length;
                        }

                        @Override
                        public int getRowCount() {
                            return data.length;
                        }

                        @Override
                        public Object getValueAt(int row, int col) {
                            return data[row][col];
                        }

                        @Override
                        public Class<?> getColumnClass(int col) {
                            return data[0][col].getClass();
                        }
                    });

                } catch (StreetNotFoundException e) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Nie znaleziono adresu \"" + address + "\" w repozytorium.\n" +
                                    "Szczegóły:\n" + e.getMessage(),
                            "Informacja",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (InvalidAddressFormatException e) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Niepoprawny format adresu. Poprawny format:\n <nazwa ulicy> <numer domu>",
                            "Informacja",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (HospitalNotFoundException e) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Nie znaleziono przypisanego szpitala. " +
                                    "Najprawdopodobniej wewnętrzne pliki .xml programu są uszkodzone.",
                            "Błąd",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        worker.start();
    }

    private void createWindow() {
        JFrame frame = new JFrame("Wyszukiwanie osiedli i ośrodków POZ");
        form = new MainForm(this);
        frame.setContentPane(form.getView());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void loadDistrictsRepository() {
        DistrictsRepositoryLoader loader = new DistrictsRepositoryLoader("data/districts/");
        try {
            loader.load();
            districtsRepository = new DistrictsRepository(loader);
        } catch (DistrictLoadingException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Wystąpił błąd podczas ładowania repozytorium dzielnic. Program zakończy swoje działanie.\n" +
                            "Opis błędu:\n" + e.getMessage(),
                    "Błąd",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(-1);
        }
    }

    private void loadHospitalsRepository() {
        HospitalsRepositoryLoader loader = new HospitalsRepositoryLoader("data/hospitals.xml");
        try {
            loader.load();
            hospitalsRepository = new HospitalsRepository(loader);
        } catch (HospitalsLoadingException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Wystąpił błąd podczas ładowania repozytorium szpitali. Program zakończy swoje działanie.\n" +
                            "Opis błędu:\n" + e.getMessage(),
                    "Błąd",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(-1);
        }
    }

    private MainForm form;
    private JFrame frame;
    private DistrictsRepository districtsRepository;
    private HospitalsRepository hospitalsRepository;
}
