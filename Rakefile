
begin
  require 'bones'
rescue LoadError
  abort '### Please install the "bones" gem ###'
end

ensure_in_path 'lib'
require 'aspen/version'

#task :default => ['spec:rcov','spec:verify','doc:yard','notes']
task :default => ['spec:rcov','notes']
task 'gem:release' => ['java:build','spec:rcov','spec:verify','doc:yard']
task 'clean' => ['java:clean', 'doc:clean']
task 'clobber' => ['java:clobber']

Bones do
  name 'aspen'
  authors ['Kevin Williams']
  email ['kevwil@gmail.com']
  url 'http://kevwil.github.com/aspen'
  version ENV['VERSION'] || Aspen::VERSION::STRING
  rubyforge.name 'aspen'
  readme_file 'README'
  ignore_file '.gitignore'
  depend_on 'rack'
  depend_on 'g', :development => true
  depend_on 'bones', :development => true
  depend_on 'bones-git', :development => true
  depend_on 'bones-extras', :development => true
  depend_on 'rspec', :development => true
  depend_on 'mocha', :development => true
  depend_on 'rcov', :development => true
  #depend_on 'yard', :development => true

  ruby_opts.clear
  ruby_opts << '-Ilib' << '-rubygems'
  spec.opts << '--color'
  # spec.opts << '--format html:./spec_out.html'
  #rcov.threshold 80
  rcov.opts << ['--include', 'lib']
  rcov.opts << ['--exclude', 'spec']
  rcov.opts << ['--exclude', 'examples']
  rcov.opts << ['--exclude', 'rcov']
  rcov.opts << ['--exclude', 'mocha']
  rcov.opts << ['--exclude', 'rails']
  rcov.opts << ['--exclude', 'action_controller']
  # rcov.opts << ['--no-html']
  # rcov.opts << ['--text-counts']
  # rcov.opts << ['--text-coverage-diff','FILE']

  use_gmail
  enable_sudo
end

require 'fileutils'

namespace :doc do
  desc "clean up generated docs"
  task :clean do
    d = 'doc'
    y = '.yardoc'  
    FileUtils.rm_rf(d) if File.exist?(d) and File.writable?(d) and File.directory?(d)
    FileUtils.rm(y) if File.exist?(y) and File.writable?(y)
  end
end

namespace :java do

  desc "clean up java tool output"
  task :clean do
    system "cd javalib;mvn --offline clean;cd .."
  end

  desc "delete the generated jar"
  task :clobber do
    FileUtils.rm Dir.glob('lib/**/*.jar')
  end

  desc "build java code and copy jars to lib folder"
  task :build => :clean do
    system "cd javalib;mvn --offline package;ant;cd .."
  end

end

# EOF
