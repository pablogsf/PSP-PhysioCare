package com.matias.physiocarepsp.viewscontroller;

import com.matias.physiocarepsp.utils.Utils;
import javafx.event.ActionEvent;
import javafx.scene.Node;

/**
 * Controller for the first view of the application.
 * Provides navigation to other views such as Patients and Physios.
 */
public class FirstViewController {

    /**
     * Handles the action to open the Patients view.
     *
     * @param actionEvent the event triggered by the user
     */
    public void openPatientsAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/com/matias/physiocarepsp/fxmlviews/patients-view.fxml";
        String title = "Patients | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    /**
     * Handles the action to open the Physios view.
     *
     * @param actionEvent the event triggered by the user
     */
    public void openPhysiosAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/com/matias/physiocarepsp/fxmlviews/physios-view.fxml";
        String title = "Physios | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }
}