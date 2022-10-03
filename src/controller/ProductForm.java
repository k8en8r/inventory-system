package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Inventory;
import model.Part;
import model.Product;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static controller.FirstScreen.productToMod;
import static model.Inventory.productIdCount;

public class ProductForm implements Initializable {

    public TextField productFormId;
    public TextField productFormName;
    public TextField productFormInv;
    public TextField productFormPrice;
    public TextField productFormMin;
    public TextField productFormMax;
    public Label addOrModProduct;
    public TableColumn<Part, String> partIdCol;
    public TableColumn<Part, String> partNameCol;
    public TableColumn<Part, String> partInvCol;
    public TableColumn<Part, String> partPriceCol;
    public TableColumn<Part, String> thisPartIdCol;
    public TableColumn<Part, String> thisPartNameCol;
    public TableColumn<Part, String> thisPartInvCol;
    public TableColumn<Part, String> thisPartPriceCol;
    public TableView<Part> allPartsTable;
    public TableView<Part> inUse;
    public TextField productFormPartsQuery;
    public Button cancel;
    public Button addPart;
    public Button removePart;
    public Button save;
    public Label productErrorMessage;

    private Product modProd;
    private Product newProduct;
    boolean isModify;

    ObservableList<Part> productParts = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productErrorMessage.setText(" ");
        modProd = productToMod();
        // newProduct = null;
        isModify = (modProd != null);
        //if modify then use product

        inUse.setItems(productParts);

        if (isModify) {
            //create new product as to not overwrite when cancelled
            newProduct = modProd;
            //change title to "modify" and fill other fields
            addOrModProduct.setText("Modify Product");
            productFormId.setText(String.valueOf(modProd.getId()));
            productFormName.setText(modProd.getName());
            productFormInv.setText(Integer.toString(modProd.getStock()));
            productFormPrice.setText(Double.toString(modProd.getPrice()));
            productFormMin.setText(Integer.toString(modProd.getMin()));
            productFormMax.setText(Integer.toString(modProd.getMax()));
            //populates parts in use
            productParts.addAll(modProd.getAllAssociatedParts());
            //
        } else {
            addOrModProduct.setText("Add Part");
            productFormId.setText(String.valueOf(productIdCount));
        }
        //populate table
        allPartsTable.setItems(Inventory.getAllParts());
        partIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partInvCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        thisPartIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        thisPartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        thisPartInvCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        thisPartPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

    }


    public void productFormPartsResults (KeyEvent actionEvent){
        String q = productFormPartsQuery.getText();
        ObservableList<Part> productFormPartsSearch = allPartsTable.getItems();
        //if int then search by ID
        if (FirstScreen.isInt(q)) {
            productFormPartsSearch = FirstScreen.searchByPartNum(q);
            // else search by part name
        } else {
            productFormPartsSearch = FirstScreen.searchByPartName(q);
        }
        //display results
        allPartsTable.setItems(productFormPartsSearch);
        //if empty show message
        allPartsTable.setPlaceholder(new Label("Error: Part not found"));
    }

    public void onCancel(ActionEvent actionEvent) throws IOException {
        // switch to first screen
        Parent root = FXMLLoader.load(getClass().getResource("/view/FirstScreen.fxml"));
        root.setStyle("-fx-font-family: 'Times New Roman'");
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Main Form");
        stage.setScene(scene);
        stage.show();
    }

    public void onAddPart(ActionEvent actionEvent) {
        productErrorMessage.setText(" ");
        Part part = (Part) allPartsTable.getSelectionModel().getSelectedItem();
        productParts.add(part);
        inUse.refresh();
    }

    public void onRemovePart(ActionEvent actionEvent) {
        productErrorMessage.setText(" ");
        Part part = (Part) inUse.getSelectionModel().getSelectedItem();
        boolean toRemove = verifyRemoval(part.getName(), productFormName.getText());
        if (!toRemove) { return;}
        productParts.remove(part);
        inUse.refresh();
    }

    public void onSave(ActionEvent actionEvent) throws IOException {
        productErrorMessage.setText(" ");
        int idNum;
        if (isModify) {
            idNum = modProd.getId();
        } else {
            idNum = productIdCount;
        }
        //verify and set inputs
        boolean isValidInput = verifyInputs();
        if (!isValidInput) {
            return;
        }
        //if modifying, remove product
        if (isModify) {
            //loop through until ids match
            for (int i = 0; i < Inventory.getAllProducts().size(); i++) {
                Product products = Inventory.getAllProducts().get(i);
                if (products.getId() == modProd.getId()) {
                    //delete product and end loop
                    Inventory.getAllProducts().remove(i);
                    break;
                }
            }
        }
        //create new product and add to inventory
        newProduct = new Product( idNum, productFormName.getText(), Double.parseDouble(productFormPrice.getText()),
                Integer.parseInt(productFormInv.getText()), Integer.parseInt(productFormMin.getText()),
                Integer.parseInt(productFormMax.getText()));
        Inventory.addProduct(newProduct);
        newProduct.setProductParts(productParts);
        // switch to first screen
        Parent root = FXMLLoader.load(getClass().getResource("/view/FirstScreen.fxml"));
        root.setStyle("-fx-font-family: 'Times New Roman'");
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Main Form");
        stage.setScene(scene);
        stage.show();

    }

    public boolean verifyInputs(){
        int min;
        int inv;
        int max;
        double price;
        try {
         min = Integer.parseInt(productFormMin.getText());
         inv = Integer.parseInt(productFormInv.getText());
         max = Integer.parseInt(productFormMax.getText());
    }catch(NumberFormatException e) {
        productErrorMessage.setText("Error: Inventory, min, and max all need to be integer values");
        return false;
    }

        //check inventory values and return if not valid input

        if ( min > max ){
            productErrorMessage.setText("Error: Min must be less than max.");
            return false;
        }
        if ( inv < min ){
            productErrorMessage.setText("Error: Inv must be greater than or equal to min.");
            return false;
        }
        if ( inv > max ){
            productErrorMessage.setText("Error: Inv must be less than or equal to max.");
            return false;
        }

        try{
            price = Double.parseDouble(productFormPrice.getText());
        }catch(NumberFormatException e) {
            productErrorMessage.setText("Error: Invalid price.");
            return false;
        }
        return true;
    }


    public static boolean verifyRemoval (String partName, String productName){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family: 'Times New Roman'");
        alert.setTitle("Confirmation");
        alert.setHeaderText("Verify Delete");
        alert.setContentText("Are you sure you would like to remove " + partName + " from " + productName + "?");

        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            return true;
        } else {
            result.get();
        }
        {
            return false;
        }
    }
}
