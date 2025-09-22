package dataModel;

public class Task {

    public int ID;
    private int arrivalTime;
    private int serviceTime;
    private int waitingTime;

    public Task(int ID, int arrivalTime, int serviceTime) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.waitingTime = 0;
    }

    public int getID() {
        return ID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public synchronized int getServiceTime() {
        return serviceTime;
    }

    public synchronized void setServiceTime(int remainingServiceTime) {
        this.serviceTime = remainingServiceTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public String toString() {
        return "(" + this.ID + "," + this.arrivalTime + "," + this.serviceTime + ")";
    }

}