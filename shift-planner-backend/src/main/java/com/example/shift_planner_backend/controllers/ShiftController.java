package com.example.shift_planner_backend.controllers;

import com.example.shift_planner_backend.dto.request.ShiftDTO;
import com.example.shift_planner_backend.models.Shift;
import com.example.shift_planner_backend.services.ShiftService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @GetMapping
    public List<Shift> getShifts(@RequestParam(required = false) String type) {
        return shiftService.getActiveShifts(type);
    }

    @PostMapping
    public Shift createShift(@Valid @RequestBody ShiftDTO shiftDTO) {
        return shiftService.createShift(shiftDTO);
    }

    @DeleteMapping({"/{shiftId}"})
    public void deleteShift(@PathVariable Long shiftId) {
        shiftService.deleteShift(shiftId);
    }

}
