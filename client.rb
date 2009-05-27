#!/usr/bin/env ruby
  
require 'socket'
require 'openssl'

socket = TCPSocket.open('127.0.0.1', 9999)

ssl_context = OpenSSL::SSL::SSLContext.new()
ssl_context.cert = OpenSSL::X509::Certificate.new(File.open('client.pem'))
ssl_context.key = OpenSSL::PKey::RSA.new(File.open('client.key'))
# if the client key is encrypted / has a password, you can add the password:
# ssl_context.key = OpenSSL::PKey::RSA.new(File.open('client.key'), 'my_secret')

ssl_socket = OpenSSL::SSL::SSLSocket.new(socket, ssl_context)
ssl_socket.sync_close = true

ssl_socket.connect

puts ssl_socket.gets
ssl_socket.puts
puts ssl_socket.gets

ssl_socket.close
