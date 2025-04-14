package businessLogic;

import dataModel.Server;
import dataModel.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy{

    @Override
    public void addTask(List<Server> servers, Task task) {
        Server server = null;
        int minQueue = Integer.MAX_VALUE;
        for(Server s : servers) {
            if(s.getTasks().size() < minQueue) {
                minQueue = s.getTasks().size();
                server = s;
            }
        }
        if(server != null) {
            task.setWaitingTime(server.getWaitingPeriod());
            server.addTask(task);
        }
    }
}
