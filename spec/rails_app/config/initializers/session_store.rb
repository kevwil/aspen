# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_rails_app_session',
  :secret      => '7a91fb08eab716e221e1e0d4fdbd5d535a13f6234cd6d7bb96a80c3e87c548d4c68c8a71de880d6091c05b60ba5d80e6dc6193b5249862fa6fb0d131c43bc17f'
}

#config.action_controller.allow_forgery_protection = false
ActionController::Base.allow_forgery_protection = false

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
