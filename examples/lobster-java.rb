require 'aspen'
require 'rack'
require 'rack/lobster'

class SimpleAdapter
  def call(env)
    [ 200, { 'Content-Type' => 'text/plain' }, ["hello!"] ]
  end
end

::Aspen::Logging.debug = true
::Aspen::Server.start('localhost',8080) do
  # use ::Rack::CommonLogger
  use ::Rack::Lint
  # use ::Rack::ShowExceptions
  map '/fish' do
    run ::Rack::Lobster.new
  end
  map '/test' do
    run SimpleAdapter.new
  end
  # map '/files' do
  #     run Rack::File.new('.')
  #   end
end

