package win.winlocker.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Менеджер событий
 */
public class EventManager {
    private static final Map<Class<?>, List<EventHandler>> registry = new ConcurrentHashMap<>();

    /**
     * Зарегистрировать слушателя событий
     */
    public static void register(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventTarget.class)) {
                EventTarget target = method.getAnnotation(EventTarget.class);
                Class<?>[] params = method.getParameterTypes();
                
                if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) {
                    continue;
                }

                Class<? extends Event> eventClass = (Class<? extends Event>) params[0];
                EventHandler handler = new EventHandler(listener, method, target.priority());

                registry.computeIfAbsent(eventClass, k -> new ArrayList<>())
                      .add(handler);
                
                registry.get(eventClass).sort(Comparator.comparingInt(EventHandler::getPriority).reversed());
            }
        }
    }

    /**
     * Отписать слушателя событий
     */
    public static void unregister(Object listener) {
        for (List<EventHandler> handlers : registry.values()) {
            handlers.removeIf(handler -> handler.getListener() == listener);
        }
    }

    /**
     * Вызвать событие
     */
    public static void call(Event event) {
        List<EventHandler> handlers = registry.get(event.getClass());
        if (handlers == null) {
            return;
        }

        for (EventHandler handler : handlers) {
            if (event.isCancelled() && handler.getPriority() < 0) {
                continue;
            }
            try {
                handler.invoke(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Очистить реестр
     */
    public static void clear() {
        registry.clear();
    }

    /**
     * Получить количество зарегистрированных обработчиков
     */
    public static int getHandlerCount() {
        return registry.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Получить количество событий
     */
    public static int getEventCount() {
        return registry.size();
    }

    private static class EventHandler {
        private final Object listener;
        private final Method method;
        private final int priority;

        public EventHandler(Object listener, Method method, int priority) {
            this.listener = listener;
            this.method = method;
            this.priority = priority;
        }

        public Object getListener() {
            return listener;
        }

        public int getPriority() {
            return priority;
        }

        public void invoke(Event event) throws Exception {
            method.setAccessible(true);
            method.invoke(listener, event);
        }
    }
}
