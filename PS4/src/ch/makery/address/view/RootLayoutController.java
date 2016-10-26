package ch.makery.address.view;

import java.io.File;

import ch.makery.address.MainApp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

public class RootLayoutController {
	// controller for the root layout

	private MainApp mainApp;
	// reference to the main application

	public void setMainApp(MainApp mainApp) {
		// called by the main application to give a reference back to itself
		this.mainApp = mainApp;
	}

	@FXML
	private void handleNew() {
		// creates an empty address book
		mainApp.getPersonData().clear();
		mainApp.setPersonFilePath(null);
	}

	@FXML
	private void handleOpen() {
		// opens a FileChooser to let the user select an address book to load
		FileChooser fileChooser = new FileChooser();

		// sets extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// shows save file dialog
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

		if (file != null) {
			mainApp.loadPersonDataFromFile(file);
		}
	}

	@FXML
	private void handleSave() {
		// saves the file to the person file that is currently open
		// if there is no open file, the Save As.. dialog is shown
		File personFile = mainApp.getPersonFilePath();
		if (personFile != null) {
			mainApp.savePersonDataToFile(personFile);
		} else {
			handleSaveAs();
		}
	}

	@FXML
	private void handleSaveAs() {
		// opens a FileChooser to let the user select a file to save to
		FileChooser fileChooser = new FileChooser();

		// sets extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// shows save file dialog
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// makes sure it has the correct extension
			if (!file.getPath().endsWith(".xml")) {
				file = new File(file.getPath() + ".xml");
			}
			mainApp.savePersonDataToFile(file);
		}
	}

	@FXML
	private void handleAbout() {
		// opens an about dialog
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("AddressApp");
		alert.setHeaderText("About");
		alert.setContentText("Author: Marco Jakob\nWebsite: http://code.makery.ch");

		alert.showAndWait();
	}

	@FXML
	private void handleExit() {
		// closes the application
		System.exit(0);
	}

	@FXML
	private void handleShowBirthdayStatistics() {
		// opens the birthday statistics
		mainApp.showBirthdayStatistics();
	}
}