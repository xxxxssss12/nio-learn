package xs.design.pattern.observer.v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xs on 2018/2/11
 */
public class Subject {
    private List<Observer> observers
            = new ArrayList<Observer>();
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        notifyAllObservers(new Event(Event.INSTALLED, state));
    }

    public void attach(Observer observer){
        observers.add(observer);
    }

    public void notifyAllObservers(Event event){
        for (Observer observer : observers) {
            observer.update(event);
        }
    }
}