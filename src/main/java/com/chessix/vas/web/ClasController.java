package com.chessix.vas.web;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.chessix.vas.actors.messages.Clean;
import com.chessix.vas.actors.messages.Count;
import com.chessix.vas.actors.messages.JournalMessage;
import com.chessix.vas.actors.messages.Validate;
import com.chessix.vas.dto.ClasCreated;
import com.chessix.vas.service.ClasService;
import com.chessix.vas.service.ValidationService;

/**
 * 
 * @author Mark Wigmans
 * 
 */
@RestController
@RequestMapping(value = "/clas")
@Slf4j
public class ClasController {

    private final ActorSystem system;
    private final ClasService clasService;
    private final ValidationService validationService;

    private final Timeout timeout = new Timeout(Duration.create(10, TimeUnit.SECONDS));

    /**
     * Auto wired constructor
     */
    @Autowired
    public ClasController(final ActorSystem system, final ClasService clasService,  final ValidationService validationService) {
        super();
        this.system = system;
        this.clasService = clasService;
        this.validationService = validationService;
    }

    @RequestMapping(value = "/{clasId}", method = RequestMethod.POST)
    public synchronized ClasCreated createClas(@PathVariable final String clasId) {
        log.info("createClas({})", clasId);
        if (clasService.create(clasId)) {
            return new ClasCreated(clasId, true, "CLAS created");
        } else {
            return new ClasCreated(clasId, false, "Already created");
        }
    }

    @RequestMapping(value = "/{clasId}/clean", method = RequestMethod.POST)
    public DeferredResult<Object> clean(@PathVariable final String clasId) {
        log.info("clean({})", clasId);

        clasService.getJournal().tell(new JournalMessage.Clean(clasId), ActorRef.noSender());

        final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
        final ActorRef clas = clasService.getClas(clasId);
        if (clas != null) {
            // there is data
            final Future<Object> future = Patterns.ask(clas, new Clean.Request(), timeout);
            future.onComplete(new OnComplete<Object>() {
                @Override
                public void onComplete(final Throwable failure, final Object result) {
                    if (failure != null) {
                        // We got a failure, handle it here
                        deferredResult.setErrorResult(failure);
                    } else {
                        // We got a result, do something with it
                        deferredResult.setResult(result);
                    }
                }
            }, system.dispatcher());
        } else {
            deferredResult.setResult(new Clean.Response(true, "Clas was already removed"));
        }

        return deferredResult;
    }

    @RequestMapping(value = "/{clasId}/count", method = RequestMethod.GET)
    public DeferredResult<Object> count(@PathVariable final String clasId) {
        log.info("clean({})", clasId);

        final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
        final ActorRef clas = clasService.getClas(clasId);
        if (clas != null) {
            final Future<Object> future = Patterns.ask(clas, new Count.Request(), timeout);
            future.onComplete(new OnComplete<Object>() {
                @Override
                public void onComplete(final Throwable failure, final Object result) {
                    if (failure != null) {
                        // We got a failure, handle it here
                        deferredResult.setErrorResult(failure);
                    } else {
                        // We got a result, do something with it
                        deferredResult.setResult(result);
                    }
                }
            }, system.dispatcher());
        } else {
            deferredResult.setResult(new Count.Response(false, null, "CLAS does not exist"));
        }

        return deferredResult;
    }

    @RequestMapping(value = "/{clasId}/validate/fast", method = RequestMethod.GET)
    public DeferredResult<Object> fastValidate(@PathVariable final String clasId) {
        log.info("clean({})", clasId);

        final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
        final ActorRef clas = clasService.getClas(clasId);
        if (clas != null) {
            final Future<Object> future = Patterns.ask(clas, new Validate.Request(), timeout);
            future.onComplete(new OnComplete<Object>() {
                @Override
                public void onComplete(final Throwable failure, final Object result) {
                    if (failure != null) {
                        // We got a failure, handle it here
                        deferredResult.setErrorResult(failure);
                    } else {
                        // We got a result, do something with it
                        deferredResult.setResult(result);
                    }
                }
            }, system.dispatcher());
        } else {
            deferredResult.setResult(new Validate.Response(false, "CLAS does not exist"));
        }

        return deferredResult;
    }

    @RequestMapping(value = "/{clasId}/validate/data", method = RequestMethod.GET)
    public Validate.Response dataValidate(@PathVariable final String clasId) {
        return new Validate.Response(clasService.validate(clasId), "");
    }

    @RequestMapping(value = "/{clasId}/validate/insync", method = RequestMethod.GET)
    public Validate.Response validateInSync(@PathVariable final String clasId) {
        return new Validate.Response(validationService.validate(clasId), "");
    }
}
