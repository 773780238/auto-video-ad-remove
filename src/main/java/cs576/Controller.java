package cs576;


import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    public boolean isTextPlay = true;
    public boolean jumpButtonClicked = false;
    public Button jumpButton;
    public Button button;
    public ImageView ivFX;
    public TextField frameTextField;


    public void onButtonClick() {
        if (isTextPlay) {
            button.setText("Pause");
            isTextPlay = false;
        } else {
            button.setText("Play");
            isTextPlay = true;
        }

    }
    public void onJumpButtonClick() {
      /*  if(frameTextField.getText() != null ){
            jumpButtonClicked = true;
        }else{
            frameTextField.setText("0");
        }*/
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}