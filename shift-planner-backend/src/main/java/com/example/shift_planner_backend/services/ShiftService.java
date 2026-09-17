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

    public List<Shift> getActiveShifts(String type, String day) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        List<Shift> shifts = shiftRepository.findByDateGreaterThanEqualAndIsAvailableTrueOrderByDateAscStartTimeAsc(today);
        List<Shift> timeFilteredShifts = new ArrayList<>();
        if (type == null || type.isBlank() || type.equalsIgnoreCase("all")) {
            timeFilteredShifts = shifts;
        } else {
            if (!type.equalsIgnoreCase("MORNING") &&
                !type.equalsIgnoreCase("AFTERNOON") &&
                !type.equalsIgnoreCase("EVENING")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid shift filter type: " + type);
        }
            for (Shift shift : shifts) {
            if (isShiftInTimeRange(shift.getStartTime(), type)) {
                timeFilteredShifts.add(shift);
            }
        }
    }
        if (day == null || day.isBlank() || day.equalsIgnoreCase("all")) {
            return timeFilteredShifts;
        }
        List<Shift> finalFilteredShifts = new ArrayList<>();
        for (Shift shift : timeFilteredShifts) {
            if (shift.getDate() != null && isShiftOnDay(shift.getDate(), day)) {
                finalFilteredShifts.add(shift);
            }
        }
        return finalFilteredShifts;
    }
    private boolean isShiftOnDay(LocalDate date, String day) {
        if (date == null || day == null) return false;
        String shiftDayName = date.getDayOfWeek().name();
        return shiftDayName.equalsIgnoreCase(day.trim());
    }
    private boolean isShiftInTimeRange(LocalTime startTime, String type) {
        if (startTime == null) return false;

        int hour = startTime.getHour();

        if (type.equalsIgnoreCase("MORNING")) {
            return hour >= 5 && hour < 12;
        } else if (type.equalsIgnoreCase("AFTERNOON")) {
            return hour >= 12 && hour < 17;
        } else if (type.equalsIgnoreCase("EVENING")) {
            return hour >= 17 && hour < 24;
        }

        return false;
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