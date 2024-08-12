module Aspen
  # Raised when a feature is not supported on the
  # current platform.
  class PlatformNotSupported < RuntimeError; end
  
  # @author Kevin Williams
  # @since 1.0.0
  # @version 1.0.0
  module VERSION
    # major version
    MAJOR    = 1
    # minor version
    MINOR    = 0
    # bugfix version
    TINY     = 0

    # version digits joined as a string
    STRING   = [MAJOR, MINOR, TINY].join('.').freeze

    # Rack protocol version
    RACK     = [1, 1].freeze
  end

  # name string
  NAME    = 'aspen'.freeze
  # server name and version
  SERVER  = "#{NAME} #{VERSION::STRING}".freeze
    
  def self.win?
    RUBY_PLATFORM =~ /mswin|mingw/
  end
  
  def self.linux?
    RUBY_PLATFORM =~ /linux/
  end
  
  def self.ruby_18?
    RUBY_VERSION =~ /^1\.8/
  end
end
