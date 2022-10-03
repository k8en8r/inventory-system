package model;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//Author Caitlin Baca
public class Product {
    //declare fields
    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    //declare constructor
    public Product(ObservableList<Part> associatedParts, int id, String name, double price, int stock, int min, int max) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
    }
    //constructor w/o observable list
    public Product(int id, String name, double price, int stock, int min, int max) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
    }

    //getters and setters
    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public double getPrice() {

        return price;
    }

    public void setPrice(double price) {

        this.price = price;
    }

    public int getStock() {

        return stock;
    }

    public void setStock(int stock) {

        this.stock = stock;
    }

    public int getMin() {

        return min;
    }

    public void setMin(int min) {

        this.min = min;
    }

    public int getMax() {

        return max;
    }

    public void setMax(int max) {

        this.max = max;
    }

    //declare methods
    public void addAssociatedPart(Part part)
    {
        associatedParts.add(part);
    }


    public  void setProductParts(ObservableList<Part> parts) {
        associatedParts = parts;
    }

//    public void deleteAssociatedPart(Part selectedAssociatedPart){associatedParts.remove(selectedAssociatedPart);}

    public ObservableList<Part> getAllAssociatedParts()
    {
        return this.associatedParts;
    }




}
