package xs.design.pattern.observer.mine;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * 事件源
 * Created by xs on 2018/2/11
 */
public class EventSource {
    public final Object BIND_LOCK = new Object();
    public Map<Integer, Set<Listener>> eventListenerMap = new HashMap<>();

    public void bind(Listener listener, int eventCode) {
        synchronized (BIND_LOCK) {
            Set<Listener> listenerSet = eventListenerMap.get(eventCode);
            if (listenerSet == null || listenerSet.isEmpty()) {
                listenerSet = new HashSet<>();
            }
            listenerSet.add(listener);
            eventListenerMap.put(eventCode, listenerSet);
        }
    }

    public void bind(Listener listener, List<Integer> eventCodeList) {
        eventCodeList.forEach(eventCode -> bind(listener, eventCode));
    }

    public EventSource() {
        System.out.println("EventSource create!");
    }

    public void init() {
        System.out.println("EventSource init!");
        notifyListener(Event.INIT, null);
    }

    public void click(Element element) {
        System.out.println("EventSource click!element=" + JSON.toJSONString(element));
        notifyListener(Event.CLICK, element);
    }

    public void destory() {
        System.out.println("EventSource destory!");
        notifyListener(Event.DESTORY, null);
    }
    private void notifyListener(int eventCode, Object data) {
        Set<Listener> listenerSet = eventListenerMap.get(eventCode);
        if (listenerSet != null && !listenerSet.isEmpty()) {
            Event event = new Event(eventCode, data);
            listenerSet.forEach(listener -> listener.onEventAttach(event));
        }
    }
}
