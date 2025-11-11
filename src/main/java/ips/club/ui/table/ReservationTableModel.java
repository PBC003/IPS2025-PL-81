package ips.club.ui.table;

import ips.club.model.Reservation;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.Collections;

public class ReservationTableModel extends AbstractTableModel {
    private final String[] cols = {"ID", "UserId", "Instalación", "Inicio", "Fin", "Min"};
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final List<Reservation> data = new ArrayList<>();

    private Map<Integer, String> locationNames = Collections.emptyMap();

    public void setLocationNames(Map<Integer, String> names) {
        this.locationNames = (names != null) ? names : Collections.emptyMap();
        fireTableDataChanged();
    }

    public void setData(List<Reservation> list) {
        data.clear();
        if (list != null) data.addAll(list);
        fireTableDataChanged();
    }

    public Reservation getAt(int row) {
        return (row >= 0 && row < data.size()) ? data.get(row) : null;
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Reservation r = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return r.getId();
            case 1: return r.getUserId();
            case 2: {
                String name = locationNames.get(r.getLocationId());
                return (name != null) ? name : ("#" + r.getLocationId());
            }
            case 3: return r.getStart().format(fmt);
            case 4: return r.getEnd().format(fmt);
            case 5: return r.getMinutes();
            default: return "";
        }
    }
}

