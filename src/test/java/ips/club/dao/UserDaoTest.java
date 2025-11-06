package ips.club.dao;

import ips.club.model.User;
import ips.util.Database;
import ips.util.DatabaseTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserDaoTest extends DatabaseTest {

    @Test
    public void findAll_returnsSeededUsers() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        List<User> users = new UserDao().findAll();
        assertNotNull(users);
        assertFalse(users.isEmpty());
        User u = users.get(0);
        assertNotNull(u.getId());
        assertNotNull(u.getName());
        assertNotNull(u.getSurname());
        assertNotNull(u.getIban());
        assertTrue(u.getMonthlyFeeCents() > 0);
    }

    @Test
    public void findBasicById_returnsExisting() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        User u = new UserDao().findBasicById(1);
        assertNotNull(u);
        assertEquals(Integer.valueOf(1), u.getId());
        assertNotNull(u.getName());
    }

    @Test
    public void findAllWithoutReceiptFor_filters() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        List<User> missing = new UserDao().findAllWithoutReceiptFor("202511");
        assertNotNull(missing);
        for (User u : missing) {
            assertNotNull(u.getId());
            assertTrue(u.getMonthlyFeeCents() > 0);
        }
    }
}
