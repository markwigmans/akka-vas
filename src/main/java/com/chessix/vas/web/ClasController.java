package com.chessix.vas.web;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * @author Mark Wigmans
 */
@RestController
@RequestMapping(value = "/clas")
@Slf4j
public class ClasController {

    private final ActorSystem system;
    private final ClasService clasService;
    private final ActorRef batchStorage;
    private final ValidationService validationService;

    private final Timeout timeout = new Timeout(Duration.create(10, TimeUnit.SECONDS));

    /**
     * Auto wired constructor
     */
    @Autowired
    public ClasController(final ActorSystem system, final ClasService clasService, final ActorRef batchStorage, final ValidationService validationService) {
        super();
        this.system = system;
        this.clasService = clasService;
        this.batchStorage = batchStorage;
        this.validationService = validationService;
    }

    /**
     * Create a class.
     */
    @RequestMapping(value = "/{clasId}", method = RequestMethod.POST)
    public ClasCreated createClas(@PathVariable final String clasId) {
        log.debug("createClas({})", clasId);
        if (clasService.create(clasId)) {
            return new ClasCreated(clasId, true, "CLAS created");
        } else {
            return new ClasCreated(clasId, false, "Already created");
        }
    }

    /**
     * Remove all accounts and transactions for the CLAS with the given id {@code clasId}.
     */
    @RequestMapping(value = "/{clasId}/clean", method = RequestMethod.POST)
    public DeferredResult<Object> clean(@PathVariable final String clasId) {
        log.debug("clean({})", clasId);

        batchStorage.tell(new JournalMessage.CleanBuilder(clasId).build(), ActorRef.noSender());

        final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
        final ActorRef clas = clasService.getClas(clasId);
        if (clas != null) {
            // there is data
            final Future<Object> future = Patterns.ask(clas, new Clean.RequestBuilder(clasId).build(), timeout);
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
            deferredResult.setResult(new Clean.ResponseBuilder(true).clasId(clasId).message("Clas was already removed").build());
        }

        return deferredResult;
    }

    /**
     * Count the number of records for the CLAS with id {@code classId}.
     */
    @RequestMapping(value = "/{clasId}/count", method = RequestMethod.GET)
    public DeferredResult<Object> count(@PathVariable final String clasId) {
        log.debug("count({})", clasId);

        final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
        final ActorRef clas = clasService.getClas(clasId);
        if (clas != null) {
            final Future<Object> future = Patterns.ask(clas, new Count.RequestBuilder(clasId).build(), timeout);
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
            deferredResult.setResult(new Count.ResponseBuilder(false).clasId(clasId).message("CLAS does not exist").build());
        }

        return deferredResult;
    }

    /**
     * Validate if for given {@code classId} the speed layer is in sync.
     */
    @RequestMapping(value = "/{clasId}/validate/speed", method = RequestMethod.GET)
    public DeferredResult<Object> validateSpeedLayer(@PathVariable final String clasId) {
        log.debug("fastValidate({})", clasId);

        final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
        final ActorRef clas = clasService.getClas(clasId);
        if (clas != null) {
            final Future<Object> future = Patterns.ask(clas, new Validate.RequestBuilder(clasId).build(), timeout);
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
            deferredResult.setResult(new Validate.ResponseBuilder(false).clasId(clasId).message("CLAS does not exist").build());
        }

        return deferredResult;
    }

    /**
     * Validate if for given {@code classId} the batch layer is in sync.
     */
    @RequestMapping(value = "/{clasId}/validate/batch", method = RequestMethod.GET)
    public Validate.Response validateBatchLayer(@PathVariable final String clasId) {
        return new Validate.ResponseBuilder(clasService.validate(clasId)).clasId(clasId).build();
    }

    /**
     * Validate if for given {@code classId} the batch layer and speed layer are in sync.
     */
    @RequestMapping(value = "/{clasId}/validate/insync", method = RequestMethod.GET)
    public Validate.Response validateInSync(@PathVariable final String clasId) {
        return new Validate.ResponseBuilder(validationService.validate(clasId)).clasId(clasId).build();
    }
}
