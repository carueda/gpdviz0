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
	
	void dispatchSourceAddedEvent(SourceAddedEvent event);
	void dispatchSourceRemovedEvent(SourceRemovedEvent event);
	
	void dispatchStreamAddedEvent(StreamAddedEvent event);
	void dispatchStreamRemovedEvent(StreamRemovedEvent event);
	
	void dispatchObservationAddedEvent(ObservationAddedEvent event);
	
}
