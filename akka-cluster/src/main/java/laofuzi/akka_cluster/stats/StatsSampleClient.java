package laofuzi.akka_cluster.stats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import java.util.concurrent.ThreadLocalRandom;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.Cancellable;
import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.ClusterEvent.ReachableMember;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.ReachabilityEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import laofuzi.akka_cluster.stats.StatsMessages.JobFailed;
import laofuzi.akka_cluster.stats.StatsMessages.StatsJob;
import laofuzi.akka_cluster.stats.StatsMessages.StatsResult;

public class StatsSampleClient extends AbstractActor {

  final String servicePath;
  final Cancellable tickTask;
  final Set<Address> nodes = new HashSet<Address>();

  Cluster cluster = Cluster.get(getContext().system());

  public StatsSampleClient(String servicePath) {
    this.servicePath = servicePath;
    FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
    tickTask = getContext()
        .system()
        .scheduler()
        .schedule(interval, interval, self(), "tick",
            getContext().dispatcher(), null);
  }

  //subscribe to cluster changes, MemberEvent
  @Override
  public void preStart() {
    cluster.subscribe(self(), MemberEvent.class, ReachabilityEvent.class);
  }

  //re-subscribe when restart
  @Override
  public void postStop() {
    cluster.unsubscribe(self());
    tickTask.cancel();
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
      .matchEquals("tick", t -> !nodes.isEmpty(), t -> {
        // just pick any one
        List<Address> nodesList = new ArrayList<>(nodes);
        Address address = nodesList.get(ThreadLocalRandom.current().nextInt(
          nodesList.size()));
        ActorSelection service = getContext().actorSelection(address + servicePath);
        service.tell(new StatsJob("this is the text that will be analyzed"),
          self());
      })
      .match(StatsResult.class, System.out::println)
      .match(JobFailed.class, System.out::println)
      .match(CurrentClusterState.class, state -> {
        nodes.clear();
        for (Member member : state.getMembers()) {
          if (member.hasRole("compute") && member.status().equals(MemberStatus.up())) {
            nodes.add(member.address());
          }
        }
      })
      .match(MemberUp.class, mUp -> {
        if (mUp.member().hasRole("compute"))
          nodes.add(mUp.member().address());
      })
      .match(MemberEvent.class, other -> {
        nodes.remove(other.member().address());
      })
      .match(UnreachableMember.class, unreachable -> {
        nodes.remove(unreachable.member().address());
      })
      .match(ReachableMember.class, reachable -> {
        if (reachable.member().hasRole("compute"))
          nodes.add(reachable.member().address());
      })
      .build();
  }

}
