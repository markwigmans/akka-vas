package com.chessix.vas.storm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatisticsTest {

    @Test
    public void testUpdate() throws Exception {
        final Integer update1 = 10;
        final Integer update2 = 5;
        final Integer update3 = 12;

        final Statistics s1 = new Statistics();
        final Statistics s2 = s1.update(update1);

        assertEquals(1, s2.measurements);
        assertEquals(update1, s2.min.get());
        assertEquals(update1, s2.max.get());
        assertEquals((double) update1, s2.avg.get(), 0.01);

        final Statistics s3 = s2.update(update2);

        assertEquals(2, s3.measurements);
        assertEquals(update2, s3.min.get());
        assertEquals(update1, s3.max.get());
        assertEquals((double) (update1 + update2) / 2, s3.avg.get(), 0.01);

        final Statistics s4 = s3.update(update3);

        assertEquals(3, s4.measurements);
        assertEquals(update2, s4.min.get());
        assertEquals(update3, s4.max.get());
        assertEquals((double) (update1 + update2 + update3) / 3, s4.avg.get(), 0.01);
    }
}