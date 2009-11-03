module Aspen
  
  module Logging
    
    class << self
      attr_reader :log
      attr_writer :debug
      @log = ::Logging.logger['aspen']
      @log.add_appenders(::Logging::Appenders.stdout)
      @log.level = :info
      
      def debug?; @log.level == 0 end
      def info?; @log.level == 1 end
      def warn?; @log.level == 2 end
      def error?; @log.level == 3 end
      def fatal?; @log.level == 4 end
      def off?; @log.level == 5 end
      
      def debug=(value)
        @log.level = :debug if value
      end
    end
    
  end
  
end