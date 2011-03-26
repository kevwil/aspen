# load local code before anything
$LOAD_PATH.unshift(File.join(File.dirname(__FILE__), '..', 'lib'))

require 'yaml'
require 'net/http'
require 'rack/lint'
require 'aspen'
require 'spec'

Spec::Runner.configure do |config|
  config.mock_with :mocha
end

Thread.abort_on_exception = true