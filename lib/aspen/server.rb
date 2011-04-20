module Aspen

  class Server
    
    java_import org.jboss.netty.channel.group.DefaultChannelGroup
    java_import org.jboss.netty.channel.group.ChannelGroupFuture
    java_import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
    java_import java.util.concurrent.Executors
    java_import org.jboss.netty.bootstrap.ServerBootstrap
    java_import org.jboss.netty.channel.Channel
    java_import java.net.InetSocketAddress
#    java_import com.github.kevwil.aspen.RackHttpServerPipelineFactory
#    java_import com.github.kevwil.aspen.JRubyRackProxy
    
    def self.run(app, options = {})

      config = {:host => '0.0.0.0', :port => 3000}
      config.merge options
      @running = false
      @all_channels = DefaultChannelGroup.new('aspen-server')
      @channel_factory = NioServerSocketChannelFactory.new(
        Executors.newCachedThreadPool, Executors.newCachedThreadPool )
      @bootstrap = ServerBootstrap.new(@channel_factory)

      @bootstrap.set_option( "child.tcpNoDelay", true )
      @bootstrap.set_option( "child.keepAlive", true )
      
#      @bootstrap.set_pipeline_factory( RackHttpServerPipelineFactory.new( JRubyRackProxy.new( app ) ) )
      @bootstrap.set_pipeline_factory ::Aspen::AspenPipelineFactory.new(app)

      channel = @bootstrap.bind( InetSocketAddress.new( config[:host], config[:port].to_i ) )
      @all_channels.add channel
      @running = true
    end
    
    def self.stop
      raise("cannot stop, not running") unless @running
      begin
        future = @all_channels.close
        future.await_uninterruptibly
        @channel_factory.release_external_resources
        @bootstrap.release_external_resources
        @running = false
        true
      rescue Exception => e
        raise "error stopping Netty channels: #{e.message}"
      end
    end
    
  end

end

# Register server with Rack
Rack::Handler.register 'aspen', 'Aspen::Server'
