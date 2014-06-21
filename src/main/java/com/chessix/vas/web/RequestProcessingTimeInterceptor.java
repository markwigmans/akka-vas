package com.chessix.vas.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 
 * @author Mark Wigmans
 *
 * @see <a href="http://www.journaldev.com/2676/spring-mvc-interceptors-example-handlerinterceptor-and-handlerinterceptoradapter">Interceptor example</a>
 */
@Slf4j
public class RequestProcessingTimeInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {
        val startTime = System.currentTimeMillis();
        log.debug("Request URL::{}:: Start", request.getRequestURL());
        request.setAttribute("startTime", startTime);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
            final Exception ex) throws Exception {
        val startTime = (Long) request.getAttribute("startTime");
        log.info("Request URL::{}:: Time Taken={} ms", request.getRequestURL(), System.currentTimeMillis() - startTime);
    }
}
