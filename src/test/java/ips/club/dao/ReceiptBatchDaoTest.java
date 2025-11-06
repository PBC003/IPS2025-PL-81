package ips.club.dao;

import ips.club.model.ReceiptBatch;
import ips.util.Database;
import ips.util.DatabaseTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ReceiptBatchDaoTest extends DatabaseTest {

    @Test
    public void findAll_returnsSeededBatches() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        List<ReceiptBatch> batches = new ReceiptBatchDao().findAll();
        assertNotNull(batches);
        assertFalse(batches.isEmpty());
        ReceiptBatch b = batches.get(0);
        assertNotNull(b.getId());
        assertNotNull(b.getChargeMonth());
        assertNotNull(b.getStatus());
        assertNotNull(b.getFileName());
    }

    @Test
    public void recalcTotals_updatesAggregates() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        ReceiptBatchDao dao = new ReceiptBatchDao();
        List<ReceiptBatch> batches = dao.findAll();
        ReceiptBatch any = batches.get(0);

        dao.recalcTotals(any.getId());

        ReceiptBatch refreshed = dao.findById(any.getId());
        assertNotNull(refreshed.getReceiptsCnt());
        assertNotNull(refreshed.getTotalAmount());
        assertTrue(refreshed.getReceiptsCnt() >= 0);
        assertTrue(refreshed.getTotalAmount() >= 0);
    }
}
