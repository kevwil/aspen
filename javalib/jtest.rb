require 'java'
#require 'target/slf4j-api-1.6.1.jar'
#require 'target/slf4j-simple-1.6.1.jar'
require 'target/netty-3.2.3.Final.jar'
require 'target/aspenj-1.0-SNAPSHOT.jar'
import com.github.kevwil.aspen.AspenServer
import com.github.kevwil.aspen.JRubyRackProxy

app = lambda { |env| [200, {'Content-Type'=>'text/html'}, 'Hello World'] }
server = AspenServer.new('localhost',8080,JRubyRackProxy.new(app))
puts "starting Aspen server ..."
begin
  server.start
  gets
rescue
  puts "an error occurred. exiting."
  server.stop
ensure
  puts "stopping Aspen server."
  server.stop
end
