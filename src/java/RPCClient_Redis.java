/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kevin
 */
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPCClient_Redis {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "mensaje";
    private String replyQueueName;

    public RPCClient_Redis() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("35.229.58.120");
        factory.setUsername("admin");
        factory.setPassword("123");
        factory.setPort(5672);

        connection = factory.newConnection();
        channel = connection.createChannel();

        replyQueueName = channel.queueDeclare().getQueue();
    }

    public String call(String message) throws IOException, InterruptedException {
        System.out.println("Enviando");
        final String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);

        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (properties.getCorrelationId().equals(corrId)) {
                    response.offer(new String(body, "UTF-8"));
                }
            }
        });

        return response.take();
    }

    public void close() throws IOException {
        connection.close();
    }

//  public static void main(String[] argv) {
//    RPCClient fibonacciRpc = null;
//    String response = null;
//    try {
//      fibonacciRpc = new RPCClient();
//
//      System.out.println(" [x] Requesting fib(30)");
//      response = fibonacciRpc.call("30");
//      System.out.println(" [.] Got '" + response + "'");
//    }
//    catch  (IOException | TimeoutException | InterruptedException e) {
//      e.printStackTrace();
//    }
//    finally {
//      if (fibonacciRpc!= null) {
//        try {
//          fibonacciRpc.close();
//        }
//        catch (IOException _ignore) {}
//      }
//    }
//  }
}
