package com.github.kevwil.aspen


//import org.specs.mock.JMocker
import org.specs.runner._
import org.specs.Specification

/**
 * @author kevinw
 * @since Apr 2, 2009
 */

class ServerSpecTest extends JUnit4(ServerSpec)
class ServerSpecRunner extends ConsoleRunner(ServerSpec)

object ServerSpec extends Specification// with JMocker
{
    var server:AspenServer = null
    var config = new ServerConfig
    "a basic running server" should
    {
        doFirst
        {
            config.host = "localhost"
            config.port = 54321
        }
        doBefore
        {
            server = new AspenServer(config)
        }

        doAfter
        {
            try
            {
                if( server.running ) server.stop
            }
            catch
            {
                case e:Exception => e.printStackTrace
            }
            finally
            {
                try
                {
                    server.kill
                    server = null
                }
                catch
                {
                    case e:Exception => e.hashCode
                }
            }
        }

        "error on start before init" in
        {
            server.start must throwA(new IllegalStateException)
        }

        "error on stop before init" in
        {
            server.stop must throwA(new IllegalStateException)
        }

        "not be running after init" in
        {
            server.init
            server.running mustBe false
        }

        "be running after init and start" in
        {
            server.init
            try
            {
                server.start
                server.running mustBe true
            }
            finally
            {
                server.stop
            }
        }

        "not be running after stop" in
        {
            server.init
            try
            {
                server.start
            }
            finally
            {
                server.stop
                try
                {
                    Thread.sleep( 2000 )
                }
                catch
                {
                    case e:InterruptedException => e.hashCode
                }
            }
            server.running mustBe false
        }

        "ignore stop before start" in
        {
            server.init
            server.stop
        }

        "do nothing if start called multiple times" in
        {
            server.init
            server.running mustBe false
            try
            {
                server.start
                server.running mustBe true
                server.start
                server.running mustBe true
            }
            finally
            {
                server.stop
                try
                {
                    Thread.sleep( 2000 )
                }
                catch
                {
                    case e:InterruptedException => e.hashCode
                }
                server.running mustBe false
            }
        }
    }
}