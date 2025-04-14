package businessLogic;

import dataModel.Server;
import dataModel.Task;

import java.util.List;

public interface Strategy {

    public void addTask(List<Server> servers, Task task);
}
