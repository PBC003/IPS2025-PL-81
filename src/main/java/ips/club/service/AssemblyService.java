package ips.club.service;

import ips.club.dao.AssemblyDao;
import ips.club.model.Assembly;
import ips.club.model.AssemblyStatus;
import ips.club.model.AssemblyType;
import ips.util.ApplicationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class AssemblyService {

    private final AssemblyDao dao;

    public AssemblyService() {
        this.dao = new AssemblyDao();
    }

    public AssemblyService(AssemblyDao dao) {
        this.dao = dao;
    }

    public Assembly createScheduled(String title,
            String description,
            LocalDateTime scheduledAt,
            LocalDateTime now,
            AssemblyType type) {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(scheduledAt, "scheduledAt");
        Objects.requireNonNull(type, "type");
        if (now == null)
            now = LocalDateTime.now();
        if (type == AssemblyType.ORDINARY && dao.existsOrdinaryInYear(scheduledAt.getYear())) {
            throw new ApplicationException("Ya existe una asamblea ordinaria para " + scheduledAt.getYear());
        }
        Assembly a = new Assembly(
                null,
                title.trim(),
                (description == null || description.trim().isEmpty()) ? null : description.trim(),
                scheduledAt,
                now,
                AssemblyStatus.SCHEDULED,
                type,
                null);
        return dao.insert(a);
    }

    public Assembly attachMinutesAndMarkWaiting(int assemblyId, String minutesText) {
        if (minutesText == null || minutesText.trim().isEmpty()) {
            throw new IllegalArgumentException("El acta no puede estar vacía");
        }
        boolean ok = dao.updateMinutesAndMarkWaiting(assemblyId, minutesText.trim());
        if (!ok) return null;
        return dao.findById(assemblyId);
    }

    public Assembly approveAndFinish(int assemblyId) {
        boolean ok = dao.markFinished(assemblyId);
        if (!ok) return null;
        return dao.findById(assemblyId);
    }

    public Assembly get(int id) { return dao.findById(id); }

    public List<Assembly> listAll() {
        return dao.findAll();
    }

    public List<Assembly> listScheduled() {
        return dao.findByStatus(AssemblyStatus.SCHEDULED);
    }

    public List<Assembly> listWaiting() {
        return dao.findByStatus(AssemblyStatus.WAITING);
    }

    public List<Assembly> listFinished() {
        return dao.findByStatus(AssemblyStatus.FINISHED);
    }

    public List<Assembly> listFiltered(AssemblyStatus status, LocalDateTime from, LocalDateTime to) {
        return dao.findFiltered(status, from, to);
    }
}
