package xs.design.pattern.observer.mine;

import com.alibaba.fastjson.JSON;

/**
 * Created by xs on 2018/2/11
 */
public class AllEventListener implements Listener {
    @Override
    public void onEventAttach(Event event) {
        System.out.println("AllEventListener onEventAttach,event=" + JSON.toJSONString(event));
    }
}
