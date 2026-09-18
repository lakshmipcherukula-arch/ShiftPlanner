package com.example.shift_planner_backend.controllers;

import com.example.shift_planner_backend.dto.request.ShiftDTO;
import com.example.shift_planner_backend.models.Shift;
import com.example.shift_planner_backend.repositories.ShiftRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shifts")
public class ShiftController {

    private final ShiftRepository shiftRepository;

    public ShiftController(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @GetMapping
    public ResponseEntity<List<Shift>> getActiveShifts(
            @RequestParam(required = false, defaultValue = "All") String type,
            @RequestParam(required = false, defaultValue = "All") String day) {

        String filterType = (type == null || type.isBlank()) ? "All" : type.trim();
        String filterDay = (day == null || day.isBlank()) ? "All" : day.trim();

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        List<Shift> shifts = shiftRepository.findByDateGreaterThanEqualAndIsAvailableTrueOrderByDateAscStartTimeAsc(today);

        List<Shift> timeFilteredShifts = new ArrayList<>();
        if (filterType.equalsIgnoreCase("all")) {
            timeFilteredShifts = shifts;
        } else {
            if (!filterType.equalsIgnoreCase("MORNING") &&
                    !filterType.equalsIgnoreCase("AFTERNOON") &&
                    !filterType.equalsIgnoreCase("EVENING")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid shift filter type: " + filterType);
            }
            for (Shift shift : shifts) {
                if (isShiftInTimeRange(shift.getStartTime(), filterType)) {
                    timeFilteredShifts.add(shift);
                }
            }
        }

        if (filterDay.equalsIgnoreCase("all")) {
            return new ResponseEntity<>(timeFilteredShifts, HttpStatus.OK);
        }

        List<Shift> finalFilteredShifts = new ArrayList<>();
        for (Shift shift : timeFilteredShifts) {
            if (shift.getDate() != null && isShiftOnDay(shift.getDate(), filterDay)) {
                finalFilteredShifts.add(shift);
            }
        }

        return new ResponseEntity<>(finalFilteredShifts, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Shift> createShift(@Valid @RequestBody ShiftDTO shiftDTO) {
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

        Shift createdShift = shiftRepository.save(shift);
        return new ResponseEntity<>(createdShift, HttpStatus.CREATED);
    }

    @DeleteMapping("/{shiftId}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long shiftId) throws NoResourceFoundException {
        if (shiftId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shift ID cannot be null");
        }

        Shift shift = shiftRepository.findById(shiftId).orElse(null);
        if (shift == null) {
            String path = "/shifts/" + shiftId;
            throw new NoResourceFoundException(HttpMethod.DELETE, path, "Shift not found!");
        } else {
            shiftRepository.deleteById(shiftId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
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
}