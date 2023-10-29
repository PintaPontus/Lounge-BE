package com.pinta.lounge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg")
public class MsgController {

    @GetMapping("/test")
    public ResponseEntity<Void> msg(){
        return ResponseEntity.ok().build();
    }

}
