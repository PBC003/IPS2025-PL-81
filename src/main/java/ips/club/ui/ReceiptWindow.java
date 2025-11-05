package ips.club.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ips.club.controller.ReceiptsController;
import ips.club.model.Receipt;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@SuppressWarnings("serial")
public class ReceiptWindow extends JDialog {

    private final ReceiptsController controller;

    private final JLabel title = new JLabel("Recibos", SwingConstants.LEFT);
    private final JTable table = new JTable();
    private final JButton btnRefresh = new JButton("Refrescar");
    private final JButton btnGenerate = new JButton("Generar mes…");
    private final JButton btnClose = new JButton("Cerrar");

    private final JComboBox<Integer> cbYear = new JComboBox<>();
    private final JComboBox<Integer> cbMonth = new JComboBox<>();

    public ReceiptWindow(ReceiptsController controller) {
        super(null, "Recibos", ModalityType.MODELESS);
        this.controller = controller;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.WEST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 5; y <= currentYear + 5; y++)
            cbYear.addItem(y);
        for (int m = 1; m <= 12; m++)
            cbMonth.addItem(m);
        cbYear.setSelectedItem(currentYear);
        cbMonth.setSelectedItem(LocalDate.now().getMonthValue());
        filters.add(new JLabel("Año:"));
        filters.add(cbYear);
        filters.add(new JLabel("Mes:"));
        filters.add(cbMonth);
        header.add(filters, BorderLayout.EAST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnRefresh);
        actions.add(btnGenerate);
        actions.add(btnClose);

        JScrollPane center = new JScrollPane(table);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        cbYear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
                updateGenerateButtonEnabled();
            }
        });
        cbMonth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
                updateGenerateButtonEnabled();
            }
        });
        btnGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onGenerate();
            }
        });

        loadData();
        updateGenerateButtonEnabled();
    }

    private DefaultTableModel newEmptyModel() {
        return new DefaultTableModel(
                new Object[] {
                        "Nº Recibo", "UserId", "Periodo (YYYYMM)", "Importe",
                        "Estado", "Lote", "Fecha emisión", "Fecha valor", "Concepto"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void loadData() {
        btnRefresh.setEnabled(false);
        try {
            String yyyymm = selectedYYYYMM();

            List<Receipt> receipts = controller.listByMonth(yyyymm);

            DefaultTableModel model = newEmptyModel();
            for (Receipt r : receipts) {
                Integer batchId = r.getBatchId() == null ? 0 : r.getBatchId();

                model.addRow(new Object[] {
                        r.getReceiptNumber(), r.getUserId(),
                        r.getChargeMonth(), r.getAmountCents()/100+"€",
                        r.getStatus().name(), batchId,
                        r.getIssueDate().toString(), r.getValueDate(),
                        r.getConcept()
                });
            }

            table.setModel(model);
            table.setAutoCreateRowSorter(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            btnRefresh.setEnabled(true);
        }
    }

    private String selectedYYYYMM() {
        Integer year = (Integer) cbYear.getSelectedItem();
        Integer month = (Integer) cbMonth.getSelectedItem();
        return String.format("%04d%02d", year, month);
    }

    private void updateGenerateButtonEnabled() {
        String now = YearMonth.now().toString().replace("-", "");
        ;
        String sel = selectedYYYYMM();
        btnGenerate.setEnabled(now.equals(sel));
    }

    private void onGenerate() {
        String yyyymm = YearMonth.now().toString().replace("-", "");

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Se van a generar los recibos del periodo " + yyyymm + " para todos los socios activos.\n¿Continuar?",
                "Confirmar generación",
                JOptionPane.OK_CANCEL_OPTION);

        if (ok != JOptionPane.OK_OPTION)return;

        try {
            int created = controller.generateAllMonthlyReceipts(yyyymm);
            JOptionPane.showMessageDialog(this, "Recibos generados: " + created);
            loadData();
            updateGenerateButtonEnabled();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}