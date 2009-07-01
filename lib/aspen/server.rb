module Aspen
  
  # the Server is the launching point and core of Aspen.
  #
  # == TCP Server
  # It will listen for incoming requests on TCP sockets
  # at <tt>host:port</tt> by specifying +host+ and +port+.
  #
  # == Rack Application
  # All requests will be processed through +app+ that must be a valid Rack adapter.
  # A valid Rack adapter (application) must respond to <tt>call(env#Hash)</tt> and
  # return an array of <tt>[status, headers, body]</tt>.
  class Server
    include Logging
    
    # default values
    DEFAULT_HOST = "0.0.0.0"
    DEFAULT_PORT = 1169
    
    # Application (Rack adapter) called with the request that produces the response.
    attr_accessor :app
    
    # IP address of to bind sockets to, usually the address of the host,
    # the loopback address (127.0.0.1), or the 'all' binding address
    # of 0.0.0.0 (which is the default).
    attr_accessor :host
    
    # port number for the server to listen on
    attr_accessor :port
    
    def initialize(*args, &block)
      host, port, options = DEFAULT_HOST, DEFAULT_PORT, {}
      
      # Guess each parameter by its type so they can be
      # received in any order
      args.each do |arg|
        case arg
        when Fixnum, /^\d+$/  then @port = arg.to_i
        when String           then @host = arg
        when Hash             then options = arg
        else
          @app = arg if arg.respond_to?(:call)
        end
      end
      
      # Allow using Rack builder as a block
      @app = Rack::Builder.new(&block).to_app if block
      
      # If in debug mode, wrap in logger adapter
      @app = Rack::CommonLogger.new(@app) if Logging.debug?
    end
    
    # Lil' shortcut to turn this:
    #
    #   Server.new(...).start
    #
    # into this:
    #
    #   Server.start(...)
    #
    def self.start(*args, &block)
      new(*args, &block).start!
    end
    
    # Start the server and listen for connections.
    def start
      raise ArgumentError, 'app required' unless @app

      log   ">> Thin web server (v#{VERSION::STRING} codename #{VERSION::CODENAME})"
      debug ">> Debugging ON"
      trace ">> Tracing ON"

      log ">> Maximum connections set to #{@backend.maximum_connections}"
      log ">> Listening on #{@backend}, CTRL+C to stop"

      @backend.start
    end
    alias :start! :start

    # == Gracefull shutdown
    # Stops the server after processing all current connections.
    # As soon as this method is called, the server stops accepting
    # new requests and wait for all current connections to finish.
    # Calling twice is the equivalent of calling <tt>stop!</tt>.
    def stop
      if running?
        @backend.stop
        unless @backend.empty?
          log ">> Waiting for #{@backend.size} connection(s) to finish, " +
                "can take up to #{timeout} sec, CTRL+C to stop now"
        end
      else
        stop!
      end
    end

    # == Force shutdown
    # Stops the server closing all current connections right away.
    # This doesn't wait for connection to finish their work and send data.
    # All current requests will be dropped.
    def stop!
      log ">> Stopping ..."

      @backend.stop!
    end
    
  end
  
end