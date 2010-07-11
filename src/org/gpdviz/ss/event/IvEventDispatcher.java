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
	void dispatchNewStreamEvent(NewStreamEvent event);
	void dispatchNewValueEvent(NewValueEvent event);
	
}
