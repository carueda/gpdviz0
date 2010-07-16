package org.gpdviz.ss.event;

/**
 * Event dispatcher.
 * 
 * @author Carlos Rueda
 */
public interface IvEventDispatcher {

	void dispatchSensorSystemRegisteredEvent(SensorSystemRegisteredEvent event);
	void dispatchSensorSystemUnregisteredEvent(SensorSystemUnregisteredEvent event);
	void dispatchSensorSystemResetEvent(SensorSystemResetEvent sensorSystemResetEvent);
	
	void dispatchNewSourceEvent(NewSourceEvent event);
	void dispatchSourceRemovedEvent(SourceRemovedEvent event);
	
	void dispatchNewStreamEvent(NewStreamEvent event);
	void dispatchStreamRemovedEvent(StreamRemovedEvent event);
	
	void dispatchNewValueEvent(NewValueEvent event);
	
}
