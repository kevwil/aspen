module Aspen

  class Server
    
    include_class org.jboss.netty.channel.group.DefaultChannelGroup
    include_class org.jboss.netty.channel.group.ChannelGroupFuture
    include_class org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
    include_class java.util.concurrent.Executors
    include_class org.jboss.netty.bootstrap.ServerBootstrap
    include_class org.jboss.netty.channel.Channel
    include_class com.github.kevwil.aspen.RackHttpServerPipelineFactory
    include_class com.github.kevwil.aspen.JRubyRackProxy
    
    def self.run(app, options)

      @running = false
      @all_channels = DefaultChannelGroup.new('aspen-server')
      @channel_factory = NioServerSocketChannelFactory.new(
        Executors.newCachedThreadPool, Executors.newCachedThreadPool )
      @bootstrap = ServerBootstrap.new(@channel_factory)

      @bootstrap.set_option( "child.tcpNoDelay", true )
      @bootstrap.set_option( "child.keepAlive", true )
      
      @bootstrap.set_pipeline_factory( RackHttpServerPipelineFactory.new( JRubyRackProxy.new( app ) ) )

      channel = @bootstrap.bind( new InetSocketAddress( options[:host], options[:port] ) )
      @all_channels.add channel
      @running = true
    end
    
    def self.stop
      raise("cannot stop, not running") unless @running
      begin
        future = @all_channels.close
        future.await_interruptably
        @channel_factory.release_external_resources
        @bootstrap.release_external_resources
        @running = false
      rescue
        raise "error stopping Netty channels"
      end
    end
    
  end

end

