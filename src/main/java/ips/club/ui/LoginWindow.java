package ips.club.ui;

import ips.club.controller.UsersController;
import ips.club.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class LoginWindow extends JDialog {

    private final UsersController controller;
    private final DefaultListModel<User> model = new DefaultListModel<>();
    private List<User> allUsers = new ArrayList<>();

    private JList<User> lstUsers;
    private JTextField txtSearch;
    private JButton btnOk;
    private JButton btnCancel;

    private User selected;

    public LoginWindow(UsersController controller) {
        super((Frame) null, "Seleccionar usuario", true);
        this.controller = controller;
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        initUI();
        safeLoadUsers();

        pack();
        setMinimumSize(new Dimension(420, 360));
        setLocationRelativeTo(null);
    }

    public User getSelectedUser() {
        return selected;
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        JPanel north = new JPanel(new BorderLayout(6, 6));
        north.add(new JLabel("Buscar:"), BorderLayout.WEST);
        txtSearch = new JTextField();
        north.add(txtSearch, BorderLayout.CENTER);
        root.add(north, BorderLayout.NORTH);

        lstUsers = new JList<User>(model);
        lstUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstUsers.setVisibleRowCount(12);
        lstUsers.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User) {
                    User u = (User) value;
                    lbl.setText(u.getName() + " — " + u.getEmail());
                }
                return lbl;
            }
        });
        root.add(new JScrollPane(lstUsers), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCancel = new JButton("Cancelar");
        btnOk = new JButton("Entrar");
        btnOk.setEnabled(false);
        south.add(btnCancel);
        south.add(btnOk);
        root.add(south, BorderLayout.SOUTH);

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accept();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected = null;
                dispose();
            }
        });
        lstUsers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;
                btnOk.setEnabled(!lstUsers.isSelectionEmpty());
            }
        });
        lstUsers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !lstUsers.isSelectionEmpty())
                    accept();
            }
        });
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filter(txtSearch.getText());
            }
        });

        getRootPane().registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancel.doClick();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        getRootPane().setDefaultButton(btnOk);
    }

    private void safeLoadUsers() {
        try {
            allUsers = controller.loadUsers();
            refreshList(allUsers);
        } catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error cargando usuarios:\n" + t.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshList(List<User> users) {
        model.clear();
        for (User u : users)
            model.addElement(u);
        if (!model.isEmpty()) {
            lstUsers.setSelectedIndex(0);
            btnOk.setEnabled(true);
        } else {
            btnOk.setEnabled(false);
        }
    }

    private void filter(String text) {
        String q = (text == null) ? "" : text.trim().toLowerCase();
        if (q.isEmpty()) {
            refreshList(allUsers);
            return;
        }
        List<User> filtered = new ArrayList<User>();
        for (User u : allUsers) {
            String target = (u.getName() + " " + u.getEmail()).toLowerCase();
            if (target.contains(q))
                filtered.add(u);
        }
        refreshList(filtered);
    }

    private void accept() {
        selected = lstUsers.getSelectedValue();
        if (selected != null)
            dispose();
    }
}
