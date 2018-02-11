package xs.design.pattern.observer;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by xs on 2018/2/11
 */
public class ChangeListener implements Listener {

    @Override
    public void onChange(Event event) {
        System.out.println("ChangeListener.onChange!!!event=" + JSON.toJSONString(event));
    }
}
