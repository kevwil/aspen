# To start aspen and sinatra at http://localhost:9090:
#
#   aspen start -p 9090 -R sinatra_app.ru
#
# ref: http://blog.tannerburson.com/2009/01/multiple-sinatra-90-applications-in-one.html
#
require 'rubygems'
require 'sinatra'
require 'sinatra_app'

map "/" do
  run Main
end

map "/post" do
  run Post
end

# run Sinatra::Application