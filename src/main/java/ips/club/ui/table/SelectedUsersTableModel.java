package ips.club.ui.table;

import ips.club.model.User;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class SelectedUsersTableModel extends AbstractTableModel {
    private final String[] cols = {"Mes (YYYYMM)", "ID", "Nombre", "Apellidos", "Cuota (cents)", "IBAN"};

    public static class Entry {
        public final String yyyymm;
        public final User user;
        public Entry(String yyyymm, User user) {
            this.yyyymm = yyyymm;
            this.user = user;
        }
    }

    private final List<Entry> rows = new ArrayList<>();

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }

    @Override public Class<?> getColumnClass(int c) {
        switch (c) {
            case 0: return String.class;
            case 1: return Integer.class;
            case 2: return String.class;
            case 3: return String.class;
            case 4: return Integer.class;
            case 5: return String.class;
            default: return Object.class;
        }
    }

    @Override public boolean isCellEditable(int r, int c) { return false; }

    @Override public Object getValueAt(int r, int c) {
        Entry e = rows.get(r);
        switch (c) {
            case 0: return e.yyyymm;
            case 1: return e.user.getId();
            case 2: return e.user.getName();
            case 3: return e.user.getSurname();
            case 4: return e.user.getMonthlyFeeCents();
            case 5: return e.user.getIban();
            default: return null;
        }
    }

    public void addAll(String yyyymm, List<User> users) {
        boolean added = false;
        for (User u : users) {
            if (u == null || u.getId() == null) continue;
            if (!contains(yyyymm, u.getId())) {
                rows.add(new Entry(yyyymm, u));
                added = true;
            }
        }
        if (added) fireTableDataChanged();
    }

    public boolean contains(String yyyymm, Integer userId) {
        for (Entry e : rows) {
            if (e.user.getId().equals(userId) && e.yyyymm.equals(yyyymm)) return true;
        }
        return false;
    }

    public void removeRows(int[] viewRows, javax.swing.JTable table) {
        if (viewRows == null || viewRows.length == 0) return;
        int[] modelRows = new int[viewRows.length];
        for (int i = 0; i < viewRows.length; i++) {
            modelRows[i] = table.convertRowIndexToModel(viewRows[i]);
        }
        Arrays.sort(modelRows);
        for (int i = modelRows.length - 1; i >= 0; i--) {
            int idx = modelRows[i];
            if (idx >= 0 && idx < rows.size()) rows.remove(idx);
        }
        fireTableDataChanged();
    }

    public void clear() {
        rows.clear();
        fireTableDataChanged();
    }

    public int size() { return rows.size(); }

    public Map<String, List<Integer>> groupByMonthUserIds() {
        Map<String, List<Integer>> map = new LinkedHashMap<>();
        for (Entry e : rows) {
            List<Integer> ids = map.get(e.yyyymm);
            if (ids == null) {
                ids = new ArrayList<>();
                map.put(e.yyyymm, ids);
            }
            ids.add(e.user.getId());
        }
        return map;
    }
}
