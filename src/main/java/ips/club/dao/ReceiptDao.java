package ips.club.dao;

import ips.club.model.Receipt;
import ips.club.model.ReceiptStatus;
import ips.util.ApplicationException;
import ips.util.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReceiptDao {

    private static final String SQL_INSERT =
        "INSERT INTO Receipt (receipt_number, user_id, amount_cents, issue_date, value_date, charge_month, concept, status, batch_id) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL)";

    private static final String SQL_NEXT_SEQ_FOR_MONTH =
        "SELECT COUNT(*) + 1 FROM Receipt WHERE charge_month = ?";

    private static final String SQL_LIST_BY_MONTH_NOT_IN_BATCH =
        "SELECT id, receipt_number, user_id, amount_cents, issue_date, value_date, charge_month, concept, status, batch_id " +
        "FROM Receipt WHERE charge_month = ? AND batch_id IS NULL ORDER BY id";

    private static final String SQL_ASSIGN_TO_BATCH =
        "UPDATE Receipt SET batch_id = ? WHERE id = ?";

    private static final String SQL_LIST_BY_BATCH =
        "SELECT id, receipt_number, user_id, amount_cents, issue_date, value_date, charge_month, concept, status, batch_id " +
        "FROM Receipt WHERE batch_id = ? ORDER BY id";

    private static final String PREFIX = "AG";

    public Receipt insert(Receipt r) {
        Database db = new Database();
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String number = r.getReceiptNumber();
                if (number == null || number.trim().isEmpty()) {
                    number = generateNumber(conn, r.getChargeMonth());
                }

                try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, number);
                    ps.setInt(2, r.getUserId());
                    ps.setInt(3, r.getAmountCents());
                    ps.setString(4, r.getIssueDate().toString());
                    ps.setString(5, r.getValueDate().toString());
                    ps.setString(6, r.getChargeMonth());
                    ps.setString(7, r.getConcept());
                    ps.setString(8, r.getStatus().name());

                    if (ps.executeUpdate() != 1) throw new ApplicationException("No se insertó el recibo.");
                    Integer id = null;
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) id = rs.getInt(1); }

                    conn.commit();
                    return new Receipt(id, number, r.getUserId(), r.getAmountCents(),
                            r.getIssueDate(), r.getValueDate(), r.getChargeMonth(),
                            r.getConcept(), r.getStatus(), null);
                }
            } catch (Exception ex) {
                conn.rollback();
                throw new ApplicationException("Error al insertar recibo");
            } finally { conn.setAutoCommit(true); }
        } catch (SQLException e) {
            throw new ApplicationException("Conexión fallida al insertar recibo");
        }
    }

    public List<Receipt> listByMonthNotInBatch(String chargeMonth) {
        Database db = new Database();
        List<Receipt> out = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_LIST_BY_MONTH_NOT_IN_BATCH)) {
            ps.setString(1, chargeMonth);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new ApplicationException("Error al listar recibos del mes sin lote");
        }
    }

    public void assignToBatch(int receiptId, int batchId) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_ASSIGN_TO_BATCH)) {
            ps.setInt(1, batchId);
            ps.setInt(2, receiptId);
            if (ps.executeUpdate() != 1) throw new ApplicationException("No se asignó el recibo " + receiptId);
        } catch (SQLException e) {
            throw new ApplicationException("Error al asignar recibo a lote");
        }
    }

    public List<Receipt> listByBatch(int batchId) {
        Database db = new Database();
        List<Receipt> out = new ArrayList<>();
        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_LIST_BY_BATCH)) {
            ps.setInt(1, batchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new ApplicationException("Error al listar recibos del lote " + batchId);
        }
    }

    private String generateNumber(Connection conn, String chargeMonth) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_NEXT_SEQ_FOR_MONTH)) {
            ps.setString(1, chargeMonth);
            try (ResultSet rs = ps.executeQuery()) {
                int seq = rs.next() ? rs.getInt(1) : 1;
                return PREFIX + "-" + chargeMonth + "-" + String.format("%03d", seq);
            }
        }
    }

    private Receipt map(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String num = rs.getString("receipt_number");
        int userId = rs.getInt("user_id");
        int amount = rs.getInt("amount_cents");
        LocalDate issue = LocalDate.parse(rs.getString("issue_date"));
        LocalDate value = LocalDate.parse(rs.getString("value_date"));
        String month = rs.getString("charge_month");
        String concept = rs.getString("concept");
        ReceiptStatus st = ReceiptStatus.valueOf(rs.getString("status"));
        Integer batchId = rs.getInt("batch_id"); if (rs.wasNull()) batchId = null;

        return new Receipt(id, num, userId, amount, issue, value, month, concept, st, batchId);
    }
}
