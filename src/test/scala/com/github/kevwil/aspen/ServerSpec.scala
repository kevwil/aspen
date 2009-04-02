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
    "a basic running server" should
    {
        doBefore
        {
            var config = new ServerConfig
            config.host = "localhost"
            config.port = 54321
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
        }
    }
}