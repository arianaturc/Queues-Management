package businessLogic;

import dataModel.Server;
import dataModel.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {


    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        servers = new ArrayList<Server>();

        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server();
            servers.add(server);
            Thread thread = new Thread(server);
            thread.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        if(policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ShortestQueueStrategy();
        }
        if(policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new TimeStrategy();
        }
    }

    public void dispatchTask(Task task) {
        strategy.addTask(servers, task);
    }

    public List<Server> getServers() {
        return servers;
    }

    public boolean emptyServers() {
        for(Server server : servers) {
            if (server.getCurrentTask() != null || !server.getTasks().isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
