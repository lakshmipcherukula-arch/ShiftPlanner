package com.example.shift_planner_backend.services;

import com.example.shift_planner_backend.dto.request.ShiftDTO;
import com.example.shift_planner_backend.models.Shift;
import com.example.shift_planner_backend.repositories.ShiftRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public List<Shift> getActiveShifts(String type) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        List<Shift> shifts = shiftRepository.findByDateGreaterThanEqualAndIsAvailableTrue(today);
        if (type == null || type.isBlank() || type.equalsIgnoreCase("all")) {
            return shifts;
        }
        List<Shift> filteredShifts = new ArrayList<>();
        for (Shift shift : shifts) {
            if (isShiftInTimeRange(shift.getStartTime(), type)) {
                filteredShifts.add(shift);
            }
        }
        return filteredShifts;
    }

    private boolean isShiftInTimeRange(LocalTime startTime, String type) {
        if (startTime == null) return false;

        int hour = startTime.getHour(); // 0 to 23

        if (type.equalsIgnoreCase("MORNING")) {
            return hour >= 5 && hour < 12;   // 5:00 AM - 11:59 AM
        } else if (type.equalsIgnoreCase("AFTERNOON")) {
            return hour >= 12 && hour < 17;  // 12:00 PM - 4:59 PM
        } else if (type.equalsIgnoreCase("EVENING")) {
            return hour >= 17 && hour < 24;  // 5:00 PM - 11:59 PM
        }

        return true;
    }

    public Shift createShift(ShiftDTO shiftDTO) {
        if (shiftDTO == null || shiftDTO.getDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is required");
        }

        LocalDate shiftDate = shiftDTO.getDate();
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