package ips.club.controller;

import ips.club.dao.LocationDao;
import ips.club.dao.UserDao;
import ips.club.model.Reservation;
import ips.club.model.User;
import ips.club.model.WeatherForecast;
import ips.club.model.Location;
import ips.club.model.WeatherPolicy;
import ips.club.service.ReservationService;
import ips.club.service.WeatherService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationController {

    private final ReservationService service = new ReservationService();
    private final LocationDao locationDao = new LocationDao();
    private final UserDao userDao = new UserDao();
    private final WeatherService weatherService = new WeatherService();

    public List<Reservation> listReservations(Integer locationId) {
        return service.listReservations(locationId);
    }

    public Reservation createReservation(int userId, int locationId, LocalDate date, LocalTime startTime, int minutes) {
        Location location = locationDao.findById(locationId);
        if (location == null) {throw new IllegalArgumentException("La instalación seleccionada no existe.");}
        WeatherForecast forecast = weatherService.getDailyForecast(date);
        if (!WeatherPolicy.isSuitableForLocation(location, forecast)) {throw new IllegalArgumentException("No se pueden realizar reservas por las condiciones meteorológicas.");}
        return service.createReservation(userId, locationId, date, startTime, minutes);
    }

    public List<Location> findAllLocations() {return locationDao.findAll();}

    public List<User> findAllUsers() {return userDao.findAll();}

    public List<LocalTime> computeAvailableStartHours(int userId, int locationId, LocalDate day, int minutes) {
        return service.computeAvailableStartHours(userId, locationId, day, minutes);
    }

    public WeatherForecast getDailyForecast(LocalDate day) {return weatherService.getDailyForecast(day);}
}
