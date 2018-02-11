package xs.design.pattern.observer;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by xs on 2018/2/11
 */
public class Demo {
    public static void main(String[] args) {
        ChangeListener listener = new ChangeListener();
        Context.addListener(listener);
        Context.sendNotification(new Event(Event.INSTALLED, new JSONObject()));
    }
}
