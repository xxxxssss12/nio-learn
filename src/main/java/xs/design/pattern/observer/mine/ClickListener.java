package xs.design.pattern.observer.mine;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by xs on 2018/2/11
 */
public class ClickListener implements Listener {

    @Override
    public void onEventAttach(Event event) {
        System.out.println("ClickListener onEventAttach, event=" + JSON.toJSONString(event));
    }
}
