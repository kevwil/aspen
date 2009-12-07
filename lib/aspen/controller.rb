require 'yaml'

module Aspen

  # Raised when an option is not valid.
  class InvalidOption < RunnerError; end
  
  # controls a server
  class Controller
    include Aspen::Logging

    # Command line options passed to the aspen script
    attr_accessor :options

    # create a new instance
    # @param [Hash] command-line options
    def initialize(options)
      @options = options
    end

    # build server and start it
    def start
    end

    # stop the server
    def stop

    end

    # restart the server
    def restart
      
    end

    # clean up options and write to file
    def config

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