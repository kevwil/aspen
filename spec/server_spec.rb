
require 'spec_helper'
include Aspen

describe Server, "with a basic lambda app" do

  it "should create instance with parameters" do
    host = '127.0.0.1'
    port = 8080
    app = lambda { |env| [200, {}, ['hello']] }
    s = Server.new(host, port, app)
    s.should_not be_nil
    s.host.should_not be_nil
    s.host.should eql(host)
    s.port.should_not be_nil
    s.port.should eql(port)
    s.app.should_not be_nil
    # proc can't be compared?
    # s.app.should eql(app)
    s.running?.should be_false
  end

  it "should have a start method" do
    Server.instance_method("start").should_not be_nil
  end

  it "should have a stop method" do
    Server.instance_method("stop").should_not be_nil
  end

  it "should have a running? method" do
    Server.instance_method("running?").should_not be_nil
  end

end

# EOF
