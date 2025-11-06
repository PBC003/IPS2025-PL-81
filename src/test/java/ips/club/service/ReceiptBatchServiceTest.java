package ips.club.service;

import ips.club.dao.ReceiptBatchDao;
import ips.club.dao.ReceiptDao;
import ips.club.model.Receipt;
import ips.club.model.ReceiptBatch;
import ips.club.model.ReceiptBatchStatus;
import ips.util.ApplicationException;
import ips.util.DatabaseTest;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class ReceiptBatchServiceTest extends DatabaseTest {

    @Test
    public void createBatch_validatesAndInserts() {
        ReceiptBatchService svc = new ReceiptBatchService();
        ReceiptBatch created = svc.createBatch(new ReceiptBatch(
                null, "202511", "MyBank", LocalDateTime.now(), ReceiptBatchStatus.GENERATED, "batch_nov_2025.csv", 0, 0
        ));
        assertNotNull(created.getId());
        assertEquals("202511", created.getChargeMonth());
    }

    @Test(expected = ApplicationException.class)
    public void createBatch_missingChargeMonth_rejected() {
        new ReceiptBatchService().createBatch(new ReceiptBatch(
                null, "", "MyBank", LocalDateTime.now(), ReceiptBatchStatus.GENERATED, "file.csv", 0, 0
        ));
    }

    @Test
    public void fillBatchWithUnbatchedReceipts_assignsAllForMonth() {
        ReceiptBatchDao bdao = new ReceiptBatchDao();
        ReceiptDao rdao = new ReceiptDao();
        ReceiptBatchService svc = new ReceiptBatchService();

        ReceiptBatch batch = bdao.insert(new ReceiptBatch(
                null, "202511", "BankX", LocalDateTime.now(), ReceiptBatchStatus.GENERATED, "filled.csv", 0, 0
        ));

        List<Receipt> before = rdao.listByMonthNotInBatch("202511");
        svc.fillBatchWithUnbatchedReceipts("202511", batch.getId());
        List<Receipt> after = rdao.listByMonthNotInBatch("202511");
        List<Receipt> inBatch = rdao.listByBatch(batch.getId());

        assertTrue(inBatch.size() >= before.size());
        assertTrue(after.size() <= before.size());
    }
}
