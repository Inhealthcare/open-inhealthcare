package uk.co.inhealthcare.open.jsat.hl7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ca.uhn.hl7v2.model.Message;

public class HL7MessageDispatcher implements InitializingBean,
		ApplicationContextAware {

	private static final Logger logger = LoggerFactory
			.getLogger(HL7MessageDispatcher.class);

	@Autowired
	private UnsupportedHL7MessageHandler unsupportedHL7MessageHandler;
	private ApplicationContext applicationContext;

	private Map<Class<?>, List<AnnotatedHL7MessageHandler>> hl7MessageHandlers = new HashMap<Class<?>, List<AnnotatedHL7MessageHandler>>();

	public void process(@Header("CamelHL7MessageType") String hl7MessageType,
			@Header("CamelHL7TriggerEvent") String hl7TriggerEvent,
			@Header("CamelHL7VersionId") String hl7VersionId, Message hl7Msg) {

		logger.debug("Processing message {} {} {} {}", hl7MessageType,
				hl7TriggerEvent, hl7VersionId, hl7Msg);

		// look through supported classes based on the message type. registered
		// through application context interface
		List<AnnotatedHL7MessageHandler> handlers = hl7MessageHandlers
				.get(hl7Msg.getClass());
		if (handlers == null) {
			unsupportedHL7MessageHandler.onMessage(hl7Msg);
		} else {
			for (AnnotatedHL7MessageHandler annotatedHL7MessageHandler : handlers) {
				annotatedHL7MessageHandler.handle(hl7MessageType,
						hl7TriggerEvent, hl7VersionId, hl7Msg);
			}
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {

		Map<String, Object> beansWithAnnotation = applicationContext
				.getBeansWithAnnotation(HL7MessageHandler.class);

		for (Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
			registerHandler(entry.getValue());
		}

	}

	private void registerHandler(Object handler) {

		AnnotatedHL7MessageHandler annotatedHL7MessageHandler = new AnnotatedHL7MessageHandler(
				handler);
		Set<Class<?>> messages = annotatedHL7MessageHandler.getMessages();
		for (Class<?> message : messages) {
			List<AnnotatedHL7MessageHandler> handlers = hl7MessageHandlers
					.get(message);
			if (handlers == null) {
				handlers = new ArrayList<AnnotatedHL7MessageHandler>();
				hl7MessageHandlers.put(message, handlers);
			}
			handlers.add(annotatedHL7MessageHandler);
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
