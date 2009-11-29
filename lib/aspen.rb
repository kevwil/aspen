require 'aspen/version'

module Aspen

  autoload :Logging, 'aspen/logging'
  autoload :NettyProxy, 'aspen/netty_proxy'
  autoload :Server, 'aspen/server'
  autoload :Runner, 'aspen/runner'

end  # module Aspen

require 'rack'
require 'rack/adapter/loader'

module Rack
  module Adapter
    autoload :Rails, 'rack/adapter/rails'
  end
end

# EOF
