package edu.sjsu.cmpe273.lab2;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

public class PollCreateClient {
 private static final Logger logger = Logger.getLogger(PollCreateClient.class.getName());

  private final ChannelImpl channel;
  private final PollServiceGrpc.PollServiceBlockingStub blockingStub;

  public PollCreateClient(String host, int port) {
    channel =
        NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT)
            .build();
    blockingStub = PollServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTerminated(5, TimeUnit.SECONDS);
  }
  
    public void createNewPoll(String moderatorId ,String question ,String startedAt ,String expiredAt ,String[] choice ) {
   logger.info("length"+choice.length);


     if( choice == null && choice.length > 2 ){
      logger.info("null pointer");
     }
    try {
      logger.info("Will try to create poll ");
      PollRequest request = PollRequest.newBuilder().setModeratorId(moderatorId).setQuestion(question).
      setStartedAt(startedAt).
      setExpiredAt(expiredAt).
      addChoice(choice[0]).
      addChoice(choice[1]).
      build();
      PollResponse response = blockingStub.createPoll(request);
      logger.info("Poll id is " + response.getId());
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

  public static void main(String[] args) throws Exception {
    PollCreateClient client = new PollCreateClient("localhost", 50051);
    try {
      /* Access a service running on the local machine on port 50051 */
          String moderatorId ="1";
	  String question ="What type of smartphone do you have?";
	  String startedAt ="2015-03-18T13:00:00.000Z";
	  String expiredAt ="2015-03-19T13:00:00.000Z";
	  String[] choice = {"Android", "iPhone"};
      client.createNewPoll( moderatorId ,question ,startedAt ,expiredAt,choice );
    } finally {
      client.shutdown();
    }
  }
}