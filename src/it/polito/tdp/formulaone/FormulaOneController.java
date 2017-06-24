package it.polito.tdp.formulaone;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Model;
import it.polito.tdp.formulaone.model.Season;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FormulaOneController {
	
	Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Season> boxAnno;

    @FXML
    private TextField textInputK;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	Season season= boxAnno.getValue();
    	model.creaGrafo(season);
    	txtResult.setText("Il pilota migliore della stagione "+season.getYear()+" è "+model.getBestDriver()+"\n");
    }

    @FXML
    void doTrovaDreamTeam(ActionEvent event) {
    	int k;
    	try{
    		k=Integer.parseInt(textInputK.getText());
    	}catch(NumberFormatException e){
    		txtResult.appendText("Errore: inserisci un numero intero!\n");
    		return;
    	}
    	List<Driver> dreamTeam= model.getDreamTeam(k);
    	txtResult.appendText("DREAM TEAM: "+dreamTeam.toString()+"\n");
    }

    @FXML
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert textInputK != null : "fx:id=\"textInputK\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FormulaOne.fxml'.";

    }
    
    public void setModel(Model model){
    	this.model = model;
    	
    	boxAnno.getItems().addAll(model.getSeasons());    
    	}
}
