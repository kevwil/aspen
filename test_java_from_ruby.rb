require 'java'
require 'lib/aspen/aspenj.jar'
import com.github.kevwil.aspen.AspenServer

app = lambda { |env| [200, {'Content-Type'=>'text/html'}, ['Hello World']] }
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
