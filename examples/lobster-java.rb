require 'aspen'
require 'rack'
require 'rack/lobster'

class SimpleAdapter
  def call(env)
    [ 200, { 'Content-Type' => 'text/plain' }, ["hello!"] ]
  end
end

# ::Aspen::Logging.debug = true
::Aspen::Server.start('localhost',3000) do
  use ::Rack::CommonLogger
  use ::Rack::ShowExceptions
  use ::Rack::ContentLength
  use ::Rack::Lint
  map '/fish' do
    run ::Rack::Lobster.new
  end
  map '/test' do
    run SimpleAdapter.new
  end
  map '/files' do
    run ::Rack::Directory.new('~/bin')
  end
end

