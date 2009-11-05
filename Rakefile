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
require 'aspen/version'

# task :default => ['java:build','spec:run']
task :default => ['spec:run']

PROJ.name = 'aspen'
PROJ.authors = ['Kevin Williams']
PROJ.email = ['kevwil@gmail.com']
PROJ.url = 'http://kevwil.github.com/aspen'
PROJ.version = ENV['VERSION'] || Aspen::VERSION::STRING
PROJ.rubyforge.name = 'aspen'
PROJ.readme_file = 'README'
PROJ.ignore_file = '.gitignore'

PROJ.spec.opts << '--color'
PROJ.rcov.opts << ['--exclude', 'rcov']
PROJ.rcov.opts << ['--exclude', 'mocha']


require 'fileutils'

namespace :java do

  desc "clean up java tool output"
  task :clean do

    system "cd javalib;mvn --offline clean;cd .."
    FileUtils.rm Dir.glob('lib/*.jar')
  end

  desc "build java code and copy jars to lib folder"
  task :build => :clean do

    system "cd javalib;mvn --offline package;cd .."

    FileUtils.cp Dir.glob('javalib/target/*.jar'), 'lib'
    FileUtils.mv Dir.glob('lib/aspenj*.jar').first, 'lib/aspenj.jar'

  end

end

# EOF
