require 'java'
require File.dirname(__FILE__) + '/aspenj.jar'
import com.github.kevwil.aspen.RackProxy
import com.github.kevwil.aspen.RackUtil
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.channels.Channels
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.buffer.ChannelBufferInputStream
import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.jboss.netty.handler.codec.http.HttpVersion
require 'stringio'
require 'aspen/version'

module Aspen
  # implementation of RackProxy from Java side
  class NettyProxy
    include RackProxy

    def initialize( app )
      @app = app
    end

    def process( cxt, req )
      # need to handle chunking, keep-alive, method, url, etc
      env = {}
      RackUtil.parse_headers( cxt, req, env )
      #env.delete "HTTP_CONTENT_TYPE"
      #env.delete "HTTP_CONTENT_LENGTH"
      env["SCRIPT_NAME"] = ""  if env["SCRIPT_NAME"] == "/"
      env.delete "PATH_INFO"  if env["PATH_INFO"] == ""
      data = req.content.to_string("UTF-8").to_s
      rack_input = StringIO.new( data )
      rack_input.set_encoding( Encoding::BINARY ) if rack_input.respond_to?( :set_encoding )
      env.update( {"rack.version" => ::Aspen::VERSION::RACK,
                   "rack.input" => rack_input,
                   "rack.errors" => $stderr,

                   "rack.multithread" => true,
                   "rack.multiprocess" => false,
                   "rack.run_once" => false,

                   "rack.url_scheme" => "http",
                 } )

      g env.inspect if Logging.debug?
      status, headers, body = @app.call(env)
      g body.inspect if Logging.debug?

      resp = DefaultHttpResponse.new( HttpVersion::HTTP_1_1, HttpResponseStatus.value_of( status ) )
      headers.each do |k,vs|
        vs.split('\n').each do |v|
          resp.add_header k, v
        end
      end
      resp.content = ChannelBuffers.copied_buffer( body.join, "UTF-8" )
      g resp.inspect if Logging.debug?
      resp
    end
  end
end