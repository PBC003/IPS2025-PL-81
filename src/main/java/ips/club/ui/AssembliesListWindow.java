package ips.club.ui;

import ips.club.controller.AssemblyController;
import ips.club.model.Assembly;
import ips.club.model.AssemblyStatus;
import ips.club.ui.table.AssembliesTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@SuppressWarnings("serial")
public class AssembliesListWindow extends JFrame {

    private final AssemblyController controller;

    private final JComboBox<Object> cbStatus;
    private final JTextField tfFrom;
    private final JTextField tfTo;
    private final JButton btnSearch;
    private final JButton btnCreate;
    private final JButton btnAttachMinutes;
    private final JButton btnApprove;
    private final JButton btnDetails;
    private final JButton btnRefresh;
    private final JButton btnClose;

    private final JTable table;
    private final AssembliesTableModel model;
    private final TableRowSorter<AssembliesTableModel> sorter;
    private boolean suppressFilterEvents = false;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AssembliesListWindow(AssemblyController controller) {
        super("Asambleas");
        this.controller = controller;

        JPanel pnlFilters = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;

        cbStatus = new JComboBox<Object>(new Object[] {
                "Todas",
                AssemblyStatus.SCHEDULED,
                AssemblyStatus.WAITING,
                AssemblyStatus.FINISHED
        });
        tfFrom = new JTextField(16);
        tfTo = new JTextField(16);
        tfFrom.setToolTipText("Desde (yyyy-MM-dd HH:mm) — opcional");
        tfTo.setToolTipText("Hasta (yyyy-MM-dd HH:mm) — opcional");

        btnSearch = new JButton("Buscar");

        int col = 0;
        gc.gridx = col++;
        gc.gridy = 0;
        pnlFilters.add(new JLabel("Estado:"), gc);
        gc.gridx = col++;
        pnlFilters.add(cbStatus, gc);
        gc.gridx = col++;
        pnlFilters.add(new JLabel("Desde:"), gc);
        gc.gridx = col++;
        pnlFilters.add(tfFrom, gc);
        gc.gridx = col++;
        pnlFilters.add(new JLabel("Hasta:"), gc);
        gc.gridx = col++;
        pnlFilters.add(tfTo, gc);
        gc.gridx = col++;
        pnlFilters.add(btnSearch, gc);

        model = new AssembliesTableModel();
        table = new JTable(model);
        sorter = new TableRowSorter<AssembliesTableModel>(model);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCreate = new JButton("Crear");
        btnAttachMinutes = new JButton("Crear acta → WAITING");
        btnApprove = new JButton("Aprobar acta → FINISHED");
        btnDetails = new JButton("Detalle");
        btnRefresh = new JButton("Recargar");
        btnClose = new JButton("Cerrar");

        pnlActions.add(btnCreate);
        pnlActions.add(btnAttachMinutes);
        pnlActions.add(btnApprove);
        pnlActions.add(btnDetails);
        pnlActions.add(btnRefresh);
        pnlActions.add(btnClose);

        setLayout(new BorderLayout());
        add(pnlFilters, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(pnlActions, BorderLayout.SOUTH);

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadWithFilters();
            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFiltersToDefault();
                reloadAll();
            }
        });
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreate();
            }
        });
        btnAttachMinutes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAttachMinutes();
            }
        });
        btnApprove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onApprove();
            }
        });
        btnDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDetails();
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting())
                    updateButtonsEnabled();
            }
        });
        cbStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suppressFilterEvents)
                    return;
                reloadWithFilters();
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 520);
        setLocationRelativeTo(null);

        reloadAll();
        updateButtonsEnabled();
    }

    private void reloadAll() {
        List<Assembly> list = controller.listAll();
        model.setRows(list);
        updateButtonsEnabled();
        table.revalidate();
        table.repaint();
    }

    private void reloadWithFilters() {
        Object sel = cbStatus.getSelectedItem();
        AssemblyStatus status = (sel instanceof AssemblyStatus) ? (AssemblyStatus) sel : null;

        LocalDateTime from = parseDateTime(tfFrom.getText());
        LocalDateTime to = parseDateTime(tfTo.getText());

        List<Assembly> list = controller.listFiltered(status, from, to);
        model.setRows(list);
        updateButtonsEnabled();
        table.revalidate();
        table.repaint();
    }

    private LocalDateTime parseDateTime(String txt) {
        if (txt == null)
            return null;
        txt = txt.trim();
        if (txt.isEmpty())
            return null;
        try {
            return LocalDateTime.parse(txt, DF);
        } catch (DateTimeParseException ex) {
            try {
                return LocalDateTime.parse(txt + " 00:00", DF);
            } catch (Exception ex2) {
                JOptionPane.showMessageDialog(this,
                        "Formato de fecha/hora inválido: " + txt + "\nUsa: yyyy-MM-dd HH:mm",
                        "Formato incorrecto", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
    }

    private Assembly getSelectedAssembly() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0)
            return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        return model.getAssemblyAt(modelRow);
    }

    private void updateButtonsEnabled() {
        Assembly a = getSelectedAssembly();
        boolean has = a != null;
        btnDetails.setEnabled(has);
        btnAttachMinutes.setEnabled(false);
        btnApprove.setEnabled(false);

        if (has && a.getStatus() != null) {
            if (a.getStatus() == AssemblyStatus.SCHEDULED) {
                btnAttachMinutes.setEnabled(true);
            } else if (a.getStatus() == AssemblyStatus.WAITING) {
                btnApprove.setEnabled(true);
            }
        }
    }

    private void onCreate() {
        AssemblyCreateWindow dlg = new AssemblyCreateWindow(this, controller);
        dlg.setVisible(true);
        Assembly created = dlg.getCreated();
        if (created != null) {
            reloadWithFilters();
            table.clearSelection();
            table.revalidate();
            table.repaint();
            JOptionPane.showMessageDialog(this, "Asamblea creada (SCHULED).", "OK", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onAttachMinutes() {
        Assembly a = getSelectedAssembly();
        if (a == null)
            return;

        AssemblyMinutesEditorWindow dlg = new AssemblyMinutesEditorWindow(this, controller, a.getId());
        dlg.setVisible(true);
        Assembly updated = dlg.getUpdated();
        if (updated != null) {
            reloadWithFilters();
            table.clearSelection();
            table.revalidate();
            table.repaint();
            JOptionPane.showMessageDialog(this, "Acta creada y asamblea marcada WAITING.", "OK",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onApprove() {
        Assembly a = getSelectedAssembly();
        if (a == null)
            return;
        int res = JOptionPane.showConfirmDialog(this,
                "¿Aprobar el acta y marcar FINISHED?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (res != JOptionPane.YES_OPTION)
            return;

        Assembly opt = controller.approveAndFinish(a.getId());
        if (opt != null) {
            reloadWithFilters();
            table.clearSelection();
            table.revalidate();
            table.repaint();
            JOptionPane.showMessageDialog(this, "Asamblea marcada FINISHED.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar la asamblea.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onDetails() {
        Assembly a = getSelectedAssembly();
        if (a == null)
            return;
        AssemblyDetailWindow dlg = new AssemblyDetailWindow(this, a);
        dlg.setVisible(true);
    }

    private void resetFiltersToDefault() {
        suppressFilterEvents = true;
        cbStatus.setSelectedIndex(0);
        tfFrom.setText("");
        tfTo.setText("");
        suppressFilterEvents = false;
    }

}
