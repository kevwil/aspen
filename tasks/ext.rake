# rake-compiler gem
require 'rake/javaextensiontask'

Rake::JavaExtensionTask.new('aspen', Aspen::GemSpec) do |ext|
  ext.release   = '11' # default is 7 :(
  ext.classpath = File.read('ext/aspen/cp.txt') # need maven dependencies too
end

CLEAN.exclude %w(ext/**/pom.xml)
CLEAN.include %w(ext/**/*.{class,lst,txt,,xml,jar} ext/*/Makefile})

namespace :java do

  desc "clean up java tool output"
  task :clean do
    system "pushd ext/aspen;mvn --offline clean;popd"
  end

  desc "delete the generated jar"
  task :clobber do
    FileUtils.rm Dir.glob('lib/java/*.jar')
  end

  desc "build java code and copy jars to lib folder"
  task :build => :clean do
    system "pushd ext/aspen;mvn --offline package;cp -vX target/*.jar ../lib/java/;popd"
  end

end
