# Or with: aspen start -R config.ru
# then browse to http://localhost:3000
# 
# Check Rack::Builder doc for more details on this file format:
#  http://rack.rubyforge.org/doc/classes/Rack/Builder.html
require 'aspen'

app = proc do |env|
  # Response body has to respond to each and yield strings
  # See Rack specs for more info: http://rack.rubyforge.org/doc/files/SPEC.html
  body = ['hi!']
  
  [
    200,                                        # Status code
    { 'Content-Type' => 'text/html' },          # Response headers
    body                                        # Body of the response
  ]
end

run app

# Run with rackup:
# 1) change "run app" to "Aspen::Server.start app"
# 2) browse to http://localhost:3000