require 'optparse'
require 'yaml'

module Aspen
  
  # Error raised that will abort the process and print not backtrace.
  class RunnerError < RuntimeError; end

  # CLI runner - parse options and drive server
  class Runner
    
    COMMANDS = %w(start stop restart config)
    
    # Commands that wont load options from the config file
    CONFIGLESS_COMMANDS = %w(config)
    
    # Parsed options
    attr_accessor :options
    
    # Name of the command to be runned.
    attr_accessor :command
    
    # Arguments to be passed to the command.
    attr_accessor :arguments
    
    # Return all available commands
    def self.commands
      COMMANDS
    end

    def initialize(argv)
      @argv = argv
      
      # Default options values
      @options = {
        :chdir          => Dir.pwd,
        :environment    => 'development',
        :address        => Server::DEFAULT_HOST,
        :port           => Server::DEFAULT_PORT,
        :log            => 'log/aspen.log',
        :pid            => 'tmp/pids/aspen.pid',
        :require        => []
      }
      
      parse!
    end
    
    def parser
      # NOTE: If you add an option here make sure the key in the +options+ hash is the
      # same as the name of the command line option.
      # +option+ keys are used to build the command line to launch other processes,
      # see <tt>lib/thin/command.rb</tt>.
      @parser ||= OptionParser.new do |opts|
        opts.banner = "Usage: aspen [options] #{self.class.commands.join('|')}"

        opts.separator ""
        opts.separator "Server options:"

        opts.on("-a", "--address HOST", "bind to HOST address " +
                                        "(default: #{@options[:address]})")             { |host| @options[:address] = host }
        opts.on("-p", "--port PORT", "use PORT (default: #{@options[:port]})")          { |port| @options[:port] = port.to_i }
        opts.on("-A", "--adapter NAME", "Rack adapter to use (default: autodetect)",
                                        "(#{Rack::ADAPTERS.map{|(a,b)|a}.join(', ')})") { |name| @options[:adapter] = name }
        opts.on("-R", "--rackup FILE", "Load a Rack config file instead of " +
                                       "Rack adapter")                                  { |file| @options[:rackup] = file }
        opts.on("-c", "--chdir DIR", "Change to dir before starting")                   { |dir| @options[:chdir] = File.expand_path(dir) }
        opts.on(      "--stats PATH", "Mount the Stats adapter under PATH")             { |path| @options[:stats] = path }
        
        opts.separator ""
        opts.separator "Adapter options:"
        opts.on("-e", "--environment ENV", "Framework environment " +                       
                                           "(default: #{@options[:environment]})")      { |env| @options[:environment] = env }
        opts.on(      "--prefix PATH", "Mount the app under PATH (start with /)")       { |path| @options[:prefix] = path }
        
        opts.on("-l", "--log FILE", "File to redirect output " +                      
                                    "(default: #{@options[:log]})")                   { |file| @options[:log] = file }
        opts.on("-P", "--pid FILE", "File to store PID " +                            
                                    "(default: #{@options[:pid]})")                   { |file| @options[:pid] = file }
        opts.on("-C", "--config FILE", "Load options from config file")               { |file| @options[:config] = file }
        
        opts.separator ""
        opts.separator "Common options:"

        opts.on_tail("-r", "--require FILE", "require the library")                     { |file| @options[:require] << file }
        opts.on_tail("-D", "--debug", "Set debbuging on")                               { @options[:debug] = true }
        opts.on_tail("-V", "--trace", "Set tracing on (log raw request/response)")      { @options[:trace] = true }
        opts.on_tail("-h", "--help", "Show this message")                               { puts opts; exit }
        opts.on_tail('-v', '--version', "Show version")                                 { puts Aspen::SERVER; exit }
      end
    end
    
    # Parse the options.
    def parse!
      parser.parse! @argv
      @command   = @argv.shift
      @arguments = @argv
    end

    
    # Parse the current shell arguments and run the command.
    # Exits on error.
    def run!
      if self.class.commands.include?(@command)
        run_command
      elsif @command.nil?
        puts "Command required"
        puts @parser
        exit 1  
      else
        abort "Unknown command: #{@command}. Use one of #{self.class.commands.join(', ')}"
      end
    end
    
    # Send the command to the server.
    def run_command
      load_options_from_config_file! unless CONFIGLESS_COMMANDS.include?(@command)
      
      ##############
      # Thin is shelling out sub-processes - this doesn't translate to JRuby, does it?
      ##############
      # PROGRAM_NAME is relative to the current directory, so make sure
      # we store and expand it before changing directory.
      # Command.script = File.expand_path($PROGRAM_NAME)
      
      # Change the current directory ASAP so that all relative paths are
      # relative to this one.
      Dir.chdir(@options[:chdir])
      
      @options[:require].each { |r| ruby_require r }
      Logging.debug = @options[:debug]
      Logging.trace = @options[:trace]
      
      server = Server.new(@options)
      
      if server.respond_to?(@command)
        begin
          server.send(@command, *@arguments)
        rescue RunnerError => e
          abort e.message
        end
      else
        abort "Invalid options for command: #{@command}"
      end
    end
    
    private
      def load_options_from_config_file!
        if file = @options.delete(:config)
          YAML.load_file(file).each { |key, value| @options[key.to_sym] = value }
        end
      end
      
      def ruby_require(file)
        if File.extname(file) == '.ru'
          warn 'WARNING: Use the -R option to load a Rack config file'
          @options[:rackup] = file
        else
          require file
        end
      end
  end
  
end
