Gpdviz - Web-based Visualization of Geolocated Point Data Streams in Real-time
Carlos Rueda - MBARI

Status: Alpha 
	
Gpdviz is a Web application to visualize geolocated point data streams in
real-time. It uses a simple data model and a RESTful interface so data providers
can easily register sensor systems and notify additions/updates of streams to
the Gpdviz registry. The visualizer is updated in real-time using ajax push technology.

Assuming Gpdviz is deployed at http://localhost:8080/gpdviz, users will visualize a
particular sensor system by providing the corresponding sensor system ID:
  http://localhost:8080/gpdviz?ssid=<sensorSystemID>

Data providers will use the REST endpoint http://localhost:8080/gpdviz/rest to register 
sensor systems and notify updates against the http://localhost:8080/gpdviz deployment. 
The gpdviz-client.jar library provides a high-level API for data providers to interact 
with a Gpdviz endpoint. Test programs are included for demonstration.

Pre-requisites

Subversion, Apache Ant, and Google Web Toolkit are installed on your system.

Building and running
 
	# get a copy of the code:
	$ svn checkout http://gpdviz.googlecode.com/svn/trunk/gpdviz
	$ cd gpdviz
	# have your own build.properties:
	# cp sample.build.properties build.properties
	# edit build.properties as appropriate for your system.
	
	# create gpdviz.war:
	$ ant war
	# If this is deployed such that the Web interface is located at http://localhost:8080/gpdviz, 
	# then the Gpdviz endpoint will be http://localhost:8080/gpdviz/rest

	# create client library gpdviz-client.jar:
	$ ant client-jar
	# creates _generated/gpdviz-client.jar.  Client code will need this JAR as well as the
	# Restlet JAR org.restlet.jar (a copy of this library is under war/WEB-INF/lib/)

	# The tests below assume the endpoint http://localhost:8080/gpdviz/rest. 
	# To indicate a different endpoint, use ``ant -Dendpoint=someURL ...''
	  
	$ ant client
	# runs a simple test of the client library. This registers a sensor system with ID "test1", so
	# open http://localhost:8080/gpdviz?ssid=test1 in your browser.
	# Then, type Enter in the console where ant is running to advance the various notifications. 
	# See the effect on your browser.

	$ ant mock-generate
	# runs a mock sensor system. This registers a sensor system with ID "mock1", so
	# open http://localhost:8080/gpdviz?ssid=mock1 in your browser.
	# The mock system generates random events and will automatically exit after a few minutes.

	$ ant mock-reset
	# convenience target to reset the mock sensor system. Resetting means that all streams are 
	# removed but the sensor system itself remains registered. The effect of this, of course,
	# is immediately reflected in your browser.

Open http://localhost:8080/gpdviz/rest/ to see the current list of registered sensor systems.

Note
- Registered sensor systems are not yet persisted.
- the REST interface is not yet documented; see the javadoc for GpdvizClient and the demo
  programs for usage details.


Acknowledgments:
- The COMET project: http://comet.ucdavis.edu
- GChart http://code.google.com/p/gchart/
- Restlet framework: http://www.restlet.org/
- ICEPush: http://www.icepush.org/
- GWT: http://code.google.com/webtoolkit/

