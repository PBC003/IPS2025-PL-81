package ips.club.ui;

import ips.club.controller.ReceiptBatchController;
import ips.club.model.User;
import ips.club.ui.table.CandidateUsersTableModel;
import ips.club.ui.table.SelectedUsersTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class CreateReceiptBatchWindow extends JDialog {
    private final ReceiptBatchController controller;

    private final CandidateUsersTableModel mLeft = new CandidateUsersTableModel();
    private final SelectedUsersTableModel mRight = new SelectedUsersTableModel();

    private JSpinner spYear;
    private JComboBox<String> cbMonth;
    private JTextField txtBankEntity;
    private JTextField txtFileName;

    private JButton btnReload;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnCreate;
    private JButton btnSelectAll;
    private JButton btnSelectNone;

    private JTable tblLeft;
    private JTable tblRight;

    public CreateReceiptBatchWindow(ReceiptBatchController controller) {
        super(null, "Crear lote", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        initUI();
        initEvents();
        loadLeftCandidates();
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;

        YearMonth now = YearMonth.now();
        spYear = new JSpinner(new SpinnerNumberModel(now.getYear(), 2000, 2100, 1));
        cbMonth = new JComboBox<String>();
        for (int m = 1; m <= 12; m++)
            cbMonth.addItem(String.format("%02d", m));
        cbMonth.setSelectedItem(String.format("%02d", now.getMonthValue()));

        txtBankEntity = new JTextField(12);
        txtFileName = new JTextField(18);

        btnReload = new JButton("Recargar candidatos");

        int col = 0;
        int row = 0;
        gc.gridx = col++;
        gc.gridy = row;
        top.add(new JLabel("Año:"), gc);
        gc.gridx = col++;
        top.add(spYear, gc);
        gc.gridx = col++;
        top.add(new JLabel("Mes:"), gc);
        gc.gridx = col++;
        top.add(cbMonth, gc);
        gc.gridx = col++;
        top.add(btnReload, gc);

        row++;
        col = 0;
        gc.gridx = col++;
        gc.gridy = row;
        top.add(new JLabel("Entidad bancaria:"), gc);
        gc.gridx = col++;
        top.add(txtBankEntity, gc);
        gc.gridx = col++;
        top.add(new JLabel("Fichero CSV:"), gc);
        gc.gridx = col++;
        top.add(txtFileName, gc);

        add(top, BorderLayout.NORTH);

        tblLeft = new JTable(mLeft);
        tblLeft.setAutoCreateRowSorter(true);

        tblRight = new JTable(mRight);
        tblRight.setAutoCreateRowSorter(true);

        btnAdd = new JButton("Añadir →");
        btnRemove = new JButton("← Quitar");
        btnSelectAll = new JButton("Marcar todo (izq)");
        btnSelectNone = new JButton("Desmarcar (izq)");

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        center.add(new JScrollPane(tblLeft), c);

        JPanel midBtns = new JPanel();
        midBtns.setLayout(new BoxLayout(midBtns, BoxLayout.Y_AXIS));
        midBtns.add(btnAdd);
        midBtns.add(Box.createVerticalStrut(8));
        midBtns.add(btnRemove);
        midBtns.add(Box.createVerticalStrut(16));
        midBtns.add(btnSelectAll);
        midBtns.add(Box.createVerticalStrut(4));
        midBtns.add(btnSelectNone);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
        center.add(midBtns, c);

        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.5;
        center.add(new JScrollPane(tblRight), c);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCreate = new JButton("Crear lote y exportar");
        JButton btnClose = new JButton("Cerrar");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        bottom.add(btnCreate);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }

    private void initEvents() {
        btnReload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onReload(e);
            }
        });
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAddSelected(e);
            }
        });
        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRemoveSelected(e);
            }
        });
        btnCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCreateSingleBatch(e);
            }
        });
        btnSelectAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                markAllLeft(true);
            }
        });
        btnSelectNone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                markAllLeft(false);
            }
        });
    }

    private void onReload(ActionEvent e) {
        loadLeftCandidates();
    }

    private String yyyymmFromUi() {
        int y = ((Integer) spYear.getValue()).intValue();
        String mm = (String) cbMonth.getSelectedItem();
        return String.format("%04d%s", new Object[] { Integer.valueOf(y), mm });
    }

    private void loadLeftCandidates() {
        String yyyymm = yyyymmFromUi();
        try {
            List<User> users = controller.findUsersWithoutReceiptFor(yyyymm);
            mLeft.setData(users);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando candidatos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markAllLeft(boolean value) {
        for (int r = 0; r < mLeft.getRowCount(); r++) {
            mLeft.setValueAt(Boolean.valueOf(value), r, 0);
        }
    }

    private List<User> getSelectedUsersFromLeft() {
        List<User> out = new ArrayList<User>();
        int rows = mLeft.getRowCount();
        for (int r = 0; r < rows; r++) {
            Object v = mLeft.getValueAt(r, 0);
            boolean sel = (v instanceof Boolean) ? ((Boolean) v).booleanValue() : false;
            if (sel) {
                User u = mLeft.getUserAt(r);
                if (u != null)
                    out.add(u);
            }
        }
        return out;
    }

    private void onAddSelected(ActionEvent e) {
        String yyyymm = yyyymmFromUi();
        List<User> chosen = getSelectedUsersFromLeft();
        if (chosen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Marca filas en la tabla izquierda.", "Nada que añadir",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        mRight.addAll(yyyymm, chosen);
        markAllLeft(false);
    }

    private void onRemoveSelected(ActionEvent e) {
        int[] viewRows = tblRight.getSelectedRows();
        if (viewRows == null || viewRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona filas en la tabla derecha.", "Nada que quitar",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        mRight.removeRows(viewRows, tblRight);
    }

    private void onCreateSingleBatch(ActionEvent e) {
        if (mRight.size() == 0) {
            JOptionPane.showMessageDialog(this, "Añade usuarios a la tabla de la derecha.",
                    "Nada que procesar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String bank = txtBankEntity.getText() == null ? "" : txtBankEntity.getText().trim();
        if (bank.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce la entidad bancaria.",
                    "Falta entidad", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String fileName = txtFileName.getText() == null ? "" : txtFileName.getText().trim();
        if (fileName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce el nombre del fichero.",
                    "Falta fichero", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!fileName.toLowerCase().endsWith(".csv")) {
            fileName = fileName + ".csv";
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Se creará un solo lote con todos los recibos seleccionados y se exportará a un único fichero.\n"
                        +
                        "¿Deseas continuar?",
                "Confirmar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.OK_OPTION)
            return;

        try {
            Map<String, List<Integer>> byMonth = mRight.groupByMonthUserIds();

            Path out = controller.createOneBatchGeneratingReceiptsForMultipleMonthsAndExport(
                    bank, fileName, byMonth);

            JOptionPane.showMessageDialog(this,
                    "Lote creado y exportado correctamente:\n" + out,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            mRight.clear();
            loadLeftCandidates();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al crear/exportar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
