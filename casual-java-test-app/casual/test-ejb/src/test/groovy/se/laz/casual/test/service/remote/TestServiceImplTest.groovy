/*
 * Copyright (c) 2022 - 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */
package se.laz.casual.test.service.remote


import se.laz.casual.api.buffer.CasualBuffer
import se.laz.casual.api.buffer.type.CStringBuffer
import se.laz.casual.api.flags.AtmiFlags
import se.laz.casual.api.flags.ErrorState
import se.laz.casual.api.flags.Flag
import se.laz.casual.jca.inbound.handler.InboundRequest
import se.laz.casual.jca.inbound.handler.InboundResponse
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration
import java.time.Instant

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable

class TestServiceImplTest extends Specification
{
   @Shared
   Instant start
   @Shared
   Instant end
   def 'no JAVA_FORWARD_SERVICE_NAME, throws'()
   {
      given:
      TestServiceImpl instance = new TestServiceImpl(Mock(TpCaller))
      when:
      instance.forward(Mock(InboundRequest))
      then:
      thrown(ForwardDefinitionMissingException)
   }

   def 'JAVA_FORWARD_SERVICE_NAME defined, forwards call to that service'()
   {
      given:
      def javaForwardServiceName = 'forwardService'
      def buffer = Mock(CasualBuffer)
      def returnBuffer = Mock(CasualBuffer)
      def inboundRequest = Mock(InboundRequest){
         1 * getBuffer() >> {
            buffer
         }
      }
      def flags = Flag.of(AtmiFlags.NOFLAG)
      def tpCaller = Mock(TpCaller){
         1 * makeTpCall(javaForwardServiceName, buffer, flags) >> {
            returnBuffer
         }
      }
      def instance = new TestServiceImpl(tpCaller)
      when:
      InboundResponse actual
      withEnvironmentVariable(TestServiceImpl.JAVA_FORWARD_ENV_NAME, javaForwardServiceName).execute( {
         actual = instance.forward(inboundRequest)
      } )
      then:
      actual.getBuffer() == returnBuffer
   }

   @Unroll
   def 'work #timeInMillis'()
   {
      given:
      Instant start
      Instant end
      expect:
      withEnvironmentVariable(EnvTime.WORK_TIME_ENV_NAME, "${timeInMillis}").execute( {
          TestServiceImpl instance = new TestServiceImpl(Mock(TpCaller))
         CStringBuffer buffer = CStringBuffer.of('Test data')
         InboundRequest request = InboundRequest.of('nice service', buffer)
         start = Instant.now()
         instance.work(request)
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
         TestServiceImpl instance = new TestServiceImpl(Mock(TpCaller))
         CStringBuffer buffer = CStringBuffer.of('Test data')
         InboundRequest request = InboundRequest.of('nice service', buffer)
         start = Instant.now()
         instance.sleep(request)
         end = Instant.now()
      } )
      Duration.between(start, end).toMillis() >= timeInMillis
      where:
      timeInMillis << [100L, 200L, 300L, 400L, 550L]
   }

   def 'commit'()
   {
      given:
      TestServiceImpl instance = new TestServiceImpl(Mock(TpCaller))
      CStringBuffer buffer = CStringBuffer.of('Test data')
      InboundRequest request = InboundRequest.of('nice service', buffer)
      when:
      InboundResponse response = instance.commit(request)
      then:
      response.errorState == ErrorState.OK
   }

   def 'rollback'()
   {
      given:
      TestServiceImpl instance = new TestServiceImpl(Mock(TpCaller))
      CStringBuffer buffer = CStringBuffer.of('Test data')
      InboundRequest request = InboundRequest.of('nice service', buffer)
      when:
      InboundResponse response = instance.rollback(request)
      then:
      response.errorState == ErrorState.TPESVCFAIL
   }

   def 'sink'()
   {
      given:
      TestServiceImpl instance = new TestServiceImpl(Mock(TpCaller))
      CStringBuffer buffer = CStringBuffer.of('Test data')
      InboundRequest request = InboundRequest.of('nice service', buffer)
      when:
      instance.sink(request)
      then:
      noExceptionThrown()
   }

}
