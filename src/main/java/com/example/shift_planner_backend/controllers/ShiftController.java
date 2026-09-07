package com.example.shift_planner_backend.controllers;

import com.example.shift_planner_backend.models.Shift;
import com.example.shift_planner_backend.repositories.ShiftRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shifts")
@CrossOrigin(origins = "*")
public class ShiftController {

    private final ShiftRepository shiftRepository;

    public ShiftController(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @GetMapping
    public List<Shift> getShifts() {
        return shiftRepository.findAll();
    }

    @PostMapping
    public Shift createShift(@RequestBody Shift shift) {
        return shiftRepository.save(shift);
    }

    @DeleteMapping
    public void deleteShift(@RequestParam Long id) {
        shiftRepository.deleteById(id);
    }

}
