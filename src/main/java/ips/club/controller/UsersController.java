package ips.club.controller;

import ips.club.dao.UserDao;
import ips.club.model.User;
import ips.club.service.UserService;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UsersController {
    private final UserDao dao = new UserDao();
    private final UserService userService = new UserService(dao);

    public List<User> loadUsers() {
        return dao.findAll();
    }
    public List<User> findEligibleUsersFor(String period) {
        return userService.findUsersWithoutReceiptFor(period);
    }

    public List<User> findEligibleUsersForThisMonth() {
        String currentYearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        return userService.findUsersWithoutReceiptFor(currentYearMonth);
    }
}
