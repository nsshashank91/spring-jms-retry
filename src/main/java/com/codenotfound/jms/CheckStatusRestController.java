package com.codenotfound.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CheckStatusRestController {

    @Autowired
    CheckStatusService checkStatusService;

    @GetMapping(value = "check")
    public String checkStatus(){
        return checkStatusService.checkStatus();
    }
}
