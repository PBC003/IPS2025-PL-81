package ips.club.controller;

import ips.club.dao.LocationDao;
import ips.club.dao.UserDao;
import ips.club.model.Reservation;
import ips.club.model.User;
import ips.club.model.Location;
import ips.club.service.ReservationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationController {
    private final ReservationService service = new ReservationService();
    private final LocationDao locationDao = new LocationDao();
    private final UserDao userDao = new UserDao();

    public List<Reservation> listReservations(Integer locationId) {
        return service.listReservations(locationId);
    }

    public Reservation createReservation(int userId, int locationId, LocalDate date, LocalTime startTime, int minutes) {
        return service.createReservation(userId, locationId, date, startTime, minutes);
    }

    public List<Location> findAllLocations() {
        return locationDao.findAll();
    }

    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    public List<LocalTime> computeAvailableStartHours(int userId, int locationId, LocalDate day, int minutes) {
        return service.computeAvailableStartHours(userId, locationId, day, minutes);
    }
}
