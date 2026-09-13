package com.example.shift_planner_backend.repositories;

import com.example.shift_planner_backend.models.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByDateGreaterThanEqualAndIsAvailableTrue(LocalDate date);
}
