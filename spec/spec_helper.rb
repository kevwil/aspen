require 'rubygems'
# gem 'rack', '=1.1.0'
gem 'rspec'
require 'spec'
# require 'g'

require 'aspen'
require 'java'
require File.dirname(__FILE__) + '/../lib/aspen/aspenj.jar'
import org.jboss.netty.handler.codec.http.DefaultHttpRequest
import org.jboss.netty.handler.codec.http.HttpMethod
import org.jboss.netty.handler.codec.http.HttpVersion
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.jboss.netty.buffer.ChannelBuffer

::Aspen::Logging.silent = true
# ::Aspen::Logging.debug = true

module Helpers
  # Silences any stream for the duration of the block.
  #
  #   silence_stream(STDOUT) do
  #     puts 'This will never be seen'
  #   end
  #
  #   puts 'But this will'
  #
  # (Taken from ActiveSupport)
  def silence_stream(stream)
    old_stream = stream.dup
    stream.reopen('/dev/null')
    stream.sync = true
    yield
  ensure
    stream.reopen(old_stream)
  end
end

Spec::Runner.configure do |config|
  config.include Helpers
  
  # == Mock Framework
  #
  # RSpec uses it's own mocking framework by default. If you prefer to
  # use mocha, flexmock or RR, uncomment the appropriate line:
  #
  config.mock_with :mocha
  # config.mock_with :flexmock
  # config.mock_with :rr
end

# EOF
