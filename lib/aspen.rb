require 'java'

jars = File.join(File.dirname(__FILE__), 'java', '*.jar')
Dir[jars].each { |j| require j }

require 'fileutils'
require 'timeout'
require 'stringio'
require 'time'
require 'forwardable'
require 'openssl'
require 'rack'

module Aspen
  autoload :Command,      "aspen/command"
  autoload :Connection,   "aspen/connection"
  autoload :Daemonizable, "aspen/daemonizing"
  autoload :Logging,      "aspen/logging"
  autoload :Headers,      "aspen/headers"
  autoload :Request,      "aspen/request"
  autoload :Response,     "aspen/response"
  autoload :Runner,       "aspen/runner"
  autoload :Server,       "aspen/server"
  autoload :Stats,        "aspen/stats"
  
  module Backends
    autoload :Base,       "aspen/backends/base"
    autoload :TcpServer,  "aspen/backends/tcp_server"
    autoload :UnixServer, "aspen/backends/unix_server"
  end
  
  module Controllers
    autoload :Cluster,    "aspen/controllers/cluster"
    autoload :Controller, "aspen/controllers/controller"
    autoload :Service,    "aspen/controllers/service"
  end
end

require "aspen/version"
require "aspen/statuses"
require "rack/adapter/loader"

module Rack
  module Adapter
    autoload :Rails, "rack/adapter/rails"
  end
end
