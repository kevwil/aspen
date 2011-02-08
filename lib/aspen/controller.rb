require 'yaml'

module Aspen
  
  # Error raised that will abort the process and print not backtrace.
  class RunnerError < RuntimeError; end

  # Raised when a mandatory option is missing to run a command.
  class OptionRequired < RunnerError
    def initialize(option)
      super("#{option} option required")
    end
  end

  # Raised when an option is not valid.
  class InvalidOption < RunnerError; end
  
  # controls a server
  class Controller
    include Logging

    # Command line options passed to the aspen script
    attr_accessor :options

    # create a new instance
    # @param [Hash] command-line options
    def initialize(options)
      @options = options
    end

    # build server and start it
    def start
      server = Server.new(@options[:Host] || @options[:address], @options[:Port] || @options[:port], @options)
      if @options[:rackup]
        server.app = load_rackup_config
      else
        server.app = load_adapter
      end

      # If a prefix is required, wrap in Rack URL mapper
      server.app = ::Rack::URLMap.new(@options[:prefix] => server.app) if @options[:prefix]

      # If a stats URL is specified, wrap in Stats adapter
      server.app = Stats::Adapter.new(server.app, @options[:stats]) if @options[:stats]

      server.start
    end

    # stop the server
    def stop
      Server.stop
    end

    # clean up options and write to file
    def config
      config_file = @options.delete(:config) || raise(OptionRequired, :config)

      # Stringify keys
      @options.keys.each { |o| @options[o.to_s] = @options.delete(o) }

      File.open(config_file, 'w') { |f| f << @options.to_yaml }
      log ">> Wrote configuration to #{config_file}"
    end
    

    private
      def load_adapter
        adapter = @options[:adapter] || Rack::Adapter.guess(@options[:chdir])
        log ">> Using #{adapter} adapter"
        Rack::Adapter.for(adapter, @options)
      rescue Rack::AdapterNotFound => e
        raise InvalidOption, e.message
      end

      def load_rackup_config
        ENV['RACK_ENV'] = @options[:environment]
        case @options[:rackup]
        when /\.rb$/
          Kernel.load(@options[:rackup])
          Object.const_get(File.basename(@options[:rackup], '.rb').capitalize.to_sym)
        when /\.ru$/
          rackup_code = File.read(@options[:rackup])
          eval("Rack::Builder.new {( #{rackup_code}\n )}.to_app", TOPLEVEL_BINDING, @options[:rackup])
        else
          raise "Invalid rackup file.  please specify either a .ru or .rb file"
        end
      end
  end
end