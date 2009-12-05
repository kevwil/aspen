module Aspen
  
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
    STRING   = [MAJOR, MINOR, TINY].join('.')

    # Rack protocol version
    RACK     = [1, 0].freeze
  end

  # name string
  NAME    = 'aspen'.freeze
  # server name and version
  SERVER  = "#{NAME} #{VERSION::STRING}".freeze
  
end