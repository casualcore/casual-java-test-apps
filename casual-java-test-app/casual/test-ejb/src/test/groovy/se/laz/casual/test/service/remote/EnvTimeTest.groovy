/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */
package se.laz.casual.test.service.remote

import spock.lang.Specification
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable

class EnvTimeTest extends Specification
{
   def 'default times'()
   {
      given:
      EnvTime envTime = EnvTime.of()
      when:
      def sleepTime = envTime.getSleepTime()
      def workTime = envTime.getWorkTime()
      then:
      sleepTime == Long.parseLong(EnvTime.DEFAULT_TIME)
      workTime == Long.parseLong(EnvTime.DEFAULT_TIME)
   }

   def 'work time'()
   {
      given:
      long workTime
      expect:
      withEnvironmentVariable(EnvTime.WORK_TIME_ENV_NAME, "${timeInMillis}").execute( {
         EnvTime envTime = EnvTime.of()
         workTime = envTime.getWorkTime()
      } )
      workTime == timeInMillis
      where:
      timeInMillis << [100L, 200L, 300L, 400L, 550L]
   }
   def 'sleep time'()
   {
      given:
      long sleepTime
      expect:
      withEnvironmentVariable(EnvTime.SLEEP_TIME_ENV_NAME, "${timeInMillis}").execute( {
         EnvTime envTime = EnvTime.of()
         sleepTime = envTime.getSleepTime()
      } )
      sleepTime == timeInMillis
      where:
      timeInMillis << [100L, 200L, 300L, 400L, 550L]
   }
}
