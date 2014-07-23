/******************************************************************************
 Copyright 2014 Mark Wigmans

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

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mark Wigmans
 * @see <a
 * href="http://www.journaldev.com/2676/spring-mvc-interceptors-example-handlerinterceptor-and-handlerinterceptoradapter">Interceptor
 * example</a>
 */
@Slf4j
public class RequestProcessingTimeInterceptor extends HandlerInterceptorAdapter {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {
        if (log.isDebugEnabled()) {
            final long startTime = System.currentTimeMillis();
            log.debug("Request URL::{}:: Start", request.getRequestURL());
            request.setAttribute(START_TIME, startTime);
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
                                final Exception ex) throws Exception {
        if (log.isDebugEnabled()) {
            final long startTime = (Long) request.getAttribute(START_TIME);
            log.debug("Request URL::{}:: Time Taken={} ms", request.getRequestURL(), System.currentTimeMillis() - startTime);
        }
    }
}
