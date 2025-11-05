package ips.club.ui;

import ips.club.controller.ReceiptBatchController;
import ips.club.model.Receipt;
import ips.util.ApplicationException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class CreateReceiptBatchWindow extends JDialog {

    private final ReceiptBatchController controller;

    private final JTextField txtEntity = new JTextField(18);
    private final JTextField txtFile = new JTextField(22);

    private final DefaultTableModel mAvail = new DefaultTableModel(
            new Object[]{"ID", "UserId", "Mes", "Importe (cts)", "Concepto"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel mSel = new DefaultTableModel(
            new Object[]{"ID", "UserId", "Mes", "Importe (cts)", "Concepto"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable tAvail = new JTable(mAvail);
    private final JTable tSel = new JTable(mSel);

    private final JButton btnAdd = new JButton("➜ Añadir");
    private final JButton btnRemove = new JButton("⟵ Quitar");
    private final JButton btnCreate = new JButton("Crear lote");
    private final JButton btnCancel = new JButton("Cerrar");

    public CreateReceiptBatchWindow(ReceiptBatchController controller) {
        super((Frame) null, "Crear lote de recibos", true);
        this.controller = controller;

        setSize(980, 560);
        setLocationRelativeTo((Frame) null);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        top.add(new JLabel("Entidad:"));
        top.add(txtEntity);
        top.add(new JLabel("Fichero:"));
        top.add(txtFile);
        root.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        tAvail.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tSel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        center.add(new JScrollPane(tAvail), gbc);

        JPanel middleBtns = new JPanel();
        middleBtns.setLayout(new javax.swing.BoxLayout(middleBtns, javax.swing.BoxLayout.Y_AXIS));
        middleBtns.add(btnAdd);
        middleBtns.add(javax.swing.Box.createVerticalStrut(8));
        middleBtns.add(btnRemove);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.0;
        center.add(middleBtns, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.5;
        center.add(new JScrollPane(tSel), gbc);

        root.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnCreate);
        bottom.add(btnCancel);
        root.add(bottom, BorderLayout.SOUTH);

        btnAdd.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { moveRows(tAvail, mAvail, mSel); }
        });
        btnRemove.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { moveRows(tSel, mSel, mAvail); }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { dispose(); }
        });
        btnCreate.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { onCreate(); }
        });

        loadAvailableAll();
    }

    private void loadAvailableAll() {
        mAvail.setRowCount(0);
        try {
            List<Receipt> rs = controller.findUnbatchedReceiptsAll();
            for (int i = 0; i < rs.size(); i++) {
                Receipt r = rs.get(i);
                mAvail.addRow(new Object[]{
                        Integer.valueOf(r.getId()),
                        Integer.valueOf(r.getUserId()),
                        r.getChargeMonth(),
                        Integer.valueOf(r.getAmountCents()),
                        r.getConcept()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error al cargar recibos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void moveRows(JTable fromTable, DefaultTableModel from, DefaultTableModel to) {
        int[] rows = fromTable.getSelectedRows();
        if (rows == null || rows.length == 0) return;
        for (int i = rows.length - 1; i >= 0; i--) {
            int r = rows[i];
            Object[] data = new Object[from.getColumnCount()];
            for (int c = 0; c < data.length; c++) {
                data[c] = from.getValueAt(r, c);
            }
            to.addRow(data);
            from.removeRow(r);
        }
    }

    private List<Integer> selectedReceiptIds() {
        List<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < mSel.getRowCount(); i++) {
            Object val = mSel.getValueAt(i, 0);
            if (val instanceof Integer) {
                ids.add((Integer) val);
            } else if (val instanceof Number) {
                ids.add(Integer.valueOf(((Number) val).intValue()));
            } else {
                try {
                    ids.add(Integer.valueOf(Integer.parseInt(String.valueOf(val))));
                } catch (ApplicationException e) { throw new ApplicationException("ID de recibo inválido en la tabla de seleccionados."); }
            }
        }
        return ids;
    }

    private void onCreate() {
        String bank = txtEntity.getText() == null ? "" : txtEntity.getText().trim();
        String file = txtFile.getText() == null ? "" : txtFile.getText().trim();

        if (bank.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Indica la entidad bancaria.",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (file.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Indica el nombre de fichero.",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Integer> receiptIds = selectedReceiptIds();
        if (receiptIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona al menos un recibo.",
                    "Lote vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String currentYearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        try {
            controller.createBatchWithReceipts(currentYearMonth, bank, file, receiptIds);
            JOptionPane.showMessageDialog(this, "Lote creado y recibos asignados.");
            dispose();
        } catch (ApplicationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo crear el lote", JOptionPane.ERROR_MESSAGE);
        }
    }
}
