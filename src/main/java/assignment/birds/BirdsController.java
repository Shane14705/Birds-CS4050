package assignment.birds;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ouda
 */
public class BirdsController implements Initializable {

    @FXML
    private MenuBar mainMenu;
    @FXML
    private ImageView image;
    @FXML
    private BorderPane BirdPortal;
    @FXML
    private Label title;
    @FXML
    private Label about;
    @FXML
    private Button play;
    @FXML
    private Button puase;
    @FXML
    private ComboBox size;
    @FXML
    private TextField name;
    Media media;
    MediaPlayer player;
    OrderedDictionary database = null;
    BirdRecord bird = null;
    int birdSize = 1;

    @FXML
    public void exit() {
        Stage stage = (Stage) mainMenu.getScene().getWindow();
        stage.close();
    }

    public void find() {
        DataKey key = new DataKey(this.name.getText(), birdSize);
        try {
            bird = database.find(key);
            showBird();
        } catch (DictionaryException ex) {
            displayAlert(ex.getMessage());
        }
    }

    public void delete() {
        BirdRecord previousBird = null;
        try {
            previousBird = database.predecessor(bird.getDataKey());
        } catch (DictionaryException ex) {

        }
        BirdRecord nextBird = null;
        try {
            nextBird = database.successor(bird.getDataKey());
        } catch (DictionaryException ex) {

        }
        DataKey key = bird.getDataKey();
        try {
            database.remove(key);
        } catch (DictionaryException ex) {
            System.out.println("Error in delete "+ ex);
        }
        if (database.isEmpty()) {
            this.BirdPortal.setVisible(false);
            displayAlert("No more birds in the database to show");
        } else {
            if (previousBird != null) {
                bird = previousBird;
                showBird();
            } else if (nextBird != null) {
                bird = nextBird;
                showBird();
            }
        }
    }

    private void showBird() {
        play.setDisable(false);
        puase.setDisable(true);
        if (player != null) {
            player.stop();
        }
        String img = bird.getImage();
        Image birdImage = new Image("file:src/main/resources/assignment/birds/images/" + img);
        image.setImage(birdImage);
        // Log DataKey and BirdRecord details
        System.out.println("Showing Bird: " + bird.getDataKey().getBirdName() + ", About: " + bird.getAbout() + ", Sound: " + bird.getSound() + ", Image: " + bird.getImage());
        title.setText(bird.getDataKey().getBirdName());
        about.setText(bird.getAbout());
    }

    private void displayAlert(String msg) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Alert.fxml"));
            Parent ERROR = loader.load();
            AlertController controller = (AlertController) loader.getController();

            Scene scene = new Scene(ERROR);
            Stage stage = new Stage();
            stage.setScene(scene);

            stage.getIcons().add(new Image("file:src/main/resources/assignment/birds/images/UMIcon.png"));
            stage.setTitle("Dictionary Exception");
            controller.setAlertText(msg);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException ex1) {

        }
    }

    public void getSize() {
        switch (this.size.getValue().toString()) {
            case "Small":
                this.birdSize = 1;
                break;
            case "Medium":
                this.birdSize = 2;
                break;
            case "Large":
                this.birdSize = 3;
                break;
            default:
                break;
        }
    }

    public void first() {
        try {
            System.out.println("[first] Attempting to find the smallest bird record.");
            BirdRecord smallest = database.smallest();
            bird = smallest;
            System.out.println("SMALLEST NAME:" + smallest.getImage());
            if (smallest != null && smallest.getDataKey() != null) {
                System.out.println("[first] Smallest BirdRecord found: " + smallest.getDataKey().getBirdName());
                showBird();
            } else {
                throw new DictionaryException("Error: Smallest BirdRecord or its key is null.");
            }
        } catch (DictionaryException ex) {
            System.out.println("[first] Exception caught: " + ex.getMessage());
            displayAlert("No birds in the database.");
        }
    }




    public void last() {
        try {
            bird = database.largest();
            if (bird != null) {
                showBird();
            }
        } catch (DictionaryException ex) {
            displayAlert("No birds in the database.");
        }
    }


    public void next() {
        if (bird == null) {
            displayAlert("No current bird selected.");
            return;
        }

        try {
            BirdRecord nextBird = database.successor(bird.getDataKey());
            if (nextBird != null) {
                bird = nextBird;
                showBird();
            } else {
                displayAlert("This is the last bird in the database.");
            }
        } catch (DictionaryException ex) {
            displayAlert("Error finding next bird: " + ex.getMessage());
        }
    }

    public void previous() {
        if (bird == null) {
            displayAlert("No current bird selected.");
            return;
        }

        try {
            BirdRecord previousBird = database.predecessor(bird.getDataKey());
            if (previousBird != null) {
                bird = previousBird;
                showBird();
            } else {
                displayAlert("This is the first bird in the database.");
            }
        } catch (DictionaryException ex) {
            displayAlert("Error finding previous bird: " + ex.getMessage());
        }
    }


    public void play() {
        String filename = "src/main/resources/assignment/birds/sounds/" + bird.getSound();
        media = new Media(new File(filename).toURI().toString());
        player = new MediaPlayer(media);
        play.setDisable(true);
        puase.setDisable(false);
        player.play();
    }

    public void puase() {
        play.setDisable(false);
        puase.setDisable(true);
        if (player != null) {
            player.stop();
        }
    }


    public void loadDictionary() {
        Scanner input;
        int line = 0;
        try {
            String birdName = "";
            String description;
            int size = 0;
            input = new Scanner(new File("BirdsDatabase.txt"));
            while (input.hasNext()) // read until  end of file
            {
                String data = input.nextLine();
                switch (line % 3) {
                    case 0:
                        size = Integer.parseInt(data);
                        System.out.println("[load] BIRD SIZE:" + birdSize);
                        break;
                    case 1:
                        birdName = data;
                        System.out.println("[load] BIRD NAME:" + birdName);
                        break;
                    default:
                        description = data;
                        DataKey key = new DataKey(birdName, size);
                        System.out.println("[load] BIRD DESCRIPTION:" + description);
                        System.out.println("[load] BIRD KEY:" + key.getBirdName() + "," + key.getBirdSize());
                        database.insert(new BirdRecord(key, description, birdName + ".mp3", birdName + ".jpg"));
                        break;
                }
                line++;
            }
        } catch (IOException e) {
            System.out.println("There was an error in reading or opening the file: BirdsDatabase.txt");
            System.out.println(e.getMessage());
        } catch (DictionaryException ex) {
            Logger.getLogger(BirdsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.BirdPortal.setVisible(true);
        this.first();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        database = new OrderedDictionary();
        size.setItems(FXCollections.observableArrayList(
                "Small", "Medium", "Large"
        ));
        size.setValue("Small");
    }

}
