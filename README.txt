Gpdviz - Web-based Visualization of Geolocated Point Data Streams in Real-time
Carlos Rueda - MBARI

Status: Alpha 
	
Gpdviz is a Web application to visualize geolocated point data streams in
real-time. It uses a simple data model and a RESTful interface so data providers
can easily register sensor systems and notify additions/updates of streams to
the visualizer, which is updated in real-time using ajax push technology.

GpdvizClient is a high-level API for data providers to interact with a Gpdviz endpoint.

Building and running

	$ ant war
	# creates gpdviz.war. If this is deployed such that the Web interface is located at
	# http://localhost:8080/gpdviz, then the Gpdviz endpoint is http://localhost:8080/gpdviz/rest

	$ ant client-jar
	# creates _generated/gpdviz-client.jar.  Client code will need this JAR as well as the
	# Restlet JAR org.restlet.jar

	# The tests below assume the endpoint http://localhost:8080/gpdviz/rest. 
	# To indicate a different endpoint, use ``ant -Dendpoint=someURL ...''
	  
	$ ant client
	# runs a simple test of the client library. This registers a sensor system with ID "test1", so
	# open http://localhost:8080/gpdviz?ssid=test1 in your browser.
	# Then, type Enter to the ant target to advance the various notifications. See the effect
	# on your browser.

	$ ant mock-generate
	# runs a mock sensor system. This registers a sensor system with ID "mock1", so
	# open http://localhost:8080/gpdviz?ssid=mock1 in your browser.
	# A timer will exit the Generate program after a few minutes.

	$ ant mock-reset
	# convenience target to reset the mock sensor system

Open http://localhost:8080/gpdviz/rest/ in your browser to see the current list of registered
sensor systems.


Acknowledgments:
- The COMET project: http://comet.ucdavis.edu
- GChart http://code.google.com/p/gchart/
- Restlet framework: http://www.restlet.org/
- ICEPush: http://www.icepush.org/
- GWT: http://code.google.com/webtoolkit/

