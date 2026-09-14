package com.example.shift_planner_backend.controllers;

import com.example.shift_planner_backend.dto.request.ScheduleDTO;
import com.example.shift_planner_backend.models.Schedule;
import com.example.shift_planner_backend.services.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/users/{userId}")
    public Schedule getScheduleByUserId(@PathVariable Long userId) {
        return scheduleService.getScheduleByUserId(userId);
    }

    @GetMapping("/{scheduleId}")
    public Schedule getScheduleById(@PathVariable Long scheduleId) {
        return scheduleService.getScheduleById(scheduleId);
    }

    @PostMapping
    public Schedule createSchedule(@Valid @RequestBody ScheduleDTO scheduleDTO) {
        return scheduleService.createSchedule(scheduleDTO);
    }

    @PutMapping("/{scheduleId}")
    public Schedule updateSchedule(@PathVariable Long scheduleId, @Valid @RequestBody ScheduleDTO scheduleDTO) {
        return scheduleService.updateSchedule(scheduleId, scheduleDTO);
    }

    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
    }

    @PostMapping("/users/{userId}/shifts/{shiftId}")
    public Schedule addShiftToUserSchedule(@PathVariable Long userId, @PathVariable Long shiftId) {
        return scheduleService.addShiftToSchedule(userId, shiftId);
    }

    @DeleteMapping("/users/{userId}/shifts/{shiftId}")
    public Schedule removeShiftFromUserSchedule(@PathVariable Long userId, @PathVariable Long shiftId) {
        return scheduleService.removeShiftFromSchedule(userId, shiftId);
    }

    @PostMapping("/{scheduleId}/shifts/{shiftId}")
    public Schedule addShiftToSchedule(@PathVariable Long scheduleId, @PathVariable Long shiftId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return scheduleService.addShiftToSchedule(schedule.getUser().getUserId(), shiftId);
    }

    @DeleteMapping("/{scheduleId}/shifts/{shiftId}")
    public Schedule removeShiftFromSchedule(@PathVariable Long scheduleId, @PathVariable Long shiftId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return scheduleService.removeShiftFromSchedule(schedule.getUser().getUserId(), shiftId);
    }
}

