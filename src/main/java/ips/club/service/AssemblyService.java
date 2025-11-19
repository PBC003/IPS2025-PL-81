package ips.club.service;

import ips.club.dao.AssemblyDao;
import ips.club.model.Assembly;
import ips.club.model.AssemblyStatus;
import ips.club.model.AssemblyType;
import ips.club.model.MinutesStatus;
import ips.util.ApplicationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Assembly createScheduled(String title,String description,LocalDateTime scheduledAt,LocalDateTime now,AssemblyType type) {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(scheduledAt, "scheduledAt");
        Objects.requireNonNull(type, "type");
        if (now == null) {now = LocalDateTime.now();}
        if (type == AssemblyType.ORDINARY && dao.existsOrdinaryInYear(scheduledAt.getYear())) {
            throw new ApplicationException("Ya existe una asamblea ordinaria para " + scheduledAt.getYear());
        }
        Assembly a = new Assembly(
                null,
                title.trim(),
                (description == null || description.trim().isEmpty()) ? null : description.trim(),
                scheduledAt,
                now,
                AssemblyStatus.NOT_HELD,
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

    public List<Assembly> listAll() {return dao.findAll(); }

    public List<Assembly> listScheduled() {
        List<Assembly> result = new ArrayList<Assembly>();
        List<Assembly> all = dao.findAll();
        for (Assembly a : all) {
            if (a.getMinutesStatus() == MinutesStatus.PENDING_UPLOAD) {
                result.add(a);
            }
        }
        return result;
    }

    public List<Assembly> listWaiting() {
        List<Assembly> result = new ArrayList<Assembly>();
        List<Assembly> all = dao.findAll();
        for (Assembly a : all) {
            if (a.getMinutesStatus() == MinutesStatus.UPLOADED) {
                result.add(a);
            }
        }
        return result;
    }

    public List<Assembly> listFinished() {
        List<Assembly> result = new ArrayList<Assembly>();
        List<Assembly> all = dao.findAll();
        for (Assembly a : all) {
            if (a.getMinutesStatus() == MinutesStatus.APPROVED) {
                result.add(a);
            }
        }
        return result;
    }

    public List<Assembly> listFiltered(AssemblyStatus status, LocalDateTime from, LocalDateTime to) {
        return dao.findFiltered(status, from, to);
    }
}
