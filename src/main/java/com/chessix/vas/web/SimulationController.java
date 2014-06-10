package com.chessix.vas.web;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import scala.concurrent.duration.Duration;
import akka.util.Timeout;

import com.chessix.vas.service.ValidationService;

/**
 * Test start/stop/report
 * 
 * @author Mark Wigmans
 * 
 */
@RestController
@RequestMapping(value = "/simulation")
public class SimulationController {

    private final ValidationService validationService;

    private final Timeout timeout = new Timeout(Duration.create(30, TimeUnit.MINUTES));

    @Autowired
    public SimulationController(final ValidationService validationService) {
        super();
        this.validationService = validationService;
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public Object stop() throws Exception {
        return validationService.prepare(timeout);
    }
}
