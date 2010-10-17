# Adapter to run a Rails app with any supported Rack handler.
# By default it will try to load the Rails application in the
# current directory in the development environment.
#
# Only rack-based Rails (2.2.3 or later) apps are supported.
#
# Options:
#  root: Root directory of the Rails app
#  environment: Rails environment to run in (development [default], production or test)
#  prefix: Set the relative URL root.
#
# Based on http://fuzed.rubyforge.org/ Rails adapter
module Rack
  module Adapter
    class Rails
      FILE_METHODS = %w(GET HEAD).freeze

      def initialize(options={})
        @root   = options[:root]         || Dir.pwd
        @env    = options[:environment]  || 'development'
        @prefix = options[:prefix]

        load_application

        raise RuntimeError, "only Rack-based Rails apps are supported" unless rack_based?

        @rails_app = ::Rack::Builder.new {
          # use ::Rails::Rack::LogTailer unless options[:detach]
          use ::Rails::Rack::Debugger if options[:debugger]

          map "/" do
            use ::Rails::Rack::Static
            run ::ActionController::Dispatcher.new
          end
        }.to_app

        @file_app = Rack::File.new(::File.join(RAILS_ROOT, "public"))
      end

      def rack_based?
        ::Rails::VERSION::MAJOR >= 2 && ::Rails::VERSION::MINOR >= 2 && ::Rails::VERSION::TINY >= 3
      end

      def load_application
        ENV['RAILS_ENV'] = @env

        require "#{@root}/config/environment"

        if @prefix
          ActionController::Base.relative_url_root = @prefix
        end
        require 'dispatcher'
      end

      def file_exist?(path)
        full_path = ::File.join(@file_app.root, Utils.unescape(path))
        ::File.file?(full_path) && ::File.readable_real?(full_path)
      end

      def call(env)
        path        = env['PATH_INFO'].chomp('/')
        method      = env['REQUEST_METHOD']
        cached_path = (path.empty? ? 'index' : path) + ActionController::Base.page_cache_extension

        if FILE_METHODS.include?(method)
          if file_exist?(path)              # Serve the file if it's there
            return @file_app.call(env)
          elsif file_exist?(cached_path)    # Serve the page cache if it's there
            env['PATH_INFO'] = cached_path
            return @file_app.call(env)
          end
        end

        # No static file, let Rails handle it
        @rails_app.call(env)
      end
    end
  end
end
