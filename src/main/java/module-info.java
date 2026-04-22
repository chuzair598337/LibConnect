module com.libconnect {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.libconnect to javafx.graphics;
    opens com.libconnect.gui to javafx.fxml;
    opens com.libconnect.model to javafx.fxml;

    exports com.libconnect;
    exports com.libconnect.model;
    exports com.libconnect.gui;
    exports com.libconnect.util;
}
