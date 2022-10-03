package model;

public class Outsourced extends Part {
    //declare fields
    private String companyName;

    //declare methods
    public Outsourced (int id, String name, double price, int stock, int min, int max, String companyName){
        //call superclass constructor
        super(id, name, price, stock, min, max);
        this.companyName = companyName;
    }

    //set company name
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName(){
        return companyName;
    }
}
