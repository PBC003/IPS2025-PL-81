package ips.club.controller;

import ips.club.dao.LocationDao;
import ips.club.model.Location;
import java.util.List;

public class LocationsController {
    private final LocationDao dao = new LocationDao();
    public List<Location> loadLocations() { return dao.findAll(); }
}
