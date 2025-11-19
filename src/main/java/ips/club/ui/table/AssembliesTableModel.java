package ips.club.ui.table;

import ips.club.model.Assembly;
import ips.club.model.AssemblyStatus;
import ips.club.model.MinutesStatus;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AssembliesTableModel extends AbstractTableModel {

    private final List<Assembly> rows = new ArrayList<>();
    private static final String[] COLUMNS = {"Id","Título","Fecha","Tipo","Estado asamblea","Estado acta"};
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setRows(List<Assembly> list) {
        rows.clear();
        if (list != null) {
            rows.addAll(list);
        }
        fireTableDataChanged();
    }

    public Assembly getAssemblyAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return null;
        }
        return rows.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Assembly a = rows.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return a.getId();
            case 1:
                return a.getTitle();
            case 2:
                return a.getScheduledAt() == null ? "" : DF.format(a.getScheduledAt());
            case 3:
                return a.getType() == null ? "" : a.getType().name();
            case 4:
                return formatAssemblyStatus(a.getStatus());
            case 5:
                return formatMinutesStatus(a.getMinutesStatus());
            default:
                return "";
        }
    }

    private String formatAssemblyStatus(AssemblyStatus status) {
        if (status == null) {
            return "";
        }
        if (status == AssemblyStatus.NOT_HELD) {
            return "No realizada";
        }
        if (status == AssemblyStatus.HELD) {
            return "Realizada";
        }
        return status.name();
    }

    private String formatMinutesStatus(MinutesStatus status) {
        if (status == null) {
            return "";
        }
        if (status == MinutesStatus.PENDING_UPLOAD) {
            return "Pendiente de subida";
        }
        if (status == MinutesStatus.UPLOADED) {
            return "Subida";
        }
        if (status == MinutesStatus.APPROVED) {
            return "Aprobada";
        }
        return status.name();
    }
}
