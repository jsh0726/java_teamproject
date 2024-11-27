package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 초기 화면 설정: MainMenu
        MainMenu mainMenu = new MainMenu();
        primaryStage.setScene(mainMenu.getScene(primaryStage)); // 메인 메뉴 화면으로 설정
        primaryStage.setTitle("마블런");
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args); // JavaFX 애플리케이션 실행
    }
}
