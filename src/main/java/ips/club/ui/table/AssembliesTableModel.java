package ips.club.ui.table;

import ips.club.model.Assembly;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AssembliesTableModel extends AbstractTableModel {

    private final String[] columns = {"ID", "Título", "Programada", "Estado", "Acta", "Creada"};
    private final List<Assembly> rows = new ArrayList<>();
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Integer.class;
            case 1: return String.class;
            case 2: return String.class;
            case 3: return String.class;
            case 4: return Boolean.class;
            case 5: return String.class;
            default: return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Assembly a = rows.get(rowIndex);
        switch (columnIndex) {
            case 0: return a.getId();
            case 1: return a.getTitle();
            case 2: return fmt(a.getScheduledAt());
            case 3: return a.getStatus() != null ? a.getStatus().name() : "";
            case 4: return a.getMinutesText() != null && !a.getMinutesText().isEmpty();
            case 5: return fmt(a.getCreatedAt());
            default: return null;
        }
    }

    private String fmt(LocalDateTime dt) {
        return dt == null ? "" : dt.withNano(0).format(DF);
    }

    public void setRows(List<Assembly> list) {
        rows.clear();
        if (list != null) rows.addAll(list);
        fireTableDataChanged();
    }

    public Assembly getAssemblyAt(int viewRow) {
        if (viewRow < 0 || viewRow >= rows.size()) return null;
        return rows.get(viewRow);
    }

    public void addOrReplace(Assembly updated) {
        if (updated == null) return;
        for (int i = 0; i < rows.size(); i++) {
            Assembly a = rows.get(i);
            if (a.getId() != null && a.getId().equals(updated.getId())) {
                rows.set(i, updated);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
        rows.add(0, updated);
        fireTableRowsInserted(0, 0);
    }
}
