package net.etfbl.server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MessageServerInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
	    String[] queueNames = new String[]{"messages", "messages2", "messages3", "messages4"};
	    for (String queueName : queueNames) {
	        MessageServer server = new MessageServer(queueName);
	        try {
				server.start();
			} catch (IOException | TimeoutException e) {
				e.printStackTrace();
			}
	    }
	}


	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	    ServletContext servletContext = sce.getServletContext();
	    List<MessageServer> messageServers = (List<MessageServer>) servletContext.getAttribute("messageServers");

	    if (messageServers != null) {
	        for (MessageServer messageServer : messageServers) {
	            messageServer.close();
	        }
	    }
	}

}

