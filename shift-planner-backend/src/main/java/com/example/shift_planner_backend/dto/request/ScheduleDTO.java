package com.example.shift_planner_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ScheduleDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    private List<Long> shiftIds;

    public ScheduleDTO() {}

    public ScheduleDTO(Long userId, List<Long> shiftIds) {
        this.userId = userId;
        this.shiftIds = shiftIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getShiftIds() {
        return shiftIds;
    }

    public void setShiftIds(List<Long> shiftIds) {
        this.shiftIds = shiftIds;
    }
}