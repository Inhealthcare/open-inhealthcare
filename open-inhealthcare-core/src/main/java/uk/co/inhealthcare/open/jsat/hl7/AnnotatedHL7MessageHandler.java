package uk.co.inhealthcare.open.jsat.hl7;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.ReflectionUtils;

import ca.uhn.hl7v2.model.Message;

class AnnotatedHL7MessageHandler {

	private Map<Class<?>, List<HL7MessageFilter>> messageFilters = new HashMap<Class<?>, List<HL7MessageFilter>>();

	public AnnotatedHL7MessageHandler(Object handler) {

		// get the public methods
		Method[] methods = handler.getClass().getMethods();
		for (Method method : methods) {

			registerIfSupportedHandlerMethod(handler, method);

		}

	}

	private void registerIfSupportedHandlerMethod(Object handler, Method method) {
		// what matches void xxx(Message) signature?
		if (method.getReturnType() == Void.TYPE) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length == 1) {
				Class<?> parameterType = parameterTypes[0];
				if (Message.class.isAssignableFrom(parameterType)) {

					HL7MessageFilter processor = new SimpleHL7MessageProcessor(
							handler, method);

					// does it include a method level annotation?
					HL7MessageHandler annotation = method
							.getAnnotation(HL7MessageHandler.class);
					if (annotation == null) {
						// do we have a class level annotation?
						annotation = handler.getClass().getAnnotation(
								HL7MessageHandler.class);
					}

					if (annotation != null) {
						String value = annotation.triggerEvent();
						if (StringUtils.isNotBlank(value)) {
							processor = new TriggerEventFilter(value, processor);
						}
					}

					List<HL7MessageFilter> list = messageFilters
							.get(parameterType);
					if (list == null) {
						list = new ArrayList<HL7MessageFilter>();
						messageFilters.put(parameterType, list);
					}
					list.add(processor);

				}
			}
		}
	}

	public void handle(String hl7MessageType, String hl7TriggerEvent,
			String hl7VersionId, Message hl7Msg) {
		List<HL7MessageFilter> filters = messageFilters.get(hl7Msg.getClass());
		if (filters != null) {
			for (HL7MessageFilter processor : filters) {
				processor.handle(hl7MessageType, hl7TriggerEvent, hl7VersionId,
						hl7Msg);
			}
		}
	}

	public Set<Class<?>> getMessages() {
		return messageFilters.keySet();
	}

	private static interface HL7MessageFilter {
		void handle(String hl7MessageType, String hl7TriggerEvent,
				String hl7VersionId, Message hl7Msg);
	}

	private static class SimpleHL7MessageProcessor implements HL7MessageFilter {

		private Method method;
		private Object handler;

		public SimpleHL7MessageProcessor(Object handler, Method method) {
			this.handler = handler;
			this.method = method;
		}

		public void handle(String hl7MessageType, String hl7TriggerEvent,
				String hl7VersionId, Message hl7Msg) {
			ReflectionUtils.invokeMethod(this.method, this.handler, hl7Msg);
		}
	}

	private static class TriggerEventFilter implements HL7MessageFilter {

		private String triggerEvent;
		private HL7MessageFilter processor;

		public TriggerEventFilter(String triggerEvent,
				HL7MessageFilter processor) {
			this.triggerEvent = triggerEvent;
			this.processor = processor;
		}

		@Override
		public void handle(String hl7MessageType, String hl7TriggerEvent,
				String hl7VersionId, Message hl7Msg) {
			if (StringUtils.equals(hl7TriggerEvent, triggerEvent)) {
				processor.handle(hl7MessageType, hl7TriggerEvent, hl7VersionId,
						hl7Msg);
			}
		}

	}

}