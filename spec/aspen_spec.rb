
require File.join(File.dirname(__FILE__), %w[spec_helper])

describe Aspen do

  it "should load the jruby class" do
    Object.const_defined?('Aspen').should be_true
    ::Aspen.class.should eql(Module)
    ::Aspen.constants.should include("VERSION")
    ::Aspen.const_defined?('AspenServer').should be_true
    ::Aspen::AspenServer.class.should eql(Class)
  end

  it "should have a constructor with optional setup parameters" do
    ::Aspen::AspenServer.should respond_to('new')
    # what is the arity for optional parameters?
    ::Aspen::AspenServer.method('initialize').arity.should eql(-1)
  end

  it "should have a host attribute" do
    ::Aspen::AspenServer.instance_method("host").should_not be_nil
  end

  it "should have a port attribute" do
    ::Aspen::AspenServer.instance_method("port").should_not be_nil
  end

  it "should create instance with parameters" do
    host = '127.0.0.1'
    port = 8080
    s = ::Aspen::AspenServer.new(host, port)
    s.should_not be_nil
    s.host.should eql(host)
    s.port.should eql(port)
  end

  it "should have an adapter= method with one argument" do
    m = ::Aspen::AspenServer.instance_method("adapter=")
    m.should_not be_nil
    m.arity.should eql(1)
  end

  it "should have a verbose= method with one argument" do
    m = ::Aspen::AspenServer.instance_method("verbose=")
    m.should_not be_nil
    m.arity.should eql(1)
  end

  it "should have a start method" do
    ::Aspen::AspenServer.instance_method("start").should_not be_nil
  end

  it "should have a stop method" do
    ::Aspen::AspenServer.instance_method("stop").should_not be_nil
  end

end

# EOF
