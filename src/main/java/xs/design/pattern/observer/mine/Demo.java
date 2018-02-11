package xs.design.pattern.observer.mine;

import java.util.Arrays;

/**
 * Created by xs on 2018/2/11
 */
public class Demo {
    public static void main(String[] args) {
        EventSource source = new EventSource();
        Element element1 = new Element("btn_query", "查询", "查询1");
        Element element2 = new Element("a_baidu", "跳转百度", "www.baidu.com");
        Listener all = new AllEventListener();
        Listener clickListener = new ClickListener();
        source.bind(clickListener, Event.CLICK);
        source.bind(all, Arrays.asList(Event.INIT, Event.CLICK, Event.DESTORY));
        source.init();
        source.click(element1);
        source.click(element2);
        source.destory();
    }
}
