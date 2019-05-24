package com.laofuzi.akka.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;


public class HelloActor extends AbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	
	public static class Hello {
		private String who;
		public Hello(String who) {
			this.who=who;
			
		}
		public String getWho() {
			return this.who;
		}
	}
	
	static public Props props() {
		return Props.create(HelloActor.class, () -> new HelloActor());
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
		        .match(HelloActor.Hello.class, this::hello)
		        .build();
	}
	
	public String hello(Hello h) {
		String hello = "Hello " + h.getWho();
		log.info(hello);
		
		//this.context().sender().tell(hello, sender);
		//pipe(hello, getContext().dispatcher()).to(getSender());
		return hello;
	}

}
