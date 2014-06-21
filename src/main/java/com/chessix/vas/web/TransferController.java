package com.chessix.vas.web;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.chessix.vas.actors.messages.Transfer;
import com.chessix.vas.dto.TransferDTO;
import com.chessix.vas.service.AccountService;
import com.chessix.vas.service.ClasService;

/**
 * 
 * @author Mark Wigmans
 * 
 */
@RestController
@RequestMapping(value = "/transfer")
@Slf4j
public class TransferController {

    private final ActorSystem actorSystem;
    private final ClasService clasService;
    private final AccountService accountService;

    private final Timeout timeout = new Timeout(Duration.create(30, TimeUnit.SECONDS));

    @Autowired
    public TransferController(final ActorSystem actorSystem, final ClasService clasService, final AccountService accountService) {
        super();
        this.actorSystem = actorSystem;
        this.clasService = clasService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "/{clasId}/{from}/{to}/{amount}", method = RequestMethod.POST)
    public DeferredResult<Object> transfer(@PathVariable final String clasId, @PathVariable final String from,
            @PathVariable final String to, @PathVariable final int amount) {
        log.info("transfer({},{},{},{})", clasId, from, to, amount);
        final ActorRef clas = clasService.getClas(clasId);
        final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
        if (clas != null) {
            final ExecutionContext ec = actorSystem.dispatcher();
            final Future<Object> future = Patterns.ask(clas, new Transfer.RequestBuilder(accountService.getAccountId(from),
                    accountService.getAccountId(to), amount).build(), timeout);
            future.onSuccess(new OnSuccess<Object>() {
                @Override
                public void onSuccess(final Object result) {
                    log.info("transfer({},{},{},{}) : result: {}", clasId, from, to, amount, result);
                    deferredResult.setResult(result);
                }
            }, ec);
            future.onFailure(new OnFailure() {
                @Override
                public void onFailure(final Throwable arg) throws Throwable {
                    log.error("onFailure", arg);
                    deferredResult.setErrorResult(arg);
                }
            }, ec);
        } else {
            deferredResult.setErrorResult(new TransferDTO(clasId, from, to, false, "CLAS does not exist"));
        }
        return deferredResult;
    }
}
