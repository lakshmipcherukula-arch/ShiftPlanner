package com.example.shift_planner_backend.controllers;

import com.example.shift_planner_backend.dto.request.ScheduleDTO;
import com.example.shift_planner_backend.models.Schedule;
import com.example.shift_planner_backend.models.Shift;
import com.example.shift_planner_backend.models.User;
import com.example.shift_planner_backend.repositories.ScheduleRepository;
import com.example.shift_planner_backend.repositories.ShiftRepository;
import com.example.shift_planner_backend.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;

    public ScheduleController(ScheduleRepository scheduleRepository,
                              UserRepository userRepository,
                              ShiftRepository shiftRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Schedule> getScheduleByUserId(@PathVariable Long userId) throws NoResourceFoundException {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            String path = "/schedules/users/" + userId;
            throw new NoResourceFoundException(HttpMethod.GET, path, "User not found with ID: " + userId);
        }

        Schedule schedule = scheduleRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    Schedule newSchedule = new Schedule();
                    newSchedule.setUser(user);
                    newSchedule.setShifts(new ArrayList<>());
                    return newSchedule;
                });

        if (schedule.getShifts() == null) {
            schedule.setShifts(new ArrayList<>());
        }
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Long scheduleId) throws NoResourceFoundException {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) {
            String path = "/schedules/" + scheduleId;
            throw new NoResourceFoundException(HttpMethod.GET, path, "Schedule not found with ID: " + scheduleId);
        }

        if (schedule.getShifts() == null) {
            schedule.setShifts(new ArrayList<>());
        }
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody ScheduleDTO scheduleDTO) throws NoResourceFoundException {
        if (scheduleDTO == null || scheduleDTO.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        User user = userRepository.findById(scheduleDTO.getUserId()).orElse(null);
        if (user == null) {
            String path = "/schedules";
            throw new NoResourceFoundException(HttpMethod.POST, path, "User not found with ID: " + scheduleDTO.getUserId());
        }

        Schedule existingSchedule = scheduleRepository.findByUser_UserId(user.getUserId()).orElse(null);
        Schedule schedule = existingSchedule != null ? existingSchedule : new Schedule();
        schedule.setUser(user);

        List<Shift> updatedShifts = new ArrayList<>();
        if (scheduleDTO.getShiftIds() != null && !scheduleDTO.getShiftIds().isEmpty()) {
            updatedShifts = shiftRepository.findAllById(scheduleDTO.getShiftIds());
        }

        validateNoOverlappingShifts(updatedShifts);
        applyShiftAvailability(schedule, updatedShifts);
        schedule.setShifts(updatedShifts);

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return new ResponseEntity<>(savedSchedule, HttpStatus.CREATED);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long scheduleId, @Valid @RequestBody ScheduleDTO scheduleDTO) throws NoResourceFoundException {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) {
            String path = "/schedules/" + scheduleId;
            throw new NoResourceFoundException(HttpMethod.PUT, path, "Schedule not found with ID: " + scheduleId);
        }

        List<Shift> updatedShifts = new ArrayList<>();
        if (scheduleDTO != null && scheduleDTO.getShiftIds() != null && !scheduleDTO.getShiftIds().isEmpty()) {
            updatedShifts = shiftRepository.findAllById(scheduleDTO.getShiftIds());
        }

        validateNoOverlappingShifts(updatedShifts);
        applyShiftAvailability(schedule, updatedShifts);
        schedule.setShifts(updatedShifts);

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return new ResponseEntity<>(savedSchedule, HttpStatus.OK);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) throws NoResourceFoundException {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) {
            String path = "/schedules/" + scheduleId;
            throw new NoResourceFoundException(HttpMethod.DELETE, path, "Schedule not found with ID: " + scheduleId);
        }

        if (schedule.getShifts() != null) {
            for (Shift shift : schedule.getShifts()) {
                if (shift != null) {
                    shift.setIsAvailable(true);
                    if (shift.getSchedules() != null) {
                        shift.getSchedules().remove(schedule);
                    }
                }
            }
        }

        scheduleRepository.deleteById(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/users/{userId}/shifts/{shiftId}")
    public ResponseEntity<Schedule> addShiftToUserSchedule(@PathVariable Long userId, @PathVariable Long shiftId) throws NoResourceFoundException {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            String path = "/schedules/users/" + userId + "/shifts/" + shiftId;
            throw new NoResourceFoundException(HttpMethod.POST, path, "User not found with ID: " + userId);
        }

        Shift shift = shiftRepository.findById(shiftId).orElse(null);
        if (shift == null) {
            String path = "/schedules/users/" + userId + "/shifts/" + shiftId;
            throw new NoResourceFoundException(HttpMethod.POST, path, "Shift not found with ID: " + shiftId);
        }

        Schedule schedule = scheduleRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    Schedule newSchedule = new Schedule();
                    newSchedule.setUser(user);
                    newSchedule.setShifts(new ArrayList<>());
                    return newSchedule;
                });

        if (schedule.getShifts() == null) {
            schedule.setShifts(new ArrayList<>());
        }
        List<Shift> updatedShifts = new ArrayList<>(schedule.getShifts());

        if (!containsShift(updatedShifts, shiftId)) {
            updatedShifts.add(shift);
        }

        validateNoOverlappingShifts(updatedShifts);
        schedule.setShifts(updatedShifts);
        applyShiftAvailability(schedule, updatedShifts);

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return new ResponseEntity<>(savedSchedule, HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}/shifts/{shiftId}")
    public ResponseEntity<Schedule> removeShiftFromUserSchedule(@PathVariable Long userId, @PathVariable Long shiftId) throws NoResourceFoundException {
        Schedule schedule = scheduleRepository.findByUser_UserId(userId).orElse(null);
        if (schedule == null) {
            String path = "/schedules/users/" + userId + "/shifts/" + shiftId;
            throw new NoResourceFoundException(HttpMethod.DELETE, path, "Schedule not found for User ID: " + userId);
        }

        if (schedule.getShifts() == null) {
            schedule.setShifts(new ArrayList<>());
            Schedule saved = scheduleRepository.save(schedule);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        }

        List<Shift> remainingShifts = new ArrayList<>();
        for (Shift shift : schedule.getShifts()) {
            if (shift != null && !shiftId.equals(shift.getShiftId())) {
                remainingShifts.add(shift);
            } else if (shift != null) {
                shift.setIsAvailable(true);
                if (shift.getSchedules() != null) {
                    shift.getSchedules().remove(schedule);
                }
            }
        }

        schedule.setShifts(remainingShifts);
        applyShiftAvailability(schedule, remainingShifts);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return new ResponseEntity<>(savedSchedule, HttpStatus.OK);
    }

    @PostMapping("/{scheduleId}/shifts/{shiftId}")
    public ResponseEntity<Schedule> addShiftToSchedule(@PathVariable Long scheduleId, @PathVariable Long shiftId) throws NoResourceFoundException {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null || schedule.getUser() == null) {
            String path = "/schedules/" + scheduleId + "/shifts/" + shiftId;
            throw new NoResourceFoundException(HttpMethod.POST, path, "Schedule or associated User not found with ID: " + scheduleId);
        }
        return addShiftToUserSchedule(schedule.getUser().getUserId(), shiftId);
    }

    @DeleteMapping("/{scheduleId}/shifts/{shiftId}")
    public ResponseEntity<Schedule> removeShiftFromSchedule(@PathVariable Long scheduleId, @PathVariable Long shiftId) throws NoResourceFoundException {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null || schedule.getUser() == null) {
            String path = "/schedules/" + scheduleId + "/shifts/" + shiftId;
            throw new NoResourceFoundException(HttpMethod.DELETE, path, "Schedule or associated User not found with ID: " + scheduleId);
        }
        return removeShiftFromUserSchedule(schedule.getUser().getUserId(), shiftId);
    }

    private void applyShiftAvailability(Schedule schedule, List<Shift> updatedShifts) {
        if (schedule != null && schedule.getShifts() != null) {
            for (Shift previousShift : schedule.getShifts()) {
                if (previousShift != null && !containsShift(updatedShifts, previousShift.getShiftId())) {
                    previousShift.setIsAvailable(true);
                    if (previousShift.getSchedules() != null) {
                        previousShift.getSchedules().remove(schedule);
                    }
                    shiftRepository.save(previousShift);
                }
            }
        }

        List<Long> updatedShiftIds = new ArrayList<>();
        for (Shift shift : updatedShifts) {
            if (shift != null) {
                if (!updatedShiftIds.contains(shift.getShiftId())) {
                    updatedShiftIds.add(shift.getShiftId());
                }
                shift.setIsAvailable(false);
                if (shift.getSchedules() == null) {
                    shift.setSchedules(new ArrayList<>());
                }
                if (!shift.getSchedules().contains(schedule)) {
                    shift.getSchedules().add(schedule);
                }
                shiftRepository.save(shift);
            }
        }

        if (schedule != null && schedule.getShifts() != null) {
            for (Shift previousShift : schedule.getShifts()) {
                if (previousShift != null && updatedShiftIds.contains(previousShift.getShiftId())) {
                    previousShift.setIsAvailable(false);
                    shiftRepository.save(previousShift);
                }
            }
        }
    }

    private boolean containsShift(List<Shift> shifts, Long shiftId) {
        if (shifts == null || shiftId == null) {
            return false;
        }
        for (Shift shift : shifts) {
            if (shift != null && shiftId.equals(shift.getShiftId())) {
                return true;
            }
        }
        return false;
    }

    private void validateNoOverlappingShifts(List<Shift> shifts) {
        if (shifts == null || shifts.size() < 2) {
            return;
        }

        for (int i = 0; i < shifts.size(); i++) {
            Shift first = shifts.get(i);
            if (first == null || first.getDate() == null || first.getStartTime() == null || first.getEndTime() == null) {
                continue;
            }

            for (int j = i + 1; j < shifts.size(); j++) {
                Shift second = shifts.get(j);
                if (second == null || second.getDate() == null || second.getStartTime() == null || second.getEndTime() == null) {
                    continue;
                }

                if (!first.getDate().equals(second.getDate())) {
                    continue;
                }

                int firstStart = toMinutes(first.getStartTime());
                int firstEnd = toMinutes(first.getEndTime());
                int secondStart = toMinutes(second.getStartTime());
                int secondEnd = toMinutes(second.getEndTime());

                if (firstStart < 0 || firstEnd < 0 || secondStart < 0 || secondEnd < 0) {
                    continue;
                }

                if (firstStart < secondEnd && secondStart < firstEnd) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Shift times overlap with an existing shift");
                }
            }
        }
    }

    private int toMinutes(LocalTime timeValue) {
        if (timeValue == null) {
            return -1;
        }
        return timeValue.getHour() * 60 + timeValue.getMinute();
    }
}