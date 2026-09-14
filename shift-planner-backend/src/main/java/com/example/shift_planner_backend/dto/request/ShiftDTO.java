package com.example.shift_planner_backend.dto.request;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ShiftDTO {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Start time is required")
    private String startTime;

    @NotBlank(message = "End time is required")
    private String endTime;

    @NotNull(message = "Hours are required")
    @Positive(message = "Hours must be greater than zero")
    private Double hours;

    private List<Long> scheduleIds;

    public ShiftDTO() {}

    public ShiftDTO(LocalDate date, String startTime, String endTime, Double hours, List<Long> scheduleIds) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hours = hours;
        this.scheduleIds = scheduleIds;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public List<Long> getScheduleIds() {
        return scheduleIds;
    }

    public void setScheduleIds(List<Long> scheduleIds) {
        this.scheduleIds = scheduleIds;
    }
}