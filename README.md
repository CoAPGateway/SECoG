# Semantically Enhanced CoAP Gateway (SECoG)
Semantically Enhanced CoAP Gateway (SECoG) is a middleware which is located between Low-power and Lossy Networks (LLNs) and the Internet.A SECoG enables users to create a simple or complex CoAP service mashup via a single HTTP request.

The SECoG was implemented in Java (version 1.8.0) based on open source Apache Tomcat, Apache Jena TDB, and Californium (https://github.com/eclipse/californium).

A real-world testbed was constructed in a campus building using Arduino Mega 2560 boards and various sensors. Arduino boards were equipped with Ethernet shields and ran microcoap (https://github.com/1248/microcoap) to act as servers. The code for coap servers are provided in the folder "coapServer".
