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
package com.chessix.vas.storm;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 *
 */
@Slf4j
class Statistics {
    final int measurements;
    final Optional<Integer> min;
    final Optional<Integer> max;
    final Optional<Double> avg;

    public Statistics() {
        this.measurements = 0;
        this.min = Optional.empty();
        this.max = Optional.empty();
        this.avg = Optional.empty();
    }

    public Statistics(int measurements, int min, int max, double avg) {
        this.measurements = measurements;
        this.min = Optional.of(min);
        this.max = Optional.of(max);
        this.avg = Optional.of(avg);
    }

    public Statistics update(final Integer amount) {
        final int measurements = this.measurements + 1;
        final int min = this.min.map(i -> Math.min(i, amount)).orElse(amount);
        final int max = this.max.map(i -> Math.max(i, amount)).orElse(amount);
        final double avg = this.avg.map(i -> i + (amount - i) / measurements).orElse(amount.doubleValue());

        return new Statistics(measurements, min, max, avg);
    }

    @Override
    public String toString() {
        return "Statisics{" +
                "measurements=" + measurements +
                ", min=" + min +
                ", max=" + max +
                ", avg=" + avg +
                '}';
    }
}