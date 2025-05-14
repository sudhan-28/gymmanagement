package com.gym.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // ⬅️ Import logger classes
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.model.Attendance;
import com.gym.model.User;
import com.gym.service.AttendanceService;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin
public class AttendanceController {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class); // ✅ Add this

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/mark")
    public Attendance markAttendance(@RequestBody Attendance attendance) {
        return attendanceService.markAttendance(attendance);
    }

    @GetMapping("/track/{date}")
    public ResponseEntity<List<User>> trackAttendance(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            logger.info("Parsed date: {}", parsedDate);
            List<User> presentUsers = attendanceService.getAttendanceByDate(parsedDate);
            if (presentUsers.isEmpty()) {
                logger.warn("No attendance records found for the date: {}", parsedDate);
            }
            return ResponseEntity.ok(presentUsers);
        } catch (Exception e) {
            logger.error("Error fetching attendance data for date: {}", date, e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
