package com.example.shift_planner_backend.services;

import com.example.shift_planner_backend.dto.request.ScheduleDTO;
import com.example.shift_planner_backend.models.Schedule;
import com.example.shift_planner_backend.models.Shift;
import com.example.shift_planner_backend.models.User;
import com.example.shift_planner_backend.repositories.ScheduleRepository;
import com.example.shift_planner_backend.repositories.ShiftRepository;
import com.example.shift_planner_backend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
                          UserRepository userRepository,
                          ShiftRepository shiftRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
    }

    public Schedule getScheduleByUserId(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

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
        schedule.getShifts().size();
        return schedule;
    }

    public Schedule getScheduleById(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        if (schedule.getShifts() == null) {
            schedule.setShifts(new ArrayList<>());
        }
        schedule.getShifts().size();
        return schedule;
    }

    public Schedule createSchedule(ScheduleDTO scheduleDTO) {
        if (scheduleDTO == null || scheduleDTO.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        User user = userRepository.findById(scheduleDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Schedule existingSchedule = scheduleRepository.findByUser_UserId(user.getUserId())
                .orElse(null);
        Schedule schedule = existingSchedule != null ? existingSchedule : new Schedule();
        schedule.setUser(user);

        List<Shift> updatedShifts = new ArrayList<>();
        if (scheduleDTO.getShiftIds() != null && !scheduleDTO.getShiftIds().isEmpty()) {
            updatedShifts = shiftRepository.findAllById(scheduleDTO.getShiftIds());
        }

        validateNoOverlappingShifts(updatedShifts);
        applyShiftAvailability(schedule, updatedShifts);
        schedule.setShifts(updatedShifts);

        return scheduleRepository.save(schedule);
    }

    public Schedule updateSchedule(Long scheduleId, ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        List<Shift> updatedShifts = new ArrayList<>();
        if (scheduleDTO != null && scheduleDTO.getShiftIds() != null && !scheduleDTO.getShiftIds().isEmpty()) {
            updatedShifts = shiftRepository.findAllById(scheduleDTO.getShiftIds());
        }

        validateNoOverlappingShifts(updatedShifts);
        applyShiftAvailability(schedule, updatedShifts);
        schedule.setShifts(updatedShifts);

        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

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
    }

    public Schedule addShiftToSchedule(Long userId, Long shiftId) {
        if (userId == null || shiftId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID and Shift ID are required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Schedule schedule = scheduleRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    Schedule newSchedule = new Schedule();
                    newSchedule.setUser(user);
                    newSchedule.setShifts(new ArrayList<>());
                    return newSchedule;
                });

        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found"));

        if (schedule.getShifts() == null) {
            schedule.setShifts(new ArrayList<>());
        }

        if (!containsShift(schedule.getShifts(), shiftId)) {
            schedule.getShifts().add(shift);
        }

        validateNoOverlappingShifts(schedule.getShifts());
        applyShiftAvailability(schedule, schedule.getShifts());
        return scheduleRepository.save(schedule);
    }

    public Schedule removeShiftFromSchedule(Long userId, Long shiftId) {
        if (userId == null || shiftId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID and Shift ID are required");
        }

        Schedule schedule = scheduleRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        if (schedule.getShifts() == null) {
            schedule.setShifts(new ArrayList<>());
            return scheduleRepository.save(schedule);
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
        return scheduleRepository.save(schedule);
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

        Set<Long> updatedShiftIds = new HashSet<>();
        for (Shift shift : updatedShifts) {
            if (shift != null) {
                updatedShiftIds.add(shift.getShiftId());
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
