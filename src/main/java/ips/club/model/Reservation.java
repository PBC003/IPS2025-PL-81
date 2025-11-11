package ips.club.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Reservation {
    public static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private Integer id;
    private int userId;
    private int locationId;
    private LocalDateTime start;
    private LocalDateTime end;
    private int minutes;
    private LocalDateTime createdAt;

    public Reservation() {}

    public static Reservation of(int userId, int locationId, LocalDateTime start, int minutes) {
        Reservation r = new Reservation();
        r.userId = userId;
        r.locationId = locationId;
        r.start = start;
        r.minutes = minutes;
        r.end = start.plusMinutes(minutes);
        r.createdAt = LocalDateTime.now();
        return r;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }

    public int getMinutes() { return minutes; }
    public void setMinutes(int minutes) { this.minutes = minutes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String startAsText() { return start.format(FMT); }
    public String endAsText() { return end.format(FMT); }
    public String createdAtAsText() { return createdAt.format(FMT); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode() { return Objects.hash(id); }

    @Override public String toString() {
        return "Reservation{id=" + id +
                ", userId=" + userId +
                ", locationId=" + locationId +
                ", start=" + start +
                ", end=" + end +
                ", minutes=" + minutes +
                '}';
    }
}
