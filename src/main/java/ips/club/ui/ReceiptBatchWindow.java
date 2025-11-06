package ips.club.ui;

import ips.club.controller.ReceiptBatchController;
import ips.club.model.ReceiptBatch;
import ips.club.model.ReceiptBatchStatus;
import ips.util.ApplicationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;


@SuppressWarnings("serial")
public class ReceiptBatchWindow extends JFrame {

    private final ReceiptBatchController controller;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Mes", "Entidad", "Estado", "Fichero", "Total (cts)", "Recibos", "Creado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    private final JButton btnNew = new JButton("Nuevo lote…");
    private final JButton btnRefresh = new JButton("Refrescar");
    private final JButton btnCancel = new JButton("Cancelar");
    private final JButton btnExport = new JButton("Exportar CSV");
    private final JButton btnClose = new JButton("Cerrar");

    public ReceiptBatchWindow(ReceiptBatchController controller) {
        super("Lotes de recibos");
        this.controller = controller;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo((Frame) null);

        initUI();
        loadData();
        updateButtons();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        setContentPane(root);

        JLabel title = new JLabel("Lotes existentes");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.add(title);
        root.add(north, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> updateButtons());
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        JPanel southLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel southRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));


        southRight.add(btnNew);
        southRight.add(btnRefresh);
        southRight.add(btnCancel);
        southRight.add(btnExport);
        southRight.add(btnClose);

        south.add(southLeft, BorderLayout.WEST);
        south.add(southRight, BorderLayout.EAST);
        root.add(south, BorderLayout.SOUTH);

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onExport();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        btnNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	CreateReceiptBatchWindow w = new CreateReceiptBatchWindow(controller);
            	w.setVisible(true);
            }
        });

    }

    private Integer selectedBatchId() {
        int row = table.getSelectedRow();
        return (row < 0) ? null : (Integer) model.getValueAt(row, 0);
    }

    private void updateButtons() {
        Integer id = selectedBatchId();
        boolean canExport = false;
        boolean canCancel = false;

        if (id != null) {
            String statusStr = String.valueOf(model.getValueAt(table.getSelectedRow(), 3));
            ReceiptBatchStatus status = ReceiptBatchStatus.valueOf(statusStr);
            canExport = (status == ReceiptBatchStatus.GENERATED);
            canCancel = (status == ReceiptBatchStatus.GENERATED);
        }

        btnExport.setEnabled(canExport);
        btnCancel.setEnabled(canCancel);
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            List<ReceiptBatch> list = controller.listBatches();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (ReceiptBatch b : list) {
                model.addRow(new Object[]{
                        b.getId(),
                        b.getChargeMonth(),
                        b.getBankEntity(),
                        b.getStatus().name(),
                        b.getFileName(),
                        b.getTotalAmount(),
                        b.getReceiptsCnt(),
                        (b.getCreatedAt() == null ? "" : dtf.format(b.getCreatedAt()))
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateButtons();
    }

    private void onExport() {
        Integer id = selectedBatchId();
        if (id == null) return;

        try {
            Path path = controller.exportBatch(id);
            loadData();
            JOptionPane.showMessageDialog(this, "Exportado a:\n" + path);
        } catch (ApplicationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo exportar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        Integer id = selectedBatchId();
        if (id == null) return;

        int ok = JOptionPane.showConfirmDialog(
                this,
                "¿Cancelar el lote " + id + " y liberar sus recibos?",
                "Confirmar cancelación",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            controller.cancelBatch(id);
            loadData();
            String msg = "Lote cancelado y recibos liberados.";
            JOptionPane.showMessageDialog(this, msg);
        } catch (ApplicationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo cancelar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
