package dataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {

  private BlockingQueue<Task> tasks;
  private AtomicInteger waitingPeriod;

  private volatile Task currentTask = null;


  public Server() {
    this.tasks = new LinkedBlockingQueue<Task>();
    this.waitingPeriod = new AtomicInteger(0);
  }

  public Task getCurrentTask() {
    return currentTask;
  }

  public void addTask(Task newTask) {
    try {
      tasks.put(newTask);
      waitingPeriod.addAndGet(newTask.getServiceTime());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void run() {
    while (true) {
      try {
        Thread.sleep(1500);
        currentTask = tasks.peek();
        if(currentTask == null) {
          continue;
        }
        int remainingServiceTime = currentTask.getServiceTime();
        if(remainingServiceTime == 0) {
          tasks.take();
        }
        else{
          remainingServiceTime--;
          currentTask.setServiceTime(remainingServiceTime);
          waitingPeriod.decrementAndGet();
          if(remainingServiceTime == 0) {
            tasks.take();
          }
        }

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  public List<Task> getTasksAsList() {
    return new ArrayList<>(tasks);
  }

  public BlockingQueue<Task> getTasks() {
    return tasks;
  }

  public int getWaitingPeriod() {
    return waitingPeriod.get();
  }

}