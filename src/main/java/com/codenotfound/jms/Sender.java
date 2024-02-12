package com.codenotfound.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Sender {

  private static final Logger logger =
      LoggerFactory.getLogger(Sender.class);

  @Autowired
  private JmsTemplate jmsTemplate;

  //@Autowired
  //private RetryTemplate retryTemplate;

  //ScheduledExecutorService scheduledExecutorService= Executors.newSingleThreadScheduledExecutor();
  //Executor executor =  Executors.newFixedThreadPool(5);
/*
  Executor executor =  Executors.newFixedThreadPool(5);

  @Scheduled(cron = "* * * * * *")
  @Async
  public void send(){
    String msg = "hello"+System.currentTimeMillis();
    logger.info("sending msg{}");
    CompletableFuture.runAsync(() -> {
      RetryCallback<Void, JmsException> retryCallback = new RetryCallback<Void, JmsException>() {
        @Override
        public Void doWithRetry(RetryContext retryContext) throws JmsException {
          try {
            jmsTemplate.convertAndSend("destination", msg);
            logger.info("message sent {}",msg);
          } catch (JmsException e) {
            if (retryContext.getRetryCount() >= 3) { // Customize the number of retries as needed
              logger.error("msg to be retried {}",msg);
              throw new RuntimeException("max  attempts exhausted");
            } else {
              logger.info("retry attempt {}",retryContext.getRetryCount()+1);
              throw e; // Re-throw the exception to trigger a retry
            }
          }
          return null;
        }
      };

      retryTemplate.execute(retryCallback);
    }).handle((result, ex) -> {
      if (ex != null) {
        if(ex instanceof RuntimeException){
          retryExceptionHandle(ex);
        }else{
          handleRetryException(ex);
        }

      }
      return null;
    });
  }

  private void retryExceptionHandle(Throwable e) {
    logger.info("Retry Exception handled: " + e.getMessage());
  }

  private void handleRetryException(Throwable ex) {
    if (ex instanceof RetryException) {
      // Retry attempts exhausted, handle the exception
      // You can log the error, send to DLQ (Dead Letter Queue), etc.
      System.out.println("Retry Exception handled: " + ex.getMessage());
    } else if(ex instanceof UncategorizedJmsException){
      // Handle other exceptions that are not related to retries
      // For example, you can decide to retry or propagate the exception based on your application logic
      System.out.println("Other exception occurred: " + ex.getMessage());
    }

  }*/

  /*
  CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
    try {
        // Your long-running task
    } catch (Exception e) {
        // Handle exceptions, if necessary
    }
});

// Schedule a task to cancel the CompletableFuture after a timeout
scheduledExecutorService.schedule(() -> {
    if (!completableFuture.isDone()) {
        // Cancel the CompletableFuture and interrupt the thread
        completableFuture.cancel(true);
    }
}, timeoutInSeconds, TimeUnit.SECONDS);
   */

/*
  @Scheduled(cron = "0/2 * * * * *")
  @Async
  public void send(){
    String msg = "hello"+System.currentTimeMillis();
    logger.info("msg to be sent {}",msg);

    CompletableFuture<Void> sendFuture = new CompletableFuture<>();
    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
      try{
        logger.info("started sending msg {}",msg);
        jmsTemplate.convertAndSend("destination", msg);
        logger.info("message sent {}",msg);
        sendFuture.complete(null);
      }catch (Exception e) {
        logger.error("error occurred {}",e.getMessage());
        sendFuture.completeExceptionally(e); // Mark the send operation as completed with an exception
      }
    });

      scheduledExecutorService.schedule(() -> {
        if (!completableFuture.isDone()) {
          // The send operation is still running, so let's try to interrupt it

            completableFuture.cancel(true); // Interrupt the CompletableFuture

            sendFuture.completeExceptionally(new TimeoutException("Send operation timed out"));
            logger.info("cancelling the task");

        }
      }, 1, TimeUnit.SECONDS);
      try {
        completableFuture.join();
      }catch (CancellationException e){
        logger.error("error handled");
      }

    sendFuture.handle((unused, throwable) -> {
      if(throwable!=null){
        handleRetryException(throwable);
      }
      return null;
    });
  }
*/
/*
  @Scheduled(cron = "0/2 * * * * *")
  @Async
  public void send() {
    String msg = "hello" + System.currentTimeMillis();
    logger.info("msg to be sent {}", msg);
    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(()->{
        jmsTemplate.convertAndSend("destination", msg);
        logger.info("message sent {}", msg);
    },executor);
      scheduledExecutorService.schedule(() -> {
        if (!completableFuture.isDone()) {
          completableFuture.cancel(true); // Cancel the CompletableFuture
         //completableFuture.completeExceptionally(new ResendException(msg));
        }
      }, 250, TimeUnit.MILLISECONDS); // Timeout after 1 seconds

    try {
      completableFuture.join(); // Wait for the task to complete or be canceled

    } catch (CancellationException e) {
      // Handle the CancellationException gracefully
      logger.error("message resend handled");
      resendMessage(msg);
    }
  }

 */

  /*@Scheduled(cron = "0/20 * * * * *")
  @Async
  public void send() {
    String msg = "hello" + System.currentTimeMillis();
    logger.info("msg to be sent {}", msg);
    sendMessage(msg);
  }

  private void sendMessage(String msg) {
    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(()->{
      jmsTemplate.convertAndSend("destination", msg);
      logger.info("message sent {}", msg);
    },executor);
    scheduledExecutorService.schedule(() -> {
      if (!completableFuture.isDone()) {
        completableFuture.cancel(true); // Cancel the CompletableFuture
        //completableFuture.completeExceptionally(new ResendException(msg));
      }
    }, 250, TimeUnit.MILLISECONDS); // Timeout after 1 seconds

    try {
      completableFuture.join(); // Wait for the task to complete or be canceled
    } catch (CancellationException e) {
      // Handle the CancellationException gracefully
      logger.info("message failure handled");
        logger.info("message retry initiated");
      retryWithCount(msg);
      logger.info("message retry completed");
        logger.info("resending msg to DLT");
        resendMessage(msg);
    }
  }

  private void retryWithCount(String msg) {
    boolean messageSent = false;
    int i=0;
    do{
      if(!messageSent) {
        messageSent = retryMessage(msg,i+1);
      }else{
        break;
      }
      i++;
    }while(i<3);
  }

  private boolean retryMessage(String msg, int count) {
    logger.info("retrying msg {} attempt {}",msg, count);
    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(()->{
      jmsTemplate.convertAndSend("destination", msg);
      logger.info("message resent with retry {} attempt {}", msg,count);
    },executor);
    scheduledExecutorService.schedule(() -> {
      if (!completableFuture.isDone()) {
        completableFuture.cancel(true); // Cancel the CompletableFuture
        //completableFuture.completeExceptionally(new ResendException(msg));
      }
    }, 250, TimeUnit.MILLISECONDS); // Timeout after 1 seconds

    try {
      completableFuture.join(); // Wait for the task to complete or be canceled
      return true;
    } catch (CancellationException e) {
      // Handle the CancellationException gracefully
      logger.info("message retry failure handled attempt {}",count);
      return false;
    }

  }

  private void resendMessage(String msg) {
    logger.info("{} to be resent", msg);
  }

  private void retryExceptionHandle(Throwable e) {
    logger.info("Retry Exception handled: " + e.getMessage());
  }

  private void handleRetryException(Throwable ex) {
    if (ex instanceof RetryException) {
      // Retry attempts exhausted, handle the exception
      // You can log the error, send to DLQ (Dead Letter Queue), etc.
      logger.error("Retry Exception handled: {}", ex.getMessage());
    } else if (ex instanceof UncategorizedJmsException) {
      // Handle other exceptions that are not related to retries
      // For example, you can decide to retry or propagate the exception based on your application logic
      logger.error("Other exception occurred: {}", ex.getMessage());
    }
    else if (ex instanceof TimeoutException) {
      // Handle other exceptions that are not related to retries
      // For example, you can decide to retry or propagate the exception based on your application logic
      logger.error("Timeout exception occurred: {}",  ex.getMessage());
    }
  } */


  //@Scheduled(cron = "0/20 * * * * *")
  public void send() {
    String msg = "hello" + System.currentTimeMillis();
    logger.info("msg to be sent {}", msg);
    sendMessage(msg);
  }

  private void sendMessage(String msg) {
    try{
      jmsTemplate.convertAndSend("destination", msg);
      logger.info("message sent {}", msg);
    }catch (JmsException e){
      writeDataToFile(msg);
    }

  }

  synchronized private void writeDataToFile(String msg) {
    //System.out.println("data written to file "+msg);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("reprocess.txt",true))) {
      writer.write(msg);
      writer.newLine();
      System.out.println("Data written to file successfully."+msg);
    } catch (IOException e) {
      System.out.println("Error writing data to file: " + e.getMessage());
    }
  }

}