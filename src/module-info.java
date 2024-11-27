module java_teamproject {
    requires javafx.controls;
    requires javafx.fxml;
    exports application; // application 패키지를 외부 모듈에 공개
    opens application to javafx.fxml;
}