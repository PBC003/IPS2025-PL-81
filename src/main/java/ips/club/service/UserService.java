package ips.club.service;

import java.util.List;

import ips.club.dao.UserDao;
import ips.club.model.User;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> findUsersWithoutReceiptFor(String currentYearMonth) {
        return userDao.findAllWithoutReceiptFor(currentYearMonth);
    }
}
