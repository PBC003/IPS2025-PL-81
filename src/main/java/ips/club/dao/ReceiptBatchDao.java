package ips.club.dao;

import ips.club.model.ReceiptBatch;
import ips.club.model.ReceiptBatchStatus;
import ips.util.ApplicationException;
import ips.util.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReceiptBatchDao {

    private static final String SQL_INSERT = "INSERT INTO Receipt_batch (charge_month, bank_entity, created_at, status, file_name, total_amount, receipts_cnt) "
            +
            "VALUES (?, ?, ?, ?, ?, 0, 0)";

    private static final String SQL_FIND_BY_ID = "SELECT id, charge_month, bank_entity, created_at, status, file_name, total_amount, receipts_cnt "
            +
            "FROM Receipt_batch WHERE id = ?";

    private static final String SQL_MARK_EXPORTED = "UPDATE Receipt_batch SET status = ?, file_name = ? WHERE id = ?";

    private static final String SQL_FIND_ALL = "SELECT id, charge_month, bank_entity, created_at, status, file_name, total_amount, receipts_cnt "
            +
            "FROM Receipt_batch ORDER BY id DESC";

    private static final String SQL_MARK_CANCELED = "UPDATE Receipt_batch SET status='CANCELED' WHERE id=? AND status='GENERATED'";

    private final String SQL_SUM_COUNT = "SELECT COALESCE(SUM(amount_cents),0) AS total_amount, COUNT(*) AS cnt " +
            "FROM Receipt WHERE batch_id = ?";

    private final String SQL_UPDATE_BATCH = "UPDATE Receipt_batch SET status = ?, file_name = ?, total_amount = ?, receipts_cnt = ? WHERE id = ?";

    private static final String SQL_RECALC_TOTALS = "UPDATE Receipt_batch " +
            "SET receipts_cnt = (SELECT COUNT(*) FROM Receipt r WHERE r.batch_id = ?), " +
            "total_amount = (SELECT COALESCE(SUM(r.amount_cents),0) FROM Receipt r WHERE r.batch_id = ?) " +
            "WHERE id = ?";

    public ReceiptBatch insert(ReceiptBatch b) {
        Database db = new Database();
        try (Connection conn = db.getConnection()){
            PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, b.getChargeMonth());
            ps.setString(2, b.getBankEntity());
            ps.setString(3, (b.getCreatedAt() != null ? b.getCreatedAt() : LocalDateTime.now()).toString());
            ps.setString(4, (b.getStatus() != null ? b.getStatus() : ReceiptBatchStatus.GENERATED).name());
            ps.setString(5, (b.getFileName() != null ? b.getFileName() : "Lote Generado - " + LocalDateTime.now().toString()));

            if (ps.executeUpdate() != 1) throw new ApplicationException("No se insertó el lote.");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) b.setId(rs.getInt(1));
            }

            return b;
        } catch (SQLException e) {
            throw new ApplicationException("Error al insertar lote");
        }
    }

    public ReceiptBatch findById(int id) {
        Database db = new Database();
        try (Connection conn = db.getConnection()){
            PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID);
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())return null;
                return map(rs);
            }

        } catch (SQLException e) {
            throw new ApplicationException("Error al buscar lote");
        }
    }

    public List<ReceiptBatch> findAll() {
        Database db = new Database();
        try (Connection c = db.getConnection()){
            PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL);
            ResultSet rs = ps.executeQuery();
            ArrayList<ReceiptBatch> out = new ArrayList<>();

            while (rs.next()){
                out.add(map(rs));
            }

            return out;
        } catch (SQLException e) {
            throw new ApplicationException("Error listando lotes");
        }
    }

    public void updateTotalsAndMarkExported(int batchId, String fileName) {
        Database db = new Database();
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int total = 0;
                int cnt = 0;

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

                conn.commit();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Conexión fallida al marcar exportado el lote " + batchId);
        }
    }

    public void markExported(int batchId, String fileName) {
        Database db = new Database();
        try (Connection conn = db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(SQL_MARK_EXPORTED);
            ps.setString(1, ReceiptBatchStatus.EXPORTED.name());
            ps.setString(2, fileName);
            ps.setInt(3, batchId);
            if (ps.executeUpdate() != 1)throw new ApplicationException("No se marcó exportado el lote " + batchId);
        } catch (SQLException e) {
            throw new ApplicationException("Error al marcar lote exportado");
        }
    }

    public void markCanceled(int batchId) {
        Database db = new Database();
        try (Connection c = db.getConnection()){
            PreparedStatement ps = c.prepareStatement(SQL_MARK_CANCELED);
            ps.setInt(1, batchId);
            if (ps.executeUpdate() != 1) throw new ApplicationException("No se pudo cancelar (¿no está en estado GENERATED?)");
        } catch (SQLException e) {
            throw new ApplicationException("Error al cancelar lote " + batchId);
        }
    }

    public void recalcTotals(int batchId) throws ApplicationException {
        Database db = new Database();
        try (Connection c = db.getConnection()) {
            PreparedStatement ps = c.prepareStatement(SQL_RECALC_TOTALS);
            ps.setInt(1, batchId);
            ps.setInt(2, batchId);
            ps.setInt(3, batchId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new ApplicationException("Error recalculando totales del lote: " + e.getMessage());
        }
    }

    public void markProcessed(int batchId) {
        Database db = new Database();
        try (Connection conn = db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE Receipt_batch SET status = ? WHERE id = ?");
            ps.setString(1, ReceiptBatchStatus.PROCESSED.name());
            ps.setInt(2, batchId);
            if (ps.executeUpdate() != 1) {
                throw new ApplicationException("No se pudo marcar como procesado el lote " + batchId);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Error marcando como procesado el lote " + batchId + ": " + e.getMessage());
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
                rs.getInt("receipts_cnt"));
    }
}
