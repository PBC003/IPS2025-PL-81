package ips.club.service;

import ips.club.dao.ReservationDao;
import ips.club.model.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    private final ReservationDao reservationDao = new ReservationDao();

    public Reservation createReservation(int userId, int locationId, LocalDate date, LocalTime startTime, int minutes) {
        LocalDateTime start = LocalDateTime.of(date, startTime);
        return createReservation(userId, locationId, start, minutes);
    }

    public Reservation createReservation(int userId, int locationId, LocalDateTime start, int minutes) {
        if (minutes != 60 && minutes != 120) {
            throw new IllegalArgumentException("La duración debe ser de 60 o 120 minutos.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.plusHours(72);

        if (start.isBefore(now)) {
            throw new IllegalArgumentException("La hora de inicio no puede estar en el pasado.");
        }

        LocalDateTime end = start.plusMinutes(minutes);

        if (start.isAfter(limit) || end.isAfter(limit)) {
            throw new IllegalArgumentException("La reserva debe estar íntegramente dentro de las próximas 72 horas.");
        }

        LocalDate day = start.toLocalDate();
        int already = reservationDao.sumMinutesUserAtLocationOn(day, userId, locationId);

        if (already + minutes > 120) {
            throw new IllegalArgumentException("Excede el máximo de 120 minutos en el mismo día para esta instalación.");
        }

        boolean overlapsAtLocation = !reservationDao.findOverlapsAtLocation(locationId, start, end).isEmpty();

        if (overlapsAtLocation) {
            throw new IllegalArgumentException("Existe otra reserva en esa instalación que se solapa con el horario elegido.");
        }

        boolean overlapsForUser = !reservationDao.findOverlapsForUser(userId, start, end).isEmpty();

        if (overlapsForUser) {
            throw new IllegalArgumentException("Tienes otra reserva que se solapa en ese intervalo.");
        }

        Reservation r = Reservation.of(userId, locationId, start, minutes);
        reservationDao.insert(r);
        return r;
    }

    public List<Reservation> listReservations(Integer locationId) {
        if (locationId == null) return reservationDao.listAll();
        return reservationDao.listByLocation(locationId);
    }

    public boolean isAvailableAtLocation(int locationId, LocalDateTime start, int minutes) {
        LocalDateTime end = start.plusMinutes(minutes);
        return reservationDao.findOverlapsAtLocation(locationId, start, end).isEmpty();
    }

    public List<LocalTime> computeAvailableStartHours(int userId, int locationId, LocalDate day, int minutes) {
        List<LocalTime> hours = new ArrayList<>();
        if (minutes != 60 && minutes != 120) return hours;

        LocalDateTime now   = LocalDateTime.now();
        LocalDateTime limit = now.plusHours(72);

        int already = reservationDao.sumMinutesUserAtLocationOn(day, userId, locationId);
        if (already + minutes > 120) return hours;

        for (int h = 0; h < 24; h++) {
            LocalDateTime start = day.atTime(LocalTime.of(h, 0));
            LocalDateTime end   = start.plusMinutes(minutes);

            if (start.isBefore(now) || end.isAfter(limit)) continue;
            if (!reservationDao.findOverlapsAtLocation(locationId, start, end).isEmpty()) continue;
            if (!reservationDao.findOverlapsForUser(userId, start, end).isEmpty()) continue;

            hours.add(LocalTime.of(h, 0));
        }
        return hours;
    }
}
