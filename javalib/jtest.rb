require 'java'
require 'target/slf4j-api-1.5.6.jar'
require 'target/slf4j-simple-1.5.6.jar'
require 'target/netty-3.1.5.GA.jar'
require 'target/aspenj-1.0-SNAPSHOT.jar'
import com.github.kevwil.aspen.AspenServer

app = lambda { |env| [200, {'Content-Type'=>'text/html'}, 'Hello World'] }
server = AspenServer.new('localhost',8080,app)
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
