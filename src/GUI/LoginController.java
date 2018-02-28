/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;


import Entite.User;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;



import GUI.FXMain;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.management.Notification;
import javax.swing.text.Position;
import org.controlsfx.control.Notifications;

import Service.UserDAO;

import static GUI.FXMain.stg;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import Utils.AnimationGenerator;

import Utils.UserStatus;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;



public class LoginController implements Initializable {

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Button loginButton;
    AnchorPane AnchorPane;
   private AnimationGenerator animationGenerator;
    @FXML
    private Button SinginButton;
    @FXML
    private Button mdpOublie;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
   animationGenerator = new AnimationGenerator();
//   mdpOublie.setOnMouseClicked((MouseEvent event) -> {
//         try {
//             
//             Stage stage = new Stage();
//             Stage stageprev = (Stage) mdpOublie.getScene().getWindow();
//             
//             FXMLLoader loader = new FXMLLoader(getClass().getResource("ForgetPassword.fxml"));
//             Parent parent = loader.load();
//             
//             Stage stagep = new Stage();
//             Scene scene = new Scene(parent);
//             stage.setScene(scene);
//             stage.setResizable(false);
//             stage.show();
//             stageprev.close();
//         } catch (IOException ex) {
//             Logger.getLogger(ForgetPasswordController.class.getName()).log(Level.SEVERE, null, ex);
//         }
//        });

    }   
    
    public void login() {
        UserDAO userDAO = new UserDAO();
        try {

            User user = userDAO.login(usernameTextField.getText(), passwordTextField.getText());
            if (user != null) {
                if (user.getStatus().equals(UserStatus.PENDING)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Votre demande n'est encore traite par l'administrateur");
                    alert.showAndWait();
                   
                    return;
                }
                if (user.getStatus().equals(UserStatus.REFUSED)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Votre demande et refuser");
                    alert.showAndWait();
                    return;
                }
                if (user.getStatus().equals(UserStatus.DELETED)
                        || user.getStatus().equals(UserStatus.BLOCKED)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Votre compte et soit blocquer ou sumpprier par l'administrateur!");
                    alert.showAndWait();
                    return;
                }
                
                    
       
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Welcome to Espace sante");
                alert.setHeaderText("Welcome!");
                FXMain.currentUser = user;
                System.out.println("Current logged in user role: " + FXMain.currentUser.getRole());
                    animateWhenLoginSuccess();
              
             //  Notifications  notification=Notifications.create()
                                   // .graphic(new ImageView(image))
//                                    .title("Sign in complete ")
//                                    .text(usernameTextField.getText() +" has loged in")
//                                    .hideAfter(Duration.seconds(3))
//                                    .position(Pos.BOTTOM_RIGHT);                           
     //                      notification.showInformation();
         
               

            } else {
                animateWhenBadLogin();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
      private void animateWhenBadLogin() {
        try {
            ImageView imageView = new ImageView(new Image("GUI/Images/Delete-50.png"));
            Label wrong = new Label("Invalide Nom d'utilisateur/Mot de passe");
            VBox temp = new VBox(imageView, wrong);
            temp.setAlignment(Pos.CENTER);

            animationGenerator.applyFadeAnimationOn(FXMain.loginWindow, 500, 1.0f, 0f, event -> {
                temp.setOpacity(0f);

                double oldWidth = FXMain.stg.getWidth();
                double oldHeight = FXMain.stg.getHeight();

                FXMain.stg.setScene(new Scene(temp, oldWidth , oldHeight));
                animationGenerator.applyFadeAnimationOn(temp, 1000, 0f, 1.0f, event1 -> {
                    animationGenerator.applyFadeAnimationOn(temp, 1000, 1, 0f, null);
                    FXMain.loginWindow.setOpacity(0f);
                    PauseTransition pause = new PauseTransition(Duration.millis(2000));
                    pause.setOnFinished(ev -> {
                        FXMain.stg.setScene(FXMain.loginScene);
                        animationGenerator.applyFadeAnimationOn(FXMain.loginWindow, 500, 0f, 1.0f, null);
                    });
                    pause.play();
                });
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
       public  void animateWhenLoginSuccess() {
        try {
            final Parent dashboard;

            switch (FXMain.currentUser.getRole().toLowerCase()) {
                case "admin":
                    
                    dashboard = FXMLLoader.load(getClass().getResource("DashboardView.fxml"));
                    break;
                case "prestataire":
                    dashboard = FXMLLoader.load(getClass().getResource("MembreView.fxml"));
                    break;
                
                case "membre":
                    dashboard = FXMLLoader.load(getClass().getResource("MembreView.fxml"));
                    break;
                default:
                    throw new AssertionError();
            }
            ImageView imageView = new ImageView(new Image("GUI/Images/Checkmark-50.png"));
            Label welcome = new Label("Bienvenue chez Notre Espace sante");
            welcome.setFont(new Font("BebasNeueRegular", 24));
           VBox temp = new VBox(imageView, welcome);
            temp.setAlignment(Pos.CENTER);

            animationGenerator.applyFadeAnimationOn(AnchorPane, 1000, 1.0f, 0f, event -> {
                temp.setOpacity(0);
                double oldWidth = FXMain.stg.getWidth();
                double oldHeight = FXMain.stg.getHeight();

                FXMain.stg.setScene(new Scene(temp, oldWidth, oldHeight));
                animationGenerator.applyFadeAnimationOn(temp, 1000, 0f, 1.0f, event1 -> {
                    animationGenerator.applyFadeAnimationOn(temp, 1000, 1.0f, 0f, event2 -> {
                        FXMain.stg.setScene(new Scene(dashboard, 1400, 850));
                       FXMain.stg.centerOnScreen();
                      // Pidev.stage.setFullScreen(true);

                        animationGenerator.applyFadeAnimationOn(dashboard, 1000, 0f, 1.0f, null);
                    });
                });
            });
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        TrayNotification tray = new TrayNotification("Login avec Succés !", "", NotificationType.SUCCESS);
    //   tray.position(Pos.TOP_RIGHT);
        tray.showAndWait();
        
    }
       public void backToHome()
       {
            try {
                           
       FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Acceuil.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));  
        stage.show();
        FXMain.stg.close();
        FXMain.stg = stage;
        } catch (Exception e) {
       System.out.println(e);
                    }
                     }
     
       
       
       
//        public void keypress(KeyEvent keyEvent) {
//        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
//            login();
//        }
//    }

//  public void signup()  {
//      
//      try {
//            FXMain.signupWindow = FXMLLoader.load(getClass().getResource("singup.fxml"));
//
//            animationGenerator.applyTranslateAnimationOn(FXMain.loginWindow, 1000, 0, -1000);
//            animationGenerator.applyFadeAnimationOn(FXMain.loginWindow, 500, 1.0f, 0f, event -> {
//            double oldWidth = FXMain.stg.getWidth();
//            double minHeight = FXMain.stg.getHeight();
//            FXMain.signupScene = new Scene(FXMain.signupWindow, oldWidth - 20, minHeight);
//            FXMain.stg.setScene(FXMain.signupScene);
//            FXMain.stg.setMinHeight(minHeight);
//            FXMain.stg.setMinHeight(800);
//            FXMain.stg.centerOnScreen();
//            
//            FXMain.signupWindow.setOpacity(1f);
//            FXMain.signupWindow.setTranslateX(1000);
//
//            FXMain.loginWindow.toBack();
//            FXMain.signupWindow.toFront();
//
//            animationGenerator.applyTranslateAnimationOn(FXMain.signupWindow, 500, 1000, 0);
//            animationGenerator.applyFadeAnimationOn(FXMain.signupWindow, 500, 0f, 1.0f, null);
//        });
//      } catch (Exception e) {
//          System.out.println(e);
//      }
//
//    }
public void signup()
    {
     try {
                           
       FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("singup.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));  
        stage.show();
        FXMain.stg.close();
        FXMain.stg = stage;
  } catch (Exception e) {
       System.out.println(e);
  }
    }
    

    @FXML
    private void loginButtoAnction(ActionEvent event) {
         login();
         

        
//         Image i=new Image("images/Delete-50.png");
//                    mimi.notificationDeConfirmation(event, i);
//         if(usernameTextField.getText().isEmpty()&&(passwordTextField.getText().isEmpty()))
//                {
//                    
//                }else
//         {Client c =new Client();
//        c.setUsername(usernameTextField.getText());
//        System.out.println(usernameTextField.getText());
//        
//        c.setPassword(passwordTextField.getText());
//        System.out.println(passwordTextField.getText());
//       
//        
//        
//     Image i= new Image("/images/Checkmark-50");
//     mimi.notificationDeConfirmation(event , i);
//         }
    }

    @FXML
    private void SinginButtonAction(ActionEvent event) throws IOException {
        signup();
        
    }

    @FXML
    private void forgetpass(MouseEvent event) throws IOException {
         final Parent dashboard;
        dashboard = FXMLLoader.load(getClass().getResource("ForgetPassword.fxml"));

         animationGenerator.applyFadeAnimationOn(AnchorPane, 1000, 1.0f, 0f, event1 -> {
                double oldWidth = FXMain.stg.getWidth();
                double oldHeight = FXMain.stg.getHeight();

               
                        FXMain.stg.setScene(new Scene(dashboard, 1350, 750));
                        FXMain.stg.centerOnScreen();
                      // Pidev.stage.setFullScreen(true);

                        animationGenerator.applyFadeAnimationOn(dashboard, 1000, 0f, 1.0f, null);
                    });
           

        
    }
    
    

    
}
