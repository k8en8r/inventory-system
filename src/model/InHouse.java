package model;

public class InHouse extends Part {
    //declare fields
    private int machineId;

    //declare methods
    public InHouse (int id, String name, double price, int stock, int min, int max, int machineId){
        super(id, name, price, stock, min, max);
        this.machineId = machineId;
    }

    //getter and setter
    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public int getMachineId() {
        return machineId;
    }
}
