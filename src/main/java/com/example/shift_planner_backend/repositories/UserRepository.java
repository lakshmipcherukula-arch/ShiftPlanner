package com.example.shift_planner_backend.repositories;

import com.example.shift_planner_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
