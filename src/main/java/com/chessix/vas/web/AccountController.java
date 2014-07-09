package com.chessix.vas.web;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import com.chessix.vas.actors.messages.Balance;
import com.chessix.vas.actors.messages.CreateAccount;
import com.chessix.vas.dto.AccountCreated;
import com.chessix.vas.dto.SaldoDTO;
import com.chessix.vas.service.AccountService;
import com.chessix.vas.service.ClasService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import scala.concurrent.Future;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mark Wigmans
 */
@RestController
@RequestMapping(value = "/account")
@Slf4j
public class AccountController {

    private final ActorSystem system;
    private final ClasService clasService;
    private final AccountService accountService;

    private final long timeout = 30000L;

    /**
     * Auto wired constructor
     */
    @Autowired
    public AccountController(final ActorSystem system, final ClasService clasService, final AccountService accountService) {
        this.system = system;
        this.clasService = clasService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "/{clasId}", method = RequestMethod.POST)
    public DeferredResult<Object> createAccount(@PathVariable final String clasId) {
        return createAccount(clasId, null);
    }

    @RequestMapping(value = "/{clasId}/{accountId}", method = RequestMethod.POST)
    public DeferredResult<Object> createAccount(@PathVariable final String clasId, @PathVariable final String accountId) {
        log.info("createAccount({},{})", clasId, accountId);
        final ActorRef clas = clasService.getClas(clasId);
        final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
        if (clas != null) {
            final Future<Object> future = Patterns.ask(clas, new CreateAccount.RequestBuilder(clasId).accountId(accountId).build(),
                    timeout);
            future.onSuccess(new OnSuccess<Object>() {
                @Override
                public void onSuccess(final Object result) {
                    log.info("createAccount({}) : result: {}", clasId, result);
                    final CreateAccount.Response response = (CreateAccount.Response) result;
                    deferredResult.setResult(new AccountCreated(clasId, response.getAccountId(), true, "Account created"));
                }
            }, system.dispatcher());
            future.onFailure(new OnFailure() {
                @Override
                public void onFailure(final Throwable arg) throws Throwable {
                    log.error("onFailure", arg);
                    deferredResult.setErrorResult(new AccountCreated(clasId, null, false, arg.getLocalizedMessage()));
                }
            }, system.dispatcher());
        } else {
            deferredResult.setErrorResult(new AccountCreated(clasId, null, false, "CLAS does not exist"));
        }
        return deferredResult;
    }

    @RequestMapping(value = "/{clasId}/range/from/{start}/count/{count}", method = RequestMethod.POST)
    public DeferredResult<Object> createAccounts(@PathVariable final String clasId, @PathVariable final String start,
                                                 @PathVariable final String count) {
        log.info("createAccounts({},{},{})", clasId, start, count);
        final ActorRef clas = clasService.getClas(clasId);
        final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
        final int countValue = Integer.parseInt(count);

        if (countValue > 50000) {
            deferredResult.setErrorResult(String.format("count %s is to large (> 50.000)", count));
            return deferredResult;
        }

        if (clas != null) {
            final List<AccountCreated> results = Collections.synchronizedList(new LinkedList<AccountCreated>());
            final AtomicInteger resultsCounter = new AtomicInteger(0);

            for (int i = Integer.parseInt(start); i < Integer.parseInt(start) + countValue; i++) {
                final String accountId = Integer.toString(i);
                log.debug("createAccounts() : create {}", accountId);
                final Future<Object> future = Patterns.ask(clas, new CreateAccount.RequestBuilder(clasId).accountId(accountId)
                        .build(), timeout * countValue);
                future.onSuccess(new OnSuccess<Object>() {
                    @Override
                    public void onSuccess(final Object result) {
                        log.info("createAccount({}) : result: {}", clasId, result);
                        final CreateAccount.Response response = (CreateAccount.Response) result;
                        results.add(new AccountCreated(clasId, response.getAccountId(), true, String.format("Account %s created",
                                accountId)));

                        // check if all values are received
                        if (resultsCounter.incrementAndGet() >= countValue) {
                            deferredResult.setResult(results);
                        }

                    }
                }, system.dispatcher());
                future.onFailure(new OnFailure() {
                    @Override
                    public void onFailure(final Throwable arg) throws Throwable {
                        log.error("onFailure", arg);
                        deferredResult.setErrorResult(new AccountCreated(clasId, null, false, arg.getLocalizedMessage()));
                    }
                }, system.dispatcher());
            }
        } else {
            deferredResult.setErrorResult(new AccountCreated(clasId, null, false, "CLAS does not exist"));
        }
        return deferredResult;
    }

    @RequestMapping(value = "/{clasId}/{accountId}/balance", method = RequestMethod.GET)
    public DeferredResult<Object> balance(@PathVariable final String clasId, @PathVariable final String accountId) {
        log.debug("balance({},{})", clasId, accountId);
        final ActorRef clas = clasService.getClas(clasId);
        final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
        if (clas != null) {
            final Future<Object> future = Patterns.ask(clas,
                    new Balance.RequestBuilder(accountService.getAccountId(accountId)).build(), timeout);

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
            deferredResult.setErrorResult(new SaldoDTO(clasId, accountId, null, false, "CLAS does not exist"));
        }
        return deferredResult;
    }
}
