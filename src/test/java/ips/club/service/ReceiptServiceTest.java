package ips.club.service;

import ips.club.model.Receipt;
import ips.util.ApplicationException;
import ips.util.Database;
import ips.util.DatabaseTest;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class ReceiptServiceTest extends DatabaseTest {

    @Test
    public void createMonthlyReceipt_withConceptNull_setsDefaultConcept() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        ReceiptService svc = new ReceiptService();
        Receipt r = svc.createMonthlyReceipt(1, 1200, LocalDate.now(), "202512", null);
        assertNotNull(r);
        assertTrue(r.getConcept().toLowerCase().contains("cuota club"));
        assertEquals("202512", r.getChargeMonth());
    }

    @Test(expected = ApplicationException.class)
    public void createMonthlyReceipt_invalidUser_rejected() {
        new ReceiptService().createMonthlyReceipt(0, 1200, LocalDate.of(2025, 11, 10), "202511", "x");
    }

    @Test(expected = ApplicationException.class)
    public void createMonthlyReceipt_negativeAmount_rejected() {
        new ReceiptService().createMonthlyReceipt(1, -1, LocalDate.of(2025, 11, 10), "202511", "x");
    }
}
