package com.example.shift_planner_backend.services;

import com.example.shift_planner_backend.dto.request.ShiftDTO;
import com.example.shift_planner_backend.models.Shift;
import com.example.shift_planner_backend.repositories.ShiftRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public List<Shift> getActiveShifts() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        Date startOfToday = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return shiftRepository.findByDateGreaterThanEqualAndIsAvailableTrue(startOfToday);
    }

    public Shift createShift(ShiftDTO shiftDTO) {
        if (shiftDTO == null || shiftDTO.getDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is required");
        }

        LocalDate shiftDate = shiftDTO.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        if (shiftDate.isBefore(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shift date cannot be in the past");
        }

        Shift shift = new Shift();
        shift.setDate(shiftDTO.getDate());
        shift.setStartTime(shiftDTO.getStartTime());
        shift.setEndTime(shiftDTO.getEndTime());
        shift.setHours(shiftDTO.getHours());
        shift.setIsAvailable(true);

        return shiftRepository.save(shift);
    }

    public void deleteShift(Long shiftId) {
        shiftRepository.deleteById(shiftId);
    }
}
