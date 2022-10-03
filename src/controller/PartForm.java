package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Inventory;
import model.Outsourced;
import model.Part;
import model.InHouse;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static controller.FirstScreen.partToMod;
import static model.Inventory.partIdCount;

public class PartForm implements Initializable {
    public Button cancel;
    public Button save;
    public Label addOrModPart;
    public ToggleGroup ihvos;
    public RadioButton outsourced;
    public RadioButton inHouse;
    public TextField partFormId;
    public TextField partFormName;
    public TextField partFormInv;
    public TextField partFormPrice;
    public TextField partFormMax;
    public TextField machineIdOrCompanyNameText;
    public TextField partFormMin;
    public Label machineIdOrCompanyNameLabel;
    public Label partErrorMessage;

    private Part modPart;
    private Part newPart;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        modPart = partToMod();
        newPart = null;
        //if modify then use modifying part
        if (modPart != null) {
            //change title to "modify" then set all the other fields
            addOrModPart.setText("Modify Part");
            partFormId.setText(String.valueOf(modPart.getId()));
            partFormName.setText(modPart.getName());
            partFormInv.setText(Integer.toString(modPart.getStock()));
            partFormPrice.setText(Double.toString(modPart.getPrice()));
            partFormMin.setText(Integer.toString(modPart.getMin()));
            partFormMax.setText(Integer.toString(modPart.getMax()));
            // machine id or company name depending on if in-house or outsourced
            if (modPart instanceof InHouse)
            {
                machineIdOrCompanyNameText.setText(Integer.toString(((InHouse)modPart).getMachineId()));
                ihvos.selectToggle(inHouse);
                machineIdOrCompanyNameLabel.setText("Machine ID");
            }
            else {
                machineIdOrCompanyNameText.setText(((Outsourced)modPart).getCompanyName());
                ihvos.selectToggle(outsourced);
                machineIdOrCompanyNameLabel.setText("Company Name");
            }


        // if not modify set title properly, and generate ID
        } else {
            addOrModPart.setText("Add Part");
            partFormId.setText(String.valueOf(partIdCount));
        }
    }
    public void onInHouse(ActionEvent actionEvent) {
        machineIdOrCompanyNameLabel.setText("Machine ID");

    }

    public void onOutsourced(ActionEvent actionEvent) {
        machineIdOrCompanyNameLabel.setText("Company Name");
    }

    /**
     * RUNTIME ERROR. onSave would not switch to the main screen when modifying a part (however it would switch
     * when adding a part) unless a separate ActionEvent (ex: deleting a part, or adding a part) occurred prior. To
     * solve that issue, I created two new functions for updating and adding a new part (updatePart and
     * addNewPart) and threw much of the code from onSave into them.
     * @param actionEvent
     * @throws IOException
     */
    public void onSave (ActionEvent actionEvent) throws IOException {
        int min;
        int inv;
        int max;
        double price;
        boolean saveSuccess = false;

        try {
            min = Integer.parseInt(partFormMin.getText());
            inv = Integer.parseInt(partFormInv.getText());
            max = Integer.parseInt(partFormMax.getText());
        }catch(NumberFormatException e) {
            partErrorMessage.setText("Error: Inventory, min, and max all need to be integer values");
            return;
        }
        try{
            price = Double.parseDouble(partFormPrice.getText());
        }catch(NumberFormatException e) {
            partErrorMessage.setText("Error: Invalid price.");
            return;
        }
        //check inventory values and return if not valid input
        if ( min > max ){
            partErrorMessage.setText("Error: Min must be less than max.");
            return;
        }
        if ( inv < min ){
            partErrorMessage.setText("Error: Inv must be greater than or equal to min.");
            return;
        }
        if ( inv > max ){
            partErrorMessage.setText("Error: Inv must be less than or equal to max.");
            return;
        }
        if (modPart != null){saveSuccess = updatePart();} else {saveSuccess = addNewPart();}
        if (saveSuccess == false ){
            return;
        }

        // go to main screen
        Parent root = FXMLLoader.load(getClass().getResource("/view/FirstScreen.fxml"));
        root.setStyle("-fx-font-family: 'Times New Roman'");
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Main Form");
        stage.setScene(scene);
        stage.show();
    }

    public void onCancel (ActionEvent actionEvent) throws IOException {
        // switch to first screen
        Parent root = FXMLLoader.load(getClass().getResource("/view/FirstScreen.fxml"));
        root.setStyle("-fx-font-family: 'Times New Roman'");
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Main Form");
        stage.setScene(scene);
        stage.show();
    }

    public boolean addNewPart(){
        int machineID;
        //get text from user and generate a new in-house or outsourced part
        if (ihvos.getSelectedToggle() == inHouse) {
            //if inhouse check for int
            try {
                machineID = Integer.parseInt(machineIdOrCompanyNameText.getText());
                // if not int return failure and display error
            }catch(NumberFormatException e) {
                partErrorMessage.setText("Machine ID must be a numerical value.");
                return false;
            }
            newPart = new InHouse(partIdCount, partFormName.getText(),
                    Double.parseDouble(partFormPrice.getText()), Integer.parseInt(partFormInv.getText()),
                    Integer.parseInt(partFormMin.getText()), Integer.parseInt(partFormMax.getText()),
                    machineID);
        } else {
            newPart = new Outsourced(partIdCount, partFormName.getText(),
                    Double.parseDouble(partFormPrice.getText()), Integer.parseInt(partFormInv.getText()),
                    Integer.parseInt(partFormMin.getText()), Integer.parseInt(partFormMax.getText()),
                    machineIdOrCompanyNameText.getText());
        }
        //increment part id count and add part to inventory
        partIdCount++;
        Inventory.addPart(newPart);
        return true;
    }

    public boolean updatePart(){
        //use bool to get current and future part type
        boolean toBeInHouse = false;
        int machineID;
        if (ihvos.getSelectedToggle() == inHouse) {
            toBeInHouse = true;
        }
        boolean currentlyInHouse = false;
        if (modPart instanceof InHouse){
            currentlyInHouse = true;
        }
        //get id so it's not lost on delete
        int modPartId = modPart.getId();
        //if  changed between in-house and outsourced
        if ((currentlyInHouse && !toBeInHouse) || (!currentlyInHouse && toBeInHouse) ){
            //delete and create new part with same id
            if (toBeInHouse){
                //check machine id for int
                try {
                    machineID = Integer.parseInt(machineIdOrCompanyNameText.getText());
                    // if not int return failure and display error
                }catch(NumberFormatException e) {
                    partErrorMessage.setText("Machine ID must be a numerical value.");
                    return false;
                }
                Inventory.getAllParts().remove(modPart);
                newPart = new InHouse(modPartId, partFormName.getText(), Double.parseDouble(partFormPrice.getText()),
                        Integer.parseInt(partFormInv.getText()), Integer.parseInt(partFormMin.getText()),
                        Integer.parseInt(partFormMax.getText()), Integer.parseInt(machineIdOrCompanyNameText.getText()));
                Inventory.addPart(newPart);
            } else {
                Inventory.getAllParts().remove(modPart);
                newPart = new Outsourced(modPartId, partFormName.getText(), Double.parseDouble(partFormPrice.getText()),
                        Integer.parseInt(partFormInv.getText()), Integer.parseInt(partFormMin.getText()),
                        Integer.parseInt(partFormMax.getText()), machineIdOrCompanyNameText.getText());
                Inventory.addPart(newPart);

            }
        //if not changed, just update with new values
        }  else {
            if (ihvos.getSelectedToggle() == inHouse) {
                try {
                    machineID = Integer.parseInt(machineIdOrCompanyNameText.getText());
                    // if not int return failure and display error
                }catch(NumberFormatException e) {
                    partErrorMessage.setText("Machine ID must be a numerical value.");
                    return false;
                }
                ((InHouse) modPart).setMachineId(Integer.parseInt(machineIdOrCompanyNameText.getText()));
            } else {
                ((Outsourced) modPart).setCompanyName(machineIdOrCompanyNameText.getText());
            }
            modPart.setMax(Integer.parseInt(partFormMax.getText()));
            modPart.setMin(Integer.parseInt(partFormMin.getText()));
            modPart.setPrice(Double.parseDouble(partFormPrice.getText()));
            modPart.setStock(Integer.parseInt(partFormInv.getText()));
            modPart.setName(partFormName.getText());
        }
        return true;
    }

    public void printErrorMessage (String message){
    partErrorMessage.setText(message);
    }


}
