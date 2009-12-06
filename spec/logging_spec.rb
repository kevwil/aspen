require 'spec_helper'

class TestLogging
  include ::Aspen::Logging
end

describe ::Aspen::Logging do
  before do
    ::Aspen::Logging.silent = false
    @object = TestLogging.new
    @object.stubs(:puts)
  end
  
  it "should output debug when set to true" do
    ::Aspen::Logging.debug = true
    @object.expects(:puts)
    @object.debug 'hi'
  end

  it "should output trace when set to true" do
    ::Aspen::Logging.trace = true
    @object.expects(:puts)
    @object.trace 'hi'
  end

  it "should not output when silenced" do
    ::Aspen::Logging.silent = true
    @object.stubs(:puts)
    #@object.should_not_receive(:puts)
    @object.log 'hi'
    # ::Aspen::Logging.silent.should be_true
  end
  
  it "should not output when silenced as instance method" do
    @object.silent = true
    
    #@object.should_not_receive(:puts)
    @object.log 'hi'
  end

  it "should log errors using debug" do
    begin
      raise RuntimeError, "boo!"
    rescue RuntimeError => e
      @object.expects(:puts)
      @object.log_error(e)
    end
  end
  
  it "should be usable as module functions" do
    ::Aspen::Logging.silent = true
    ::Aspen::Logging.log "hi"
  end
  
  after do
    ::Aspen::Logging.silent = true
  end
end