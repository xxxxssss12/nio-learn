package xs.spring.quickstart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xs.testpackage.MsgOutput;

@Component
public class MessagePrinter {
    @Autowired
    private MessageService service;

    @Autowired
    private MsgOutput msgOutput;
    public void printMessage() {
        System.out.println(this.service.getMessage());
        System.out.println(msgOutput.getMsg());
    }
}
