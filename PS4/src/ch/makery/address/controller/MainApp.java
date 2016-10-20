package ch.makery.address.controller;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ch.makery.address.model.Person;
import ch.makery.address.model.PersonListWrapper;
import ch.makery.address.view.BirthdayStatisticsController;
import ch.makery.address.view.PersonEditDialogController;
import ch.makery.address.view.PersonOverviewController;
import ch.makery.address.view.RootLayoutController;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	// data as ObservableList<E>list of Persons
	private ObservableList<Person> personData = FXCollections.observableArrayList();
	
	public MainApp() {
		// constructor with some sample data
		personData.add(new Person("Hans", "Muster"));
		personData.add(new Person("Ruth", "Mueller"));
		personData.add(new Person("Heinz", "Kurz"));
		personData.add(new Person("Cornelia", "Meier"));
		personData.add(new Person("Werner", "Meyer"));
		personData.add(new Person("Lydia", "Kunz"));
		personData.add(new Person("Anna", "Best"));
		personData.add(new Person("Stefan", "Meier"));
		personData.add(new Person("Martin ", "Mueller"));
	}
	
	public ObservableList<Person> getPersonData() {
		// returns the data as an observable list of Persons
        return personData;
    }
	

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Address App");
		
		// sets the application icon
		this.primaryStage.getIcons().add(new Image("file:resources/images/1476938443_Address_Book.png"));
		
		initRootLayout();

        showPersonOverview();
	}
	
	public void initRootLayout() {
		// initializes the root layout
		try {
			// loads root layout from fxml file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			//shows the scene containing the root layout
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			
			// gives the controller access to the main app
	        RootLayoutController controller = loader.getController();
	        controller.setMainApp(this);
			
			primaryStage.show();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		// tries to load last opened person file
	    File file = getPersonFilePath();
	    if (file != null) {
	        loadPersonDataFromFile(file);
	    }
	}
	
	public void showPersonOverview() {
		// shows the person overview inside the root layout
		try {
			// loads person overview
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
			AnchorPane personOverview = (AnchorPane) loader.load();
			
			// sets person overview into the center of the root layout
			rootLayout.setCenter(personOverview);
			
			// gives the controller access to the main application
	        PersonOverviewController controller = loader.getController();
	        controller.setMainApp(this);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean showPersonEditDialog(Person person) {
		// opens a dialog to edit details for the specified person
		// if the user clicks OK, the changes are saved to the provided person object
	    try {
	        // loads the fxml file and create a new stage for the pop-up dialog
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/PersonEditDialog.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();

	        // creates the dialog stage
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Edit Person");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        // sets the person into the controller
	        PersonEditDialogController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        controller.setPerson(person);

	        // shows the dialog and wait until the user closes it
	        dialogStage.showAndWait();

	        return controller.isOkClicked();
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public File getPersonFilePath() {
		// returns the person file preference
		// if no preference can be found, null is returned
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    String filePath = prefs.get("filePath", null);
	    if (filePath != null) {
	        return new File(filePath);
	    }
	    else {
	        return null;
	    }
	}

	public void setPersonFilePath(File file) {
		// sets the file path of the currently loaded file
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    if (file != null) {
	        prefs.put("filePath", file.getPath());
	        // updates the stage title
	        primaryStage.setTitle("AddressApp - " + file.getName());
	    }
	    else {
	        prefs.remove("filePath");
	        // updates the stage title
	        primaryStage.setTitle("AddressApp");
	    }
	}
	
	public void loadPersonDataFromFile(File file) {
		// loads person data from the specified file
		// current person data will be replaced
	    try {
	        JAXBContext context = JAXBContext
	                .newInstance(PersonListWrapper.class);
	        Unmarshaller um = context.createUnmarshaller();

	        // for reading XML from the file and unmarshalling
	        PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);

	        personData.clear();
	        personData.addAll(wrapper.getPersons());

	        // saves the file path to the registry
	        setPersonFilePath(file);
	    }
	    catch (Exception e) {
	    	// catches ANY exception
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not load data");
	        alert.setContentText("Could not load data from file:\n" + file.getPath());

	        alert.showAndWait();
	    }
	}

	public void savePersonDataToFile(File file) {
		// saves the current person data to the specified file
	    try {
	        JAXBContext context = JAXBContext
	                .newInstance(PersonListWrapper.class);
	        Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        // for wrapping our person data
	        PersonListWrapper wrapper = new PersonListWrapper();
	        wrapper.setPersons(personData);

	        //  for marshalling and saving XML to the file
	        m.marshal(wrapper, file);

	        // saves the file path to the registry
	        setPersonFilePath(file);
	    }
	    catch (Exception e) {
	    	// catches ANY exception
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not save data");
	        alert.setContentText("Could not save data to file:\n" + file.getPath());

	        alert.showAndWait();
	    }
	}
	
	public void showBirthdayStatistics() {
		// opens a dialog to show birthday statistics
	    try {
	        // loads the fxml file and create a new stage for the pop-up
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/BirthdayStatistics.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Birthday Statistics");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        // sets the persons into the controller
	        BirthdayStatisticsController controller = loader.getController();
	        controller.setPersonData(personData);

	        dialogStage.show();

	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public Stage getPrimaryStage() {
		// returns the main stage
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
}