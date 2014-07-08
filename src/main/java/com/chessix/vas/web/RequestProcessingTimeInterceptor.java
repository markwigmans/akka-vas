package com.chessix.vas.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Mark Wigmans
 *
 * @see <a
 *      href="http://www.journaldev.com/2676/spring-mvc-interceptors-example-handlerinterceptor-and-handlerinterceptoradapter">Interceptor
 *      example</a>
 */
@Slf4j
public class RequestProcessingTimeInterceptor extends HandlerInterceptorAdapter {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {
        final long startTime = System.currentTimeMillis();
        log.debug("Request URL::{}:: Start", request.getRequestURL());
        request.setAttribute(START_TIME, startTime);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
            final Exception ex) throws Exception {
        final long startTime = (Long) request.getAttribute(START_TIME);
        log.info("Request URL::{}:: Time Taken={} ms", request.getRequestURL(), System.currentTimeMillis() - startTime);
    }
}
