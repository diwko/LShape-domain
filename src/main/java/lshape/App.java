package lshape;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.javafx.JavaFXChartFactory;

import java.util.Arrays;

public class App extends Application {
    private Stage stage;
    private Scene scene;
    private TextField textField;
    private Button button;
    private ImageView chartView;
    private HBox vBox;

    private JavaFXChartFactory chartFactory = new JavaFXChartFactory();

    public static void main(String [] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("LShapeDomain");
        stage.setWidth(800);
        stage.setHeight(700);

        chartView = new ImageView();

        Label label = new Label("Number of cuts:");

        textField = new TextField();
        textField.setOnAction(event -> updateImageView());

        button = new Button("Draw");
        button.setOnAction(event -> updateImageView());

        VBox inputBox = new VBox(20);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.getChildren().add(label);
        inputBox.getChildren().add(textField);
        inputBox.getChildren().add(button);

        vBox = new HBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(inputBox);
        vBox.getChildren().add(chartView);
        vBox.setStyle("-fx-background-color: #FFF");

        scene = new Scene(vBox);
        stage.setScene(scene);

        stage.show();
    }

    private void updateImageView() {
        try {
            int cuts = getNumberOfCuts();
            double[] result = LShapeDomainSolver.solve(cuts);
            Arrays.stream(result).forEach(System.out::println);

            showChart(cuts, result);
        } catch (NumberFormatException e) {
            textField.setText(textField.getText() + " <- this is not INTEGER");
        }
    }

    private int getNumberOfCuts() {
        return Integer.parseInt(textField.getText());
    }

    private void showChart(int cuts, double[] result) {
        AWTChart chart = ChartGenerator.draw(cuts, result, chartFactory, "offscreen");
        chartView = chartFactory.bindImageView(chart);
        //chartFactory.addSceneSizeChangedListener(chart, scene);
        vBox.getChildren().remove(vBox.getChildren().size() - 1);
        vBox.getChildren().add(chartView);
    }

}