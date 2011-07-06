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
    java_import java.net.URL

    def initialize(app)
      super()
      @app = app
    end

    def messageReceived(context, event)
      req = Request.new(context, event)
      env = req.get_env
      rack_response = @app.call(env)
      resp = Response.new(context, rack_response)
      resp.write
      # handle keep-alive?
      context.get_channel.write(resp).add_listener(ChannelFutureListener::CLOSE)
      
      # resp = DefaultHttpResponse.new(HttpVersion::HTTP_1_1, HttpResponseStatus::OK)
      # buffer = ChannelBuffers.copied_buffer("hello world\r\n", Charset.for_name('UTF-8'))
      # resp.set_content(buffer)
      # resp.set_header(HttpHeaders::Names::CONNECTION, 'close')
      # resp.set_header(HttpHeaders::Names::CONTENT_TYPE, 'text/plain')
      # context.get_channel.write(resp).add_listener(ChannelFutureListener::CLOSE)
    end

  end

end
