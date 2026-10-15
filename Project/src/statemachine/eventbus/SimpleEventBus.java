package statemachine.eventbus;

import statemachine.model.Event;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer; 

public class SimpleEventBus {
    private final String name;
    private final Queue<Event> eventQueue = new ConcurrentLinkedQueue<>();
    private Consumer<Event> outboundEventPublisher; 

    public SimpleEventBus(String name) {
        this.name = name;
    }

    public void postEvent(Event event) {
        System.out.println("[" + name + "] EventBus received: " + event.getEventType());
        eventQueue.offer(event);
    }

    public void setOutboundEventPublisher(Consumer<Event> publisher) {
        this.outboundEventPublisher = publisher;
    }

    public void postOutboundEvent(Event event) {
        if (outboundEventPublisher != null) {
            System.out.println("[" + name + "] EventBus publishing outbound event: " + event.getEventType());
            outboundEventPublisher.accept(event);
        } else {
            System.err.println("[" + name + "] WARNING: No outbound event publisher set for event: " + event.getEventType());
        }
    }

    public void processEvents(Consumer<Event> eventConsumer) {
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            if (event != null) {
                eventConsumer.accept(event);
            }
        }
    }
}