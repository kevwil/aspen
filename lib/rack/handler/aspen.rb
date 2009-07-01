require 'rack/content-length'
require 'rack/chunked'

module Rack
  module Handler
    class Aspen
      def self.run(app, options={})
        app = Rack::Chunked.new(Rack::ContentLength.new(app))
        server = ::Aspen::Server.new( options[:Host] || '0.0.0.0',
                                      options[:Port] || 1169,
                                      app)
        yield server if block_given?
        server.start
      end
    end
  end
end