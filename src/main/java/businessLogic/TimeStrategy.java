package businessLogic;

import dataModel.Server;
import dataModel.Task;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeStrategy implements Strategy{

    @Override
    public void addTask(List<Server> servers, Task task) {
        Server server = null;
        AtomicInteger minTime = new AtomicInteger(Integer.MAX_VALUE);
        for(Server s : servers) {
            if(s.getWaitingPeriod() < minTime.get()) {
                minTime.set(s.getWaitingPeriod());
                server = s;
            }
        }
        if(server != null) {
            task.setWaitingTime(server.getWaitingPeriod());
            server.addTask(task);
        }
    }
}
