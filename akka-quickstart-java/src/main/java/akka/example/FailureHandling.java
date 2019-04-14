package akka.example;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * 
 * The default supervisor strategy is to stop and restart the child
 *
 */
public class FailureHandling {

	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("failure-handling");
		ActorRef supervisingActor = system.actorOf(SupervisingActor.props(), "supervising-actor");
		supervisingActor.tell("failChild", ActorRef.noSender());

	}

}

class SupervisingActor extends AbstractActor {
	static Props props() {
		return Props.create(SupervisingActor.class, SupervisingActor::new);
	}

	ActorRef child = getContext().actorOf(SupervisedActor.props(), "supervised-actor");

	@Override
	public Receive createReceive() {
		return receiveBuilder().matchEquals("failChild", f -> {
			child.tell("fail", getSelf());
		}).build();
	}
}

class SupervisedActor extends AbstractActor {
	static Props props() {
		return Props.create(SupervisedActor.class, SupervisedActor::new);
	}

	@Override
	public void preStart() {
		System.out.println("supervised actor started");
	}

	@Override
	public void postStop() {
		System.out.println("supervised actor stopped");
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().matchEquals("fail", f -> {
			System.out.println("supervised actor fails now");
			throw new Exception("I failed!");
		}).build();
	}
}