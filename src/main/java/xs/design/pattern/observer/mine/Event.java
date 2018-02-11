package xs.design.pattern.observer.mine;

/**
 * Created by xs on 2018/2/11
 */
public class Event {
    public static final int INIT = 0;
    public static final int CLICK = 1;
    public static final int DESTORY = -1;

    private int code;
    private Object data;

    public Event(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
