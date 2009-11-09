
require File.join(File.dirname(__FILE__), %w[spec_helper])

describe Aspen do

  it "should load the jruby class" do
    Object.const_defined?('Aspen').should be_true
    ::Aspen.class.should eql(Module)
    ::Aspen.constants.should include("VERSION")
    ::Aspen.const_defined?('Server').should be_true
    ::Aspen::Server.class.should eql(Class)
  end

  it "should have a constructor with optional setup parameters" do
    ::Aspen::Server.should respond_to('new')
    # what is the arity for optional parameters?
    ::Aspen::Server.method('initialize').arity.should eql(-1)
  end

  it "should have a host attribute" do
    ::Aspen::Server.instance_method("host").should_not be_nil
  end

  it "should have a port attribute" do
    ::Aspen::Server.instance_method("port").should_not be_nil
  end

  it "should create instance with parameters" do
    host = '127.0.0.1'
    port = 8080
    app = lambda { |env| [200, {}, 'hello'] }
    s = ::Aspen::Server.new(host, port, app)
    s.should_not be_nil
    s.host.should_not be_nil
    s.host.should eql(host)
    s.port.should_not be_nil
    s.port.should eql(port)
    s.app.should_not be_nil
    # proc can't be compared?
    # s.app.should eql(app)
  end

  it "should have a start method" do
    ::Aspen::Server.instance_method("start").should_not be_nil
  end

  it "should have a stop method" do
    ::Aspen::Server.instance_method("stop").should_not be_nil
  end

end

# EOF
