package com.laofuzi.akka.service;


import java.time.Duration;
import java.util.concurrent.CompletionStage;

import org.springframework.stereotype.Service;

import com.laofuzi.akka.actors.HelloActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;


@Service
public class DemoService {
	private ActorSystem actorSystem;
	private final Duration totalTimeout;

	public DemoService() {
		this.totalTimeout = Duration.ofSeconds(1);
		actorSystem = ActorSystem.create("akkaDemoSystem");
		

		try {

//			actorSystem.actorOf(HelloActor.props(), "helloActor");
		} catch (Exception e) {
			actorSystem.terminate();
		}
	}
	
	
	public CompletionStage<String>  hello(String who) {
		 ActorRef helloActor = actorSystem.actorOf(HelloActor.props());
		 CompletionStage<Object> completionStage = Patterns.ask(helloActor,
	        		new HelloActor.Hello(who), totalTimeout).toCompletableFuture();
	        return completionStage.thenApply(resp -> resp.toString())
	                .exceptionally(ex -> ex.getMessage());
	}

}
