module Aspen
  
  module Logging
    
    class << self
      @logger = ::Logging.logger['aspen']
      @logger.add_appenders(::Logging::Appenders.stdout)
      @logger.level = :info
      
      def debug?; @logger.level == 0 end
      def info?; @logger.level == 1 end
      def warn?; @logger.level == 2 end
      def error?; @logger.level == 3 end
      def fatal?; @logger.level == 4 end
      def off?; @logger.level == 5 end
    end
    
  end
  
end