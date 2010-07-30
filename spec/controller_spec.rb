require 'spec_helper'
require 'ostruct'
include Aspen

describe Controller, 'start' do
  before do
    @controller = Controller.new(:address              => '0.0.0.0',
                                 :port                 => 8080,
                                 :pid                  => 'aspen.pid',
                                 :log                  => 'aspen.log',
                                 :timeout              => 60,
                                 :adapter              => 'rails')
    
    @server = OpenStruct.new
    @adapter = OpenStruct.new
    
    Server.expects(:new).with('0.0.0.0', 8080, @controller.options).returns(@server)
    # @server.expects(:config)
    ::Rack::Adapter::Rails.stubs(:new).returns(@adapter)
  end
  
  it "should configure server" do
    @controller.start
    
    @server.app.should == @adapter
  end
  
  it "should configure Rails adapter" do
    ::Rack::Adapter::Rails.expects(:new).with(@controller.options.merge(:root => nil))
    
    @controller.start
  end
  
  it "should mount app under :prefix" do
    @controller.options[:prefix] = '/app'
    @controller.start

    @server.app.class.to_s.should == 'Rack::URLMap'
  end

  it "should mount Stats adapter under :stats" do
    @controller.options[:stats] = '/stats'
    @controller.start
    
    @server.app.class.should == Stats::Adapter
  end
  
  it "should load app from Rack config" do
    @controller.options[:rackup] = File.dirname(__FILE__) + '/../examples/config.ru'
    @controller.start
    
    @server.app.class.should == Proc
  end

  it "should load app from ruby file" do
    @controller.options[:rackup] = File.dirname(__FILE__) + '/../examples/myapp.rb'
    @controller.start
    
    @server.app.should == Myapp
  end

  it "should throwup if rackup is not a .ru or .rb file" do
    proc do
      @controller.options[:rackup] = File.dirname(__FILE__) + '/../examples/myapp.foo'
      @controller.start
    end.should raise_error(RuntimeError, /please/)
  end

  it "should set RACK_ENV" do
    @controller.options[:rackup] = File.dirname(__FILE__) + '/../examples/config.ru'
    @controller.options[:environment] = "lolcat"
    @controller.start
    
    ENV['RACK_ENV'].should == "lolcat"
  end
    
end

describe Controller do
  
  it "should stop" do
    Server.expects(:stop)
    Controller.new({}).stop
  end
  
  it "should write configuration file" do
    silence_stream(STDOUT) do
      Controller.new(:config => 'test.yml', :port => 5000, :address => '127.0.0.1').config
    end

    File.read('test.yml').should include('port: 5000', 'address: 127.0.0.1')
    File.read('test.yml').should_not include('config: ')

    File.delete('test.yml')
  end
end