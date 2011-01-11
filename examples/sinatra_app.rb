require 'sinatra/base'

class Main < Sinatra::Base
  get '/' do
    'Hello from Main.'
  end
end

class Post < Sinatra::Base
  get '/' do
    'Hello from Post.'
  end
  
  get '/list' do
    "<p>item 1</p>\n<p>item 2</p>\n<p>...</p>"
  end
  
end
