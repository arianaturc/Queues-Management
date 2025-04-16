package businessLogic;

import dataModel.Server;
import dataModel.Task;
import graphicalUserInterface.SimulationFrame;
import javafx.application.Platform;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class SimulationManager implements Runnable {
    public int timeLimit;
    public int maxProcessingTime;
    public int minProcessingTime;
    public int numberOfServers;
    public int numberOfClients;
    public int maxArrivalTime;
    public int minArrivalTime;
    public SelectionPolicy selectionPolicy;
    private final String outputFile;
    private int maxServerTask = 0;


    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> generatedTasks;

    public SimulationManager(int numberOfClients, int numberOfServers, int timeLimit,
                             int minArrivalTime, int maxArrivalTime, int minProcessingTime,
                             int maxProcessingTime, SelectionPolicy selectionPolicy, String outputFile) {

        this.numberOfClients = numberOfClients;
        this.numberOfServers = numberOfServers;
        this.timeLimit = timeLimit;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minProcessingTime = minProcessingTime;
        this.maxProcessingTime = maxProcessingTime;
        this.selectionPolicy = selectionPolicy;
        this.outputFile = outputFile;

        scheduler = new Scheduler(numberOfServers, 100);
        scheduler.changeStrategy(selectionPolicy);
        generateNRandomTasks();
    }

    private void generateNRandomTasks() {
        generatedTasks = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < numberOfClients; i++) {
            int arrivalTime = minArrivalTime + rand.nextInt(maxArrivalTime - minArrivalTime + 1);
            int processingTime = minProcessingTime + rand.nextInt(maxProcessingTime - minProcessingTime + 1);
            Task task = new Task(i + 1, arrivalTime, processingTime);
            generatedTasks.add(task);
        }
        Collections.sort(generatedTasks, Comparator.comparingInt(Task::getArrivalTime));
    }

    public void run() {

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outputFile))) {
            int currentTime = 0;
            int serviceTime = 0, totalWaitingTime = 0;
            int count = 0;
            int peakTime = 0;

            while (currentTime < timeLimit) {
                List<Task> tasksToDispatch = new ArrayList<>();
                List<Task> genTasks = new ArrayList<>(generatedTasks);

                for (Task t : genTasks) {
                    if (t.getArrivalTime() == currentTime) {
                        serviceTime += t.getServiceTime();
                        count++;
                        scheduler.dispatchTask(t);
                        tasksToDispatch.add(t);
                        totalWaitingTime += t.getWaitingTime();
                    }
                }
                generatedTasks.removeAll(tasksToDispatch);

                int serverTasks = 0;
                for(Server server : scheduler.getServers()) {
                    serverTasks += server.getTasks().size();
                }
                if(serverTasks > maxServerTask) {
                    maxServerTask = serverTasks;
                    peakTime = currentTime;
                }

                if (scheduler.emptyServers() && generatedTasks.isEmpty()) {
                    break;
                }

                fileWriter.write("Time " + currentTime + "\n");
                fileWriter.write("Waiting clients: ");
                for (Task t : generatedTasks) {
                    fileWriter.write(taskFormat(t) + "; ");
                }
                fileWriter.write("\n");
                fileWriter.flush();
                printQueues(scheduler, fileWriter);

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                currentTime++;
            }
            fileWriter.write("Simulation finished!\n");
            fileWriter.write("Average waiting time: " + ((double) totalWaitingTime / count) + "\n");
            fileWriter.write("Average service time: " + ((double) serviceTime / count) + "\n");
            fileWriter.write("Peak time: " + peakTime + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void printQueues(Scheduler scheduler, BufferedWriter fileWriter) throws IOException {

        int queueIndex = 1;

        for (Server server : scheduler.getServers()) {
            List<Task> currentServerTasks = server.getTasksAsList();

            fileWriter.write("Queue " + queueIndex + ": ");

            for (Task t : server.getTasks()) {
                fileWriter.write(taskFormat(t) + "; ");
            }

            if (currentServerTasks.isEmpty()) {
                fileWriter.write("closed");
            }

            fileWriter.newLine();
            queueIndex++;
        }

        fileWriter.newLine();
        fileWriter.flush();

    }

    private String taskFormat(Task task) {
        return "(" + task.getID() + "," + task.getArrivalTime() + "," + task.getServiceTime() + ")";
    }

    public static void launchGUI() {
        Platform.startup(() -> {
            SimulationFrame frame = new SimulationFrame();
            frame.launchUI();
        });
    }

    public static void main(String[] args) {
//        SimulationManager test1 = new SimulationManager(4, 2, 60,
//                2, 30, 2,
//                4, SelectionPolicy.SHORTEST_QUEUE, "C:\\Users\\arian\\Documents\\an2_utcn\\sem2\\fpt\\PT2025_30422_Turc_Ariana_Assignment_2\\src\\main\\java\\test1.txt");
//        SimulationManager test2 = new SimulationManager(50, 5, 60,
//                2, 40, 1,
//                7, SelectionPolicy.SHORTEST_TIME, "C:\\Users\\arian\\Documents\\an2_utcn\\sem2\\fpt\\PT2025_30422_Turc_Ariana_Assignment_2\\src\\main\\java\\test2.txt");
//        SimulationManager test3 = new SimulationManager(1000, 20, 200,
//                10, 100, 3,
//                9, SelectionPolicy.SHORTEST_QUEUE, "C:\\Users\\arian\\Documents\\an2_utcn\\sem2\\fpt\\PT2025_30422_Turc_Ariana_Assignment_2\\src\\main\\java\\test3.txt");
//
//
//        Thread thread1 = new Thread(test1);
//        Thread thread2 = new Thread(test2);
//        Thread thread3 = new Thread(test3);
//        thread1.start();
//        thread2.start();
//        thread3.start();
//
//        try {
//            thread1.join();
//            thread2.join();
//            thread3.join();
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        SimulationManager.launchGUI();
    }
}
