/*
 * Copyright (c)  2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */
package se.laz.casual.test.service.remote

import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration
import java.time.Instant
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable

class DelayerTest extends Specification
{
   @Unroll
   def 'work #timeInMillis'()
   {
      given:
      Instant start
      Instant end
      expect:
      withEnvironmentVariable(EnvTime.WORK_TIME_ENV_NAME, "${timeInMillis}").execute( {
         Delayer delayer = Delayer.of()
         start = Instant.now()
         delayer.spinUntilDone()
         end = Instant.now()
      } )
      Duration.between(start, end).toMillis() >= timeInMillis
      where:
      timeInMillis << [100L, 200L, 300L, 400L, 550L]
   }
   @Unroll
   def 'sleep #timeInMillis'()
   {
      given:
      Instant start
      Instant end
      expect:
      withEnvironmentVariable(EnvTime.SLEEP_TIME_ENV_NAME, "${timeInMillis}").execute( {
         Delayer delayer = Delayer.of()
         start = Instant.now()
         delayer.sleep()
         end = Instant.now()
      } )
      Duration.between(start, end).toMillis() >= timeInMillis
      where:
      timeInMillis << [100L, 200L, 300L, 400L, 550L]
   }
}
