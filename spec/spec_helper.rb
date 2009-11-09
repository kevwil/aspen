require 'java'
require 'rubygems'
gem 'rspec'
require 'spec'

require File.expand_path(
    File.join(File.dirname(__FILE__), %w[.. lib aspen]))

::Aspen::Logging.silent = true

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
