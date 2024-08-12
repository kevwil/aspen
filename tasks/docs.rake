
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
