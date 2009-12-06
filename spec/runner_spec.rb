
require 'spec_helper'

describe Aspen::Runner, "with basic options" do
  
  it "should parse options" do
    runner = Aspen::Runner.new(%w(start --pid test.pid --port 5000))
    
    runner.should_not be_nil
    runner.options.should_not be_nil
    runner.options.kind_of?(Hash).should be_true
    runner.options[:pid].should eql('test.pid')
    runner.options[:port].should eql(5000)
  end
  
  it "should parse specified command" do
    Aspen::Runner.new(%w(start)).command.should eql('start')
    Aspen::Runner.new(%w(stop)).command.should eql('stop')
    Aspen::Runner.new(%w(restart)).command.should eql('restart')
  end
  
  it "should abort on unknow command" do
    runner = Aspen::Runner.new(%w(poop))
    runner.stubs(:abort)
    
    runner.expects(:abort)
    
    runner.run!
  end
  
  it "should exit on empty command" do
    runner = Aspen::Runner.new([])
    runner.stubs(:exit)
    
    runner.expects(:exit).with(1)
    
    silence_stream(STDOUT) do
      runner.run!
    end
  end
  
  it "should warn when require a rack config file" do
    Aspen::Runner.any_instance.expects(:warn).with(regexp_matches(/WARNING/))
    
    runner = Aspen::Runner.new(%w(start -r config.ru))
    runner.run! rescue nil
    
    runner.options[:rackup].should eql('config.ru')
  end
  
  it "should require file" do
    runner = Aspen::Runner.new(%w(start -r unexisting))
    proc { runner.run! }.should raise_error(LoadError)
  end
  
  it "should remember requires" do
    runner = Aspen::Runner.new(%w(start -r rubygems -r aspen))
    runner.options[:require].should eql(%w(rubygems aspen))
  end

  it "should remember debug options" do
    runner = Aspen::Runner.new(%w(start -D -V))
    runner.options[:debug].should be_true
    runner.options[:trace].should be_true
  end

  it "should default debug and trace to false" do
    runner = Aspen::Runner.new(%w(start))
    runner.options[:debug].should_not be_true
    runner.options[:trace].should_not be_true
  end
end

 describe Aspen::Runner, 'with config file' do
   before do
     Aspen::Logging.silent = true
     @runner = Aspen::Runner.new(%w(start --config spec/configs/single.yml))
   end

   it "should load options from file with :config option" do
     @runner.send :load_options_from_config_file!

     @runner.options[:environment].should == 'production'
     @runner.options[:chdir].should == 'spec/rails_app'
     @runner.options[:port].should == 6000
     @runner.options[:pid].should == 'tmp/pids/aspen.pid'
     @runner.options[:log].should == 'log/aspen.log'
     @runner.options[:timeout].should == 60
   end

#   it "should change directory after loading config" do
#     @orig_dir = Dir.pwd
#
#     server = mock('server')
#     server.expects(:respond_to?).with('start').returns(true)
#     server.expects(:start)
#     Aspen::Server.any_instance.stubs(:new).returns(server)
#
#     expected_dir = File.expand_path('spec/rails_app')
#
#     begin
#       silence_stream(STDERR) do
#         @runner.run!
#       end
#
#       Dir.pwd.should == expected_dir
#
#     ensure
#       # any other spec using relative paths should work as expected
#       Dir.chdir(@orig_dir)
#     end
#   end
 end