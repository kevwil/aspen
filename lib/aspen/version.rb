module Aspen
  
  module VERSION #:nodoc:
    MAJOR    = 1
    MINOR    = 0
    TINY     = 0

    STRING   = [MAJOR, MINOR, TINY].join('.')

    RACK     = [1, 0].freeze # Rack protocol version
  end

  NAME    = 'aspen'.freeze
  SERVER  = "#{NAME} #{VERSION::STRING}".freeze
  
end