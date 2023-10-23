# SNG COAP HTTP PROXY

This repository contains a CoAP-HTTP Proxy for use with the Stand-alone Notifying Gateway (SNG).
The proxy forwards CoAP messages from devices to a HTTP server, and returns the HTTP server's response back to the devices.

The CoAP server side is implemented using the [Eclipse Californium™ framework](https://eclipse.dev/californium/).

The HTTP client side is implemented using a [Spring WebClient](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html)

The payload received from the devices is expected to be a JSON string in [CBOR](https://cbor.io/) format and should contain an ID field.

Currently the proxy performs a simple validation on the ID contained in the payload and the Identity used by the device when setting up a connection, before forwarding the message to the HTTP server.

Although some basic support for DTLS is already present, the implementation of PSK management, is still work in progress and will become available in a later version.
