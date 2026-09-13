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
}

