package com.example.shift_planner_backend.repositories;

import com.example.shift_planner_backend.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByUser_UserId(Long userId);
}
