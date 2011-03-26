# -*- encoding: utf-8 -*-
$:.push File.expand_path('../lib', __FILE__)
require 'aspen/version'

Gem::Specification.new do |s|
  s.name        = 'aspen'
  s.version     = Aspen::VERSION::STRING
  s.platform    = Gem::Platform::CURRENT
  s.authors     = ['Kevin Williams']
  s.email       = ['kevwil@gmail.com']
  s.homepage    = 'http://kevwil.github.com/aspen'
  s.summary     = %q{Lightweight web server for Rack apps.}
  s.description = %q{Java NIO powered web server using Netty (http://jboss.org/netty).}

  s.rubyforge_project = 'aspen'

  s.files         = `git ls-files`.split('\n')
  s.test_files    = `git ls-files -- {test,spec,features}/*`.split('\n')
  s.executables   = `git ls-files -- bin/*`.split('\n').map{ |f| File.basename(f) }
  s.require_paths = ['lib']

  s.add_runtime_dependency 'rack'
  s.add_development_dependency 'rspec'
  s.add_development_dependency 'mocha'
  s.add_development_dependency 'rcov'
end
