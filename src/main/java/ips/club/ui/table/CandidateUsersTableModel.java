package ips.club.ui.table;

import ips.club.model.User;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

@SuppressWarnings("serial")
public class CandidateUsersTableModel extends AbstractTableModel {
    private final String[] cols = { "Sel.", "ID", "Nombre", "Apellidos", "Cuota (cents)", "IBAN" };
    private final List<User> rows = new ArrayList<>();
    private final BitSet selected = new BitSet();

    public void setData(List<User> users) {
        rows.clear();
        if (users != null) {
            rows.addAll(users);
        }
        selected.clear();
        fireTableDataChanged();
    }

    public List<Integer> getSelectedUserIds() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            if (selected.get(i)) {
                User u = rows.get(i);
                if (u != null && u.getId() != null) {
                    ids.add(u.getId());
                }
            }
        }
        return ids;
    }

    public User getUserAt(int row) {
        if (row < 0 || row >= rows.size())
            return null;
        return rows.get(row);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int c) {
        return cols[c];
    }

    @Override
    public Class<?> getColumnClass(int c) {
        switch (c) {
            case 0:
                return Boolean.class;
            case 1:
                return Integer.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return Integer.class;
            case 5:
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int r, int c) {
        return c == 0;
    }

    @Override
    public Object getValueAt(int r, int c) {
        User u = rows.get(r);
        switch (c) {
            case 0:
                return Boolean.valueOf(selected.get(r));
            case 1:
                return u.getId();
            case 2:
                return u.getName();
            case 3:
                return u.getSurname();
            case 4:
                return u.getMonthlyFeeCents();
            case 5:
                return u.getIban();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object v, int r, int c) {
        if (c == 0 && r >= 0 && r < rows.size()) {
            boolean sel = (v instanceof Boolean) ? ((Boolean) v).booleanValue() : false;
            if (sel) {
                selected.set(r);
            } else {
                selected.clear(r);
            }
            fireTableCellUpdated(r, c);
        }
    }
}
