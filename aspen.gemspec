# -*- encoding: utf-8 -*-
$:.push File.expand_path("../lib", __FILE__)
require "aspen/version"

Gem::Specification.new do |s|
  s.name        = Aspen::NAME
  s.version     = Aspen::VERSION::STRING
  s.platform    = Gem::Platform::CURRENT
  s.authors     = ["Kevin Williams"]
  s.email       = ["kevwil@gmail.com"]
  s.homepage    = "https://kevwil.github.io/aspen"
  s.summary     = %q{Lightweight web server for Rack apps}
  s.description = %q{Java NIO powered web server using Netty (https://netty.io)}

  s.files         = `git ls-files`.split("\n")
  s.test_files    = `git ls-files -- {spec,examples}/*`.split("\n")
  s.executables   = `git ls-files -- bin/*`.split("\n").map{ |f| File.basename(f) }
  s.require_paths = ["lib"]
  
  s.add_runtime_dependency "rack"
  s.add_development_dependency "rspec"
  s.add_development_dependency "mocha"
  s.add_development_dependency "simplecov"
  # s.add_development_dependency "ruby-debug-base"
  # s.add_development_dependency "ruby-debug"
  # s.add_development_dependency "ruby-debug-ide"
  s.add_development_dependency "yard"
end
