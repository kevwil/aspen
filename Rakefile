require 'rake'
require 'rake/clean'
load 'aspen.gemspec'

require 'bundler'
Bundler::GemHelper.install_tasks

# load tasks from files in tasks/
Dir['tasks/**/*.rake'].each { |rake| load rake unless rake == 'tasks/ext.rake' }
# need to build maven classpath file before loading ext.rake
system 'pushd ext/aspen;mvn dependency:build-classpath -Dmdep.outputFile=cp.txt;popd'
load 'tasks/ext.rake'

# task :default        => ['java:build', :spec]
# task :build          => ['java:build', :spec]
# task :clean          => ['java:clean', 'java:clobber', 'doc:clean']
task :default        => [:build]
task :build          => [:compile, :spec]
task :clean          => ['java:clean', 'java:clobber', 'doc:clean']
