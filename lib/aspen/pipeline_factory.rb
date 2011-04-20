module Aspen

  class AspenPipelineFactory
    java_import org.jboss.netty.channel.ChannelPipelineFactory
    include ChannelPipelineFactory

    java_import org.jboss.netty.channel.ChannelPipeline
    java_import org.jboss.netty.channel.Channels
    java_import org.jboss.netty.handler.codec.http.HttpRequestDecoder
    java_import org.jboss.netty.handler.codec.http.HttpResponseEncoder
    java_import org.jboss.netty.handler.logging.LoggingHandler
    java_import org.jboss.netty.logging.InternalLogLevel

    def initialize(app)
      @app = app
    end

    def getPipeline
      pl = Channels.pipeline
      pl.add_last("logger", LoggingHandler.new(InternalLogLevel::DEBUG))
      pl.add_last("decoder", HttpRequestDecoder.new)
      pl.add_last("encoder", HttpResponseEncoder.new)
      pl.add_last("handler", ChannelHandler.new(@app))
      pl
    end
  end

end
