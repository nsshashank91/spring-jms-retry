package com.codenotfound.jms;

import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;

@Service
public class CheckStatusService {

   // @Retryable(value = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(3000))
    public String checkStatus(){
        System.out.println("calling another service to get status");
        throw new RuntimeException("service not available");
        //return "approved";
    }

    @Recover
    public String recover(){
        return "please try after sometime";
    }
}
