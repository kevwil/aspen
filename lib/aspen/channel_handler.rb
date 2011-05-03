module Aspen
  java_import org.jboss.netty.channel.SimpleChannelUpstreamHandler

  class ChannelHandler < SimpleChannelUpstreamHandler
    java_import org.jboss.netty.channel.ChannelHandlerContext
    java_import org.jboss.netty.channel.MessageEvent
    java_import org.jboss.netty.handler.codec.http.HttpRequest
    java_import org.jboss.netty.handler.codec.http.DefaultHttpResponse
    java_import org.jboss.netty.handler.codec.http.HttpVersion
    java_import org.jboss.netty.handler.codec.http.HttpResponseStatus
    java_import org.jboss.netty.handler.codec.http.HttpHeaders
    java_import org.jboss.netty.buffer.ChannelBuffers
    java_import org.jboss.netty.channel.ChannelFutureListener
    java_import java.nio.charset.Charset

    def initialize(app)
      super()
      @app = app
    end

    def messageReceived(context, event)
      resp = DefaultHttpResponse.new(HttpVersion::HTTP_1_1, HttpResponseStatus::OK)
      buffer = ChannelBuffers.copied_buffer('hello world\r\n', Charset.for_name('UTF-8'))
      resp.set_content(buffer)
      resp.set_header(HttpHeaders::Names::CONNECTION, 'close')
      context.get_channel.write(resp).add_listener(ChannelFutureListener::CLOSE)
    end

  end

end
