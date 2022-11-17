/**
 * The SimpleQueueSender class consists only of a main method,
 * which sends several messages to a queue. 
 * 
 * Run this program in conjunction with SimpleQueueReceiver. 
 * Specify a queue name on the command line when you run the 
 * program. By default, the program sends one message. Specify 
 * a number after the queue name to send that number of messages. 
 */

package cohort10.jms.producer;

import javax.jms.Connection; 
import javax.jms.ConnectionFactory; 
import javax.jms.Destination; 
import javax.jms.JMSException; 
import javax.jms.MessageProducer; 
import javax.jms.Session; 
import javax.jms.TextMessage; 
import javax.naming.Context; 
import javax.naming.InitialContext; 
import javax.naming.NamingException; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
/**
 * A simple polymorphic JMS producer which can work with Queues or Topics which 
 * uses JNDI to lookup the JMS connection factory and destination.
 */ 
public final class SimpleProducer { 
   private static final Logger LOG = LoggerFactory.getLogger(SimpleProducer.class); 
   private SimpleProducer() {}  
  
   /**
    * @param args the destination name to send to and optionally, the number of 
    * messages to send 
    */ 
   public static void main(String[] args) { 
      Context jndiContext = null;
      ConnectionFactory connectionFactory = null;
      Connection connection = null;
      Session session; 
      Destination destination = null;
      MessageProducer producer; 
      String destinationName; 
      final int numMsgs;  
      
      if ((args.length < 1) || (args.length > 2)) { 
         LOG.info("Usage: java SimpleProducer <destination-name> [<number-of-messages>]"); System.exit(1); 
      }  
      
      destinationName = args[0]; 
      LOG.info("Destination name is " + destinationName); 
      
      if (args.length == 2) { 
         numMsgs = (new Integer(args[1])).intValue(); 
      } else { 
         numMsgs = 1; 
      } 
      
      /*
       * Create a JNDI API InitialContext object 
       */
      try { 
         jndiContext = new InitialContext(); 
      } catch (NamingException e) { 
         LOG.info("Could not create JNDI API context: " + e.toString()); 
         System.exit(1); 
      }
      
      /* 
       * Look up connection factory and destination. 
       */
      try { 
         connectionFactory = (ConnectionFactory)jndiContext.lookup("ConnectionFactory"); 
         destination = (Destination)jndiContext.lookup(destinationName); 
      } catch (NamingException e) { 
         LOG.info("JNDI API lookup failed: " + e); 
         System.exit(1); 
      }  
      
      /*
       * Create connection. Create session from connection; false means 
       * session is not transacted. Create sender and text message. Send 
       * messages, varying text slightly. Send end-of-messages message. 
       * Finally, close the connection. 
       */ 
      try { 
         connection = connectionFactory.createConnection(); 
         session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); 
         producer = session.createProducer(destination); 
         TextMessage message = session.createTextMessage();  
         for (int i = 0; i < numMsgs; i++) { 
            message.setText("This is message " + (i + 1)); 
            LOG.info("Sending message: " + message.getText()); producer.send(message); 
         }  
         
         /*
          * Send a non-text control message indicating end of messages. 
          */ 
         producer.send(session.createMessage()); 
      } catch (JMSException e) { 
         LOG.info("Exception occurred: " + e); 
      } finally { 
         if (connection != null) { 
            try { 
               connection.close(); 
            } catch (JMSException ignored) {
            } 
         } 
      } 
   } 
}