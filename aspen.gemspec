# -*- encoding: utf-8 -*-
$:.push File.expand_path("../lib", __FILE__)
require "aspen/version"

Aspen::GemSpec ||= Gem::Specification.new do |s|
  s.name        = Aspen::NAME
  s.version     = Aspen::VERSION::STRING
  s.platform    = Gem::Platform::CURRENT
  s.authors     = ["Kevin Williams"]
  s.email       = ["kevwil@gmail.com"]
  s.homepage    = "https://kevwil.github.io/aspen"
  s.summary     = %q{Lightweight web server for Rack apps on JRuby}
  s.description = %q{Java NIO powered web server using Netty (https://netty.io)}

  s.metadata = {
    'source_code_uri' => 'https://github.com/kevwil/aspen',
    'changelog_uri'   => 'https://github.com/kevwil/aspen/blob/master/CHANGELOG.md'
  }

  s.files         = `git ls-files`.split("\n")
  s.test_files    = `git ls-files -- {spec,examples}/*`.split("\n")
  s.executables   = `git ls-files -- bin/*`.split("\n").map{ |f| File.basename(f) }
  s.require_paths = ["lib"]
  s.bindir        = "bin"

  s.add_dependency 'rack',    '>= 1'
  s.add_dependency 'daemons', '~> 1.0', '>= 1.0.9' unless Aspen.win?
  s.add_development_dependency "rspec"
  s.add_development_dependency "mocha"
  s.add_development_dependency "simplecov"
  s.add_development_dependency "yard"
end
