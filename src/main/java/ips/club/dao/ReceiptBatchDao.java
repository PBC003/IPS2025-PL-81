package ips.club.dao;

import ips.club.model.ReceiptBatch;
import ips.club.model.ReceiptBatchStatus;
import ips.club.model.ReceiptStatus;
import ips.util.ApplicationException;
import ips.util.Database;

import java.sql.*;
import java.time.LocalDateTime;

public class ReceiptBatchDao {

    private static final String SQL_INSERT =
        "INSERT INTO Receipt_batch (charge_month, bank_entity, created_at, status, file_name, total_amount, receipts_cnt) " +
        "VALUES (?, ?, ?, ?, ?, 0, 0)";

    private static final String SQL_FIND_BY_ID =
        "SELECT id, charge_month, bank_entity, created_at, status, file_name, total_amount, receipts_cnt " +
        "FROM Receipt_batch WHERE id = ?";

    private static final String SQL_MARK_EXPORTED =
        "UPDATE Receipt_batch SET status = ?, file_name = ? WHERE id = ?";


    public ReceiptBatch insert(ReceiptBatch b) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, b.getChargeMonth());
            ps.setString(2, b.getBankEntity());
            ps.setString(3, (b.getCreatedAt() != null ? b.getCreatedAt() : LocalDateTime.now()).toString());
            ps.setString(4, (b.getStatus() != null ? b.getStatus() : ReceiptBatchStatus.GENERATED).name());
            ps.setString(5, (b.getFileName() != null ? b.getFileName() : "Lote Generado - "+ LocalDateTime.now().toString()));

            if (ps.executeUpdate() != 1) throw new ApplicationException("No se insertó el lote.");
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) b.setId(rs.getInt(1)); }
            return b;
        } catch (SQLException e) {
            throw new ApplicationException("Error al insertar lote");
        }
    }

    public ReceiptBatch findById(int id) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Error al buscar lote");
        }
    }

    public void updateTotalsAndMarkExported(int batchId, String fileName) {
        final String SQL_SUM_COUNT =
            "SELECT COALESCE(SUM(amount_cents),0) AS total_amount, COUNT(*) AS cnt " +
            "FROM Receipt WHERE batch_id = ?";

        final String SQL_UPDATE_BATCH =
            "UPDATE Receipt_batch SET status = ?, file_name = ?, total_amount = ?, receipts_cnt = ? WHERE id = ?";

        final String SQL_UPDATE_RECEIPTS =
            "UPDATE Receipt SET status = ? WHERE batch_id = ?";

        Database db = new Database();
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int total = 0, cnt = 0;
                try (PreparedStatement ps = conn.prepareStatement(SQL_SUM_COUNT)) {
                    ps.setInt(1, batchId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            total = rs.getInt("total_amount");
                            cnt = rs.getInt("cnt");
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_BATCH)) {
                    ps.setString(1, ReceiptBatchStatus.EXPORTED.name());
                    ps.setString(2, fileName);
                    ps.setInt(3, total);
                    ps.setInt(4, cnt);
                    ps.setInt(5, batchId);
                    if (ps.executeUpdate() != 1) {
                        throw new ApplicationException("No se pudo actualizar el lote " + batchId);
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_RECEIPTS)) {
                    ps.setString(1, ReceiptStatus.PAID.name());
                    ps.setInt(2, batchId);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                if (ex instanceof ApplicationException) throw (ApplicationException) ex;
                throw new ApplicationException("Error al marcar exportado el lote " + batchId);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Conexión fallida al marcar exportado el lote " + batchId);
        }
    }


    public void markExported(int batchId, String fileName) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_MARK_EXPORTED)) {
            ps.setString(1, ReceiptBatchStatus.EXPORTED.name());
            ps.setString(2, fileName);
            ps.setInt(3, batchId);
            if (ps.executeUpdate() != 1) throw new ApplicationException("No se marcó exportado el lote " + batchId);
        } catch (SQLException e) {
            throw new ApplicationException("Error al marcar lote exportado");
        }
    }

    private ReceiptBatch map(ResultSet rs) throws SQLException {
        return new ReceiptBatch(
            rs.getInt("id"),
            rs.getString("charge_month"),
            rs.getString("bank_entity"),
            LocalDateTime.parse(rs.getString("created_at")),
            ReceiptBatchStatus.valueOf(rs.getString("status")),
            rs.getString("file_name"),
            rs.getInt("total_amount"),
            rs.getInt("receipts_cnt")
        );
    }
}
