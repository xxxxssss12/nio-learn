package xs.design.pattern.observer.v2;

/**
 * Created by xs on 2018/2/11
 */
public abstract class Observer {
    protected Subject subject;
    public abstract void update(Event event);

}
