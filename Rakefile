# Look in the tasks/setup.rb file for the various options that can be
# configured in this Rakefile. The .rake files in the tasks directory
# are where the options are used.

begin
  require 'bones'
  Bones.setup
rescue LoadError
  begin
    load 'tasks/setup.rb'
  rescue LoadError
    raise RuntimeError, '### please install the "bones" gem ###'
  end
end

ensure_in_path 'lib'
require 'aspen'

task :default => 'spec:run'

PROJ.name = 'aspen'
PROJ.authors = ['Kevin Williams']
PROJ.email = ['kevwil@gmail.com']
PROJ.url = 'http://kevwil.github.com/aspen'
PROJ.version = ENV['VERSION'] || Aspen::VERSION
PROJ.rubyforge.name = 'aspen'
PROJ.readme_file = 'README'
PROJ.ignore_file = '.gitignore'

PROJ.spec.opts << '--color'
PROJ.rcov.opts << ['--exclude', 'rcov']
PROJ.rcov.opts << ['--exclude', 'mocha']

# EOF
