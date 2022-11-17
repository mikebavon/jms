package cohort10.jms.producer;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Date;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App 
{
    private static InitialContext ictx = null;
    private static QueueConnectionFactory queueConnectionFactory = null;

    public static void main( String[] args )
    {
        QueueConnection queueConnection = null;
        QueueSession queueSession = null;
        Queue queue = null;
        QueueSender queueSender = null;
        TextMessage message = null;

        try {

            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL,"tcp://localhost:61616");
            ictx = new InitialContext(props);
            queueConnectionFactory = (QueueConnectionFactory) ictx.lookup("ConnectionFactory");
            queue = (Queue) ictx.lookup("dynamicQueues/queue.Boniface");

        }catch (Exception ex) {
            System.out.println("<<<<<<<<<<<<<<<<<<Unable to load JNDI Resource>>>>>>>>>>>>>>>>>>>>>>>>>>");
            ex.printStackTrace();
        }

        try {
            queueConnection = queueConnectionFactory.createQueueConnection();
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            queueSender = queueSession.createSender(queue);
            message = queueSession.createTextMessage();
            for (int i = 0; i < 10; i++) {
                message.setText("Test Message " + ++i);
                System.out.println(message.getText() + " Sent At " + new Date());
                queueSender.send(message);
            }
        } catch (JMSException e) {
            System.out.println(e.getMessage());
        } finally {
            if (null != queueConnection) {
                try {
                    queueConnection.close();
                } catch (JMSException e) {}
            }
        }
    }
}
