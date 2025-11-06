package ips.club.service;

import ips.club.dao.UserDao;
import ips.club.model.User;
import ips.util.DatabaseTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserServiceTest extends DatabaseTest {

    @Test
    public void findUsersWithoutReceiptFor_delegatesAndReturnsList() {
        UserService svc = new UserService(new UserDao());
        List<User> missing = svc.findUsersWithoutReceiptFor("202511");
        assertNotNull(missing);
    }
}
