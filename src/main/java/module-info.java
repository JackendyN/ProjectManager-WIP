module com.project {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
	requires javafx.base;

    opens com.project to javafx.fxml;
    exports com.project;
}
