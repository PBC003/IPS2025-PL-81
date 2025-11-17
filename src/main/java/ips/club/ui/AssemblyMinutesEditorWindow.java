package ips.club.ui;

import ips.club.controller.AssemblyController;
import ips.club.model.Assembly;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class AssemblyMinutesEditorWindow extends JDialog {

    private final AssemblyController controller;
    private final int assemblyId;

    private final DefaultListModel<AgendaItem> agendaModel = new DefaultListModel<AgendaItem>();
    private final JList<AgendaItem> lstAgenda = new JList<AgendaItem>(agendaModel);
    private final JTextField tfItemTitle = new JTextField(28);
    private final JTextArea taItemNotes = new JTextArea(3, 28);
    private final JButton btnAddItem = new JButton("Añadir punto");
    private final JButton btnRemoveItem = new JButton("Eliminar seleccionado");

    private final JTextArea taRuegos = new JTextArea(6, 40);

    private final JButton btnSave = new JButton("Guardar acta");
    private final JButton btnCancel = new JButton("Cancelar");

    private Assembly updated;

    public AssemblyMinutesEditorWindow(Window owner, AssemblyController controller, int assemblyId) {
        super(owner, "Crear acta (→ WAITING)", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.assemblyId = assemblyId;

        lstAgenda.setVisibleRowCount(8);
        lstAgenda.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        taItemNotes.setLineWrap(true);
        taItemNotes.setWrapStyleWord(true);
        taRuegos.setLineWrap(true);
        taRuegos.setWrapStyleWord(true);


        JPanel pnlEditor = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        int r = 0;
        gc.gridx=0; gc.gridy=r; pnlEditor.add(new JLabel("Título del punto:"), gc);
        gc.gridx=1; gc.gridy=r++; pnlEditor.add(tfItemTitle, gc);

        gc.gridx=0; gc.gridy=r; pnlEditor.add(new JLabel("Notas (opcional):"), gc);
        gc.gridx=1; gc.gridy=r++; pnlEditor.add(new JScrollPane(taItemNotes), gc);

        JPanel pnlEditorBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlEditorBtns.add(btnAddItem);
        pnlEditorBtns.add(btnRemoveItem);
        gc.gridx=1; gc.gridy=r++; pnlEditor.add(pnlEditorBtns, gc);

        JPanel pnlAgenda = new JPanel(new BorderLayout(4,4));
        pnlAgenda.add(new JLabel("Orden del día / Puntos a tratar:"), BorderLayout.NORTH);
        pnlAgenda.add(new JScrollPane(lstAgenda), BorderLayout.CENTER);

        JPanel pnlRuegos = new JPanel(new BorderLayout(4,4));
        pnlRuegos.add(new JLabel("Ruegos y preguntas (opcional):"), BorderLayout.NORTH);
        pnlRuegos.add(new JScrollPane(taRuegos), BorderLayout.CENTER);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlActions.add(btnSave);
        pnlActions.add(btnCancel);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(6,6,6,6);
        c2.fill = GridBagConstraints.BOTH; c2.weightx=1; c2.weighty=0;
        c2.gridx=0; c2.gridy=0; center.add(pnlEditor, c2);
        c2.weighty=1; c2.gridy=1; center.add(pnlAgenda, c2);
        c2.weighty=0.6; c2.gridy=2; center.add(pnlRuegos, c2);

        setLayout(new BorderLayout(8,8));
        add(center, BorderLayout.CENTER);
        add(pnlActions, BorderLayout.SOUTH);

        btnAddItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { onAddItem(); }
        });
        btnRemoveItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { onRemoveItem(); }
        });
        btnSave.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { onSave(); }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { dispose(); }
        });

        setSize(760, 600);
        setLocationRelativeTo(owner);
    }

    private void onAddItem() {
        String title = tfItemTitle.getText() != null ? tfItemTitle.getText().trim() : "";
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título del punto es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String notes = taItemNotes.getText() != null ? taItemNotes.getText().trim() : "";
        agendaModel.addElement(new AgendaItem(title, notes));
        tfItemTitle.setText("");
        taItemNotes.setText("");
        tfItemTitle.requestFocusInWindow();
    }

    private void onRemoveItem() {
        int i = lstAgenda.getSelectedIndex();
        if (i >= 0) agendaModel.remove(i);
    }

    private void onSave() {
        if (agendaModel.getSize() < 1) {
            JOptionPane.showMessageDialog(this,
                "Debes añadir al menos un ítem del orden del día.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String minutes = buildMinutesText();
        Assembly updated = controller.attachMinutesAndMarkWaiting(assemblyId, minutes);
        if (updated == null) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el acta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.updated = updated;
        dispose();
    }

    private String buildMinutesText() {
        StringBuilder sb = new StringBuilder();
        sb.append("1) Aprobación del acta anterior.\n\n");

        sb.append("2) Orden del día / Puntos a tratar:\n");
        for (int i = 0; i < agendaModel.getSize(); i++) {
            AgendaItem it = agendaModel.getElementAt(i);
            sb.append("   2.").append(i + 1).append(" ").append(it.title).append('\n');
            if (it.notes != null && !it.notes.isEmpty()) {
                sb.append("      - ").append(it.notes).append('\n');
            }
        }
        sb.append('\n');

        String rq = taRuegos.getText() != null ? taRuegos.getText().trim() : "";
        sb.append("3) Ruegos y preguntas:\n");
        if (rq.isEmpty()) {
            sb.append("   Sin ruegos ni preguntas.\n");
        } else {
            String[] lines = rq.split("\\R");
            for (String line : lines) {
                String l = line != null ? line.trim() : "";
                if (!l.isEmpty()) sb.append("   - ").append(l).append('\n');
            }
        }
        return sb.toString();
    }

    public Assembly getUpdated() { return updated; }

    private static final class AgendaItem {
        final String title;
        final String notes;
        AgendaItem(String title, String notes) { this.title = title; this.notes = notes; }
        @Override public String toString() { return title; }
    }
}
