package ips.club.controller;

import ips.club.model.Assembly;
import ips.club.model.AssemblyStatus;
import ips.club.model.AssemblyType;
import ips.club.service.AssemblyService;

import java.time.LocalDateTime;
import java.util.List;

public class AssemblyController {

    private final AssemblyService service;

    public AssemblyController() {
        this.service = new AssemblyService();
    }

    public AssemblyController(AssemblyService service) {
        this.service = service;
    }

    public Assembly createScheduled(String title, String description, LocalDateTime scheduledAt, AssemblyType type) {
        return service.createScheduled(title, description, scheduledAt, LocalDateTime.now(), type);
    }

    public Assembly attachMinutesAndMarkWaiting(int assemblyId, String minutesText) {
        return service.attachMinutesAndMarkWaiting(assemblyId, minutesText);
    }

    public Assembly approveAndFinish(int assemblyId) {
        return service.approveAndFinish(assemblyId);
    }

    public Assembly get(int id) { return service.get(id); }

    public List<Assembly> listAll() { return service.listAll(); }

    public List<Assembly> listScheduled() { return service.listScheduled(); }

    public List<Assembly> listWaiting() { return service.listWaiting(); }

    public List<Assembly> listFinished() { return service.listFinished(); }

    public List<Assembly> listFiltered(AssemblyStatus status, LocalDateTime from, LocalDateTime to) {
        return service.listFiltered(status, from, to);
    }
}
