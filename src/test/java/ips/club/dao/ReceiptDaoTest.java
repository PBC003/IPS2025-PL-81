package ips.club.dao;

import ips.club.model.Receipt;
import ips.club.model.ReceiptStatus;
import ips.club.model.User;
import ips.util.ApplicationException;
import ips.util.Database;
import ips.util.DatabaseTest;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class ReceiptDaoTest extends DatabaseTest {

    @Test
    public void listByMonth_returnsSeededReceipts() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        List<Receipt> rs = new ReceiptDao().listByMonth("202511");
        assertNotNull(rs);
        assertFalse(rs.isEmpty());
        for (Receipt r : rs) {
            assertEquals("202511", r.getChargeMonth());
            assertNotNull(r.getReceiptNumber());
            assertTrue(r.getAmountCents() > 0);
        }
    }

    @Test
    public void insert_generatesNumberWithAGPrefixAndSeq() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();

        UserDao userDao = new UserDao();
        List<User> candidates = userDao.findAllWithoutReceiptFor("202511");
        assertFalse("Se necesita al menos un usuario sin recibo en 202511 en el seed", candidates.isEmpty());
        int userId = candidates.get(0).getId();

        ReceiptDao dao = new ReceiptDao();
        Receipt toInsert = new Receipt(
                null, null, userId, 1234,
                LocalDate.now(),
                LocalDate.now(),
                "202511",
                "Cuota test",
                ReceiptStatus.GENERATED,
                null
        );
        Receipt inserted = dao.insert(toInsert);
        assertNotNull(inserted.getId());
        assertNotNull(inserted.getReceiptNumber());
        assertTrue(Pattern.matches("AG-202511-\\d{3}", inserted.getReceiptNumber()));
    }

    @Test(expected = ApplicationException.class)
    public void insert_sameUserSameMonth_violatesUniqueConstraint() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        ReceiptDao dao = new ReceiptDao();
        List<Receipt> existing = dao.listByMonth("202511");
        assertFalse(existing.isEmpty());
        Receipt base = existing.get(0);

        Receipt dup = new Receipt(null, null, base.getUserId(), base.getAmountCents(),
                base.getIssueDate(), base.getValueDate(),
                base.getChargeMonth(), "Duplicado", base.getStatus(), null);

        dao.insert(dup);
    }

    @Test
    public void findUnbatchedAll_returnsNotInBatch() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        List<Receipt> rs = new ReceiptDao().findUnbatchedAll();
        assertNotNull(rs);
        assertTrue(rs.stream().allMatch(r -> r.getBatchId() == null || r.getBatchId() == 0));
    }
}
