require 'java'

jars = File.join(File.dirname(__FILE__), 'java', '*.jar')
Dir[jars].each { |j| require j }

require 'rack'
require 'aspen/version'

module Aspen
  # should I use autoload?
  require 'aspen/runner'
  require 'aspen/server'
end
