# Overview

Noxy is an HTTP proxy infrastructure for Java 8 based on Netty and LittleProxy 
including reverse proxies and forward proxies.

Noxy is designed for *production* use in devops roles meaning it has a full
configuration file syntax, admin REST API, integrated metrics via dropwizard
metrics (which work especially well when integrated with KairosDB), and HTTP 
healthchecks.

It's an alternative to using nginx/haproxy in Java and provides a a package
that's daemonized with a configuration vocabulary and designed to be deployed
into production out of the box.
  
Noxy will be  used within Spinn3r for most of our HTTP infrastructure.

# noxy-reverse

The ```noxy-reverse``` module provides a daemon via a Debian package that 
provides a load balancing reverse proxy. 

It supports ```checks``` whereby we periodically perform TCP connections to the
backend servers to verify that they are up and online.

# noxy-forward

The ```noxy-forward``` module provides a daemon and Debian package that 
provides forward proxying - AKA you can configure an HTTP client to send / proxy
all requests to the Internet through the centralized proxy instance.

# Features

- Ability to log HTTP requests

- admin REST API for determining the status of the reverse proxy