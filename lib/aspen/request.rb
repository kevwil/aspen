require 'socket'

module Aspen

  class Request
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

    # ChannelHandlerContext
    attr_reader :context

    # HttpRequest
    attr_reader :req

    # URL
    attr_reader :url

    def initialize(context, event)
      @context = context
      @req = event.getMessage
      @url = get_url
      @method = @req.getMethod.getName
      @qs = url.getQuery
      @name, @port = parse_host
      @scheme = url.getProtocol
      @http_ver = get_ver
      @remote_addr = get_remote_addr
      @remote_port = get_remote_port
    end

    def get_remote_port
      case @remote_addr.getPort
        when 80
          ""
        when 443
          ""
        else
          @remote_addr.getPort
      end
    end

    def get_remote_addr
      @context.getChannel.getRemoteAddress
    end

    def get_env
      env = Hash.new
      env['REQUEST_METHOD'] = @method
      env['QUERY_STRING'] = @qs
      env['SERVER_NAME'] = @name
      env['SERVER_PORT'] = @port
      env['rack.version'] = Rack::VERSION
      env['rack.url_scheme'] = @scheme
      env['HTTP_VERSION'] = @http_ver
      env["SERVER_PROTOCOL"] = @scheme
      env['REMOTE_ADDR'] = @remote_addr
      env['REMOTE_HOST'] = @remote_port

      # request.getPathInfo seems to be blank, so we're using the URI.
      env['REQUEST_PATH'] = @url.getPath
      env['PATH_INFO'] = @url.getPath
      env['SCRIPT_NAME'] = ""

      # Rack says URI, but it hands off a URL.
      env['REQUEST_URI'] = @url.toURI.toASCIIString

      # Java chops off the query string, but a Rack application will
      # expect it, so we'll add it back if present
      env['REQUEST_URI'] << "?#{env['QUERY_STRING']}" \
          if env['QUERY_STRING']

      # JRuby is like the matrix, only there's no spoon or fork().
      env['rack.multiprocess'] = false
      env['rack.multithread'] = true
      env['rack.run_once'] = false

      # Populate the HTTP headers.
      request.getHeaderNames.each do |header_name|
        header = header_name.upcase.tr('-', '_')
        env["HTTP_#{header}"] = request.getHeader(header_name)
      end

      # Rack Weirdness: HTTP_CONTENT_TYPE and HTTP_CONTENT_LENGTH
      # both need to have the HTTP_ part dropped.
      env["CONTENT_TYPE"] = env.delete("HTTP_CONTENT_TYPE") \
          if env["HTTP_CONTENT_TYPE"]
      env["CONTENT_LENGTH"] = env.delete("HTTP_CONTENT_LENGTH") \
          if env["HTTP_CONTENT_LENGTH"]

      # The input stream is a wrapper around the Java InputStream.
      env['rack.input'] = request.getInputStream.to_io

      # The output stream defaults to stderr.
      env['rack.errors'] ||= $stderr

      # All done, hand back the Rack request.
      env
    end

    def get_url
      begin
        return URL.new(@req.getUri)
      rescue
      end
    end

    def parse_host
      if @req.containsHeader('Host')
        arr = @req.getHeader('Host').split(/:/)
        return arr[0], arr[1] if arr.size > 1
        return(arr[0], 80)
      end
      host = @url.getHost
      port = @url.getPort
      return host, port unless host.nil? or port.nil?
      return Socket.gethostname, 80
    end

    # @return [major, minor]
    def get_ver
      ver = @req.getProtocolVersion
      "#{ver.getMajorVersion}.#{ver.getMinorVersion}"
    end

  end

end
