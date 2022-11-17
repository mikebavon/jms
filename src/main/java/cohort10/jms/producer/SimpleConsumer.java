package cohort10.jms.producer;

import javax.jms.*;
import javax.naming.*;
import java.util.Date;
import java.util.Properties;

public class SimpleConsumer {
    private static InitialContext ictx = null;
    private static QueueConnectionFactory queueConnectionFactory = null;


    public static void main(String... args) {

        QueueConnection queueConnection = null;
        QueueSession queueSession = null;
        Queue queue = null;
        QueueReceiver queueReceiver = null;
        TextMessage message = null;

        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL,"tcp://localhost:61616");
            ictx = new InitialContext(props);
            queueConnectionFactory = (QueueConnectionFactory) ictx.lookup("ConnectionFactory");

            queue = (Queue) ictx.lookup("dynamicQueues/queue.Cohort10");
        } catch (NamingException e) {
            System.out.println("Not able to create JNDI context: " + e.getMessage());
            System.exit(1);
        }

        try {
            queueConnection = queueConnectionFactory.createQueueConnection();
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            queueReceiver = queueSession.createReceiver(queue);
            queueConnection.start();
            while (true) {
                Message m = queueReceiver.receive(1);
                if (null != m && m instanceof TextMessage) {
                    message = (TextMessage) m;
                    System.out.println(message.getText() + " Received at " + new Date());
                }
            }
        } catch (JMSException jmse) {
            System.out.println(jmse.getMessage());
        } finally {
            if (null != queueConnection) {
                try {
                    queueConnection.close();
                } catch (JMSException jmse) {}
            }
        }
    }
}
