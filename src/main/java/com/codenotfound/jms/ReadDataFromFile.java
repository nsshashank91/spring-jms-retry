package com.codenotfound.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@RestController
@RequestMapping("/read")
public class ReadDataFromFile {

    @Autowired
    private JmsTemplate jmsTemplate;

    @GetMapping("/file")
    public String readFile(){
        readDataFromFile("reprocess.txt");
        return "read file";
    }

    public synchronized void readDataFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Read each line from the file until the end
            while ((line = reader.readLine()) != null) {
                // Print each line
                //System.out.println(line);
                sendMessage(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading data from file: " + e.getMessage());
        }
    }

    private void sendMessage(String msg) {
        try {
            jmsTemplate.convertAndSend("destination", msg);
            System.out.println("message sent " + msg);
        } catch (JmsException e) {
            System.out.println("error encountered writing data to file "+e);
        }
    }

}
