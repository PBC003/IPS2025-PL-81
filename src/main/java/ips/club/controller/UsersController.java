package ips.club.controller;

import ips.club.dao.UserDao;
import ips.club.model.User;
import java.util.List;

public class UsersController {
    private final UserDao dao = new UserDao();

    public List<User> loadUsers() {
        return dao.findAll();
    }
}
