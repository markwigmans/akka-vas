/******************************************************************************
 Copyright 2014,2015 Mark Wigmans

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ******************************************************************************/
package com.chessix.vas.web;

import akka.util.Timeout;
import com.chessix.vas.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Test start/stop/report
 */
@RestController
@RequestMapping(value = "/simulation")
public class SimulationController {

    private final ValidationService validationService;

    private final Timeout timeout = new Timeout(Duration.create(90, TimeUnit.MINUTES));

    @Autowired
    public SimulationController(final ValidationService validationService) {
        super();
        this.validationService = validationService;
    }

    /**
     * The simulation is stopped. Return if all messages are processed.
     */
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public Object stop() throws Exception {
        return validationService.prepare(timeout);
    }
}
