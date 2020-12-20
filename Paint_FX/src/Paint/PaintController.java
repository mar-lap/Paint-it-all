package Paint;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

class DragContext {

    double anchorX;
    double anchorY;

    double translateX;
    double translateY;

}

public class PaintController {
    @FXML
    private BorderPane borderPane;

    @FXML
    private Canvas canvas;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private TextField brushSize;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private MenuItem save;

    @FXML
    private MenuItem insert;

    @FXML
    private MenuItem exit;

    @FXML
    private CheckBox grid;

    @FXML
    private Button getBack;

    @FXML
    private Button getForward;

    @FXML
    private Menu scheme;

    @FXML
    private Menu colorScheme;

    @FXML
    private MenuItem AddNew;

    @FXML
    private MenuItem AddNewColor;

    private ArrayList<Image> DrawOperations = new ArrayList<>();

    private ArrayList<Image> Schemes = new ArrayList<>();
    private ArrayList<Image> ColorSchemes = new ArrayList<>();

    ArrayList<MenuItem> SchemeItems = new ArrayList<>();
    ArrayList<MenuItem> ColorSchemeItems = new ArrayList<>();

    private int ind_back = 1;
    private int ind_forward = 0;

    /*use for scaling*/
    DoubleProperty myScale = new SimpleDoubleProperty(1.0);

    private static final double MAX_SCALE = 10.0d;
    private static final double MIN_SCALE = .1d;

    public double getScale() {
        return myScale.get();
    }

    public void setScale( double scale) {
        myScale.set(scale);
    }

    public void setPivot(double x, double y) {
        canvas.setTranslateX(canvas.getTranslateX() - x);
        canvas.setTranslateY(canvas.getTranslateY() - y);
    }

    public double getBoundsX() {
        return canvas.getBoundsInParent().getWidth()/2 + canvas.getBoundsInParent().getMinX();
    }

    public double getBoundsY() {
        return canvas.getBoundsInParent().getHeight()/2 + canvas.getBoundsInParent().getMinY();
    }

    public void initialize() {
        canvas.scaleXProperty().bind(myScale);
        canvas.scaleYProperty().bind(myScale);

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        addOperation();
        addScheme();
        addColorScheme();
        addSchemeItems();
        addColorSchemeItems();
        setCombination();
    }

    public void setCombination() {
        KeyCombination Exit = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);
        exit.setAccelerator(Exit);

        KeyCombination Save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        save.setAccelerator(Save);

        KeyCombination Insert = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
        insert.setAccelerator(Insert);
    }

    public void addScheme() {
        Schemes.add(new Image("/Paint/resources/scheme/Butterfly.jpg"));
        Schemes.add(new Image("/Paint/resources/scheme/Camel.jpg"));
        Schemes.add(new Image("/Paint/resources/scheme/Crocodile.jpg"));
        Schemes.add(new Image("/Paint/resources/scheme/Duck.jpg"));
        Schemes.add(new Image("/Paint/resources/scheme/Elephant.jpg"));
        Schemes.add(new Image("/Paint/resources/scheme/Fish.jpg"));
        Schemes.add(new Image("/Paint/resources/scheme/Goose.jpg"));
        Schemes.add(new Image("/Paint/resources/scheme/House.jpg"));
        Schemes.add(new Image("/Paint/resources/scheme/Kangaroo.jpg"));
        Schemes.add(new Image("/Paint/resources/scheme/Parrot.jpg"));
        Schemes.add(new Image("/Paint/resources/scheme/Rabbit.jpg"));
        Schemes.add(new Image("/Paint/resources/scheme/Squirrel.jpg"));
    }

    public void  addColorScheme() {
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/AmNam.png"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/Cat.jpg"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/CatWithBall.jpg"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/ChristmasTree.jpg"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/Dog.jpg"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/Flower.jpg"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/Guitar.jpg"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/Hamster.png"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/HelloKitty.jpg"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/Horse.gif"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/Minions.jpg"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/Pikachu.jpg"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/Raspberry.jpg"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/Star.jpg"));
        ColorSchemes.add(new Image("/Paint/resources/colorScheme/Unicorn.jpg"));
    }

    public String getSchemeName(String path) {
        int ind = path.length() - 1;
        StringBuffer NameBuffer;
        String name = null;

        while (ind >= 0 && path.charAt(ind) != '/') {
            name += path.charAt(ind);
            ind--;
        }
        ind = 0;

        while (name.charAt(ind) != '.')
            ind++;
        ind++;

        name = name.substring(ind);
        NameBuffer = new StringBuffer(name);
        NameBuffer.reverse();
        name = NameBuffer.toString();

        return name;
    }

    public void addSchemeItems() {
        int i;
        Image icon;

        for (i = 0; i < Schemes.size(); i++)
            SchemeItems.add(new MenuItem());

        for (i = 0; i < Schemes.size(); i++) {
            icon = new Image(Schemes.get(i).getUrl(), 30, 30, false, true);
            SchemeItems.get(i).setText(getSchemeName(Schemes.get(i).getUrl()));
            SchemeItems.get(i).setGraphic(new ImageView(icon));
        }
        onChooseScheme();
        AddNew.setGraphic(new ImageView(new Image("/Paint/resources/icons/addIcon.png", 30, 30, false,true)));
        scheme.getItems().addAll(SchemeItems);
    }

    public void addColorSchemeItems() {
        int i;
        Image icon;

        for (i = 0; i < ColorSchemes.size(); i++)
            ColorSchemeItems.add(new MenuItem());

        for (i = 0; i< ColorSchemes.size(); i++) {
            icon = new Image(ColorSchemes.get(i).getUrl(), 30, 30, false, true);
            ColorSchemeItems.get(i).setText(getSchemeName(ColorSchemes.get(i).getUrl()));
            ColorSchemeItems.get(i).setGraphic(new ImageView(icon));
        }
        onChooseColorScheme();
        AddNewColor.setGraphic(new ImageView(new Image("/Paint/resources/icons/addIcon.png", 30, 30, false,true)));
        colorScheme.getItems().addAll(ColorSchemeItems);
    }

    public void showScheme(Image image) {
        Stage stage = new Stage();

        HBox view = new HBox();
        BackgroundImage picture = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false,true, true));
        view.setBackground(new Background(picture));

        Scene scene = new Scene(view, image.getWidth(), image.getHeight());
        stage.setScene(scene);
        stage.setTitle("Your scheme. Enjoy!");
        stage.getIcons().add(new Image("/Paint/resources/icons/paintIcon.png"));
        stage.show();
    }

    public void onChooseScheme() {
        int i;

        for (i = 0; i < SchemeItems.size(); i++) {
            int finalI = i;
            SchemeItems.get(i).setOnAction(e -> {
                showScheme(Schemes.get(finalI));
            });
        }
    }

    public void onChooseColorScheme() {
        int i;

        for (i = 0; i < ColorSchemeItems.size(); i++) {
            int finalI = i;
            ColorSchemeItems.get(i).setOnAction(e -> {
                showScheme(ColorSchemes.get(finalI));
            });
        }
    }

    public Image addImage() {
        Stage primStage = new Stage();
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

        File SelectedImage = fileChooser.showOpenDialog(primStage);
        Image NewScheme;
        if (SelectedImage != null)
            NewScheme = new Image("file:" + SelectedImage.getAbsolutePath());
        else NewScheme = null;

        return NewScheme;
    }

    @FXML
    public void onAddScheme() {
        int i;
        Image icon = addImage();

        if (icon != null) {
            Schemes.add(icon);
            i = Schemes.size() - 1;

            SchemeItems.add(new MenuItem());
            icon = new Image(Schemes.get(i).getUrl(), 30, 30, false, true);
            SchemeItems.get(i).setText(getSchemeName(Schemes.get(i).getUrl()));
            SchemeItems.get(i).setGraphic(new ImageView(icon));

            scheme.getItems().add(SchemeItems.get(i));
        }
    }

    @FXML
    public void onAddColorScheme() {
        int i;
        Image icon = addImage();

        if (icon != null) {
            ColorSchemes.add(icon);
            i = ColorSchemes.size() - 1;

            ColorSchemeItems.add(new MenuItem());
            icon = new Image(ColorSchemes.get(i).getUrl(), 30, 30, false, true);
            ColorSchemeItems.get(i).setText(getSchemeName(ColorSchemes.get(i).getUrl()));
            ColorSchemeItems.get(i).setGraphic(new ImageView(icon));

            colorScheme.getItems().add(ColorSchemeItems.get(i));
        }
    }

    public void addOperation() {
        GraphicsContext g = canvas.getGraphicsContext2D();

        Image snapshot = canvas.snapshot(null, null);
        DrawOperations.add(snapshot);
    }

    public void onGetBack(int ind) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.drawImage(DrawOperations.get(DrawOperations.size() - ind), 0, 0);
        ind_forward = DrawOperations.size() - ind + 1;
    }

    @FXML
    public void onPressBack() {
        if (ind_back < DrawOperations.size()) {
            getForward.setDisable(false);
            ind_back++;
            onGetBack(ind_back);
        } else {
            getBack.setDisable(true);
        }
    }

    public void onGetForward(int ind) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.drawImage(DrawOperations.get(ind), 0, 0);
    }

    @FXML
    public void onPressForward() {
        if (ind_forward < DrawOperations.size()) {
            getBack.setDisable(false);
            ind_back--;
            onGetForward(ind_forward);
            ind_forward++;
        }
        else {
            getForward.setDisable(true);
        }
    }

    @FXML
    public void onZoom() {
        DragContext nodeDragContext = new DragContext();

        canvas.setOnMousePressed(event -> {
            if(!event.isPrimaryButtonDown())
                return;

            nodeDragContext.anchorX = event.getSceneX();
            nodeDragContext.anchorY = event.getSceneY();

            Node node = (Node) event.getSource();

            nodeDragContext.translateX = node.getTranslateX();
            nodeDragContext.translateY = node.getTranslateY();
        });

        canvas.setOnMouseDragged(e -> {
            if(!e.isPrimaryButtonDown())
                return;

            double scale = getScale();

            Node node = (Node) e.getSource();

            node.setTranslateX(nodeDragContext.translateX + (( e.getSceneX() - nodeDragContext.anchorX) / scale));
            node.setTranslateY(nodeDragContext.translateY + (( e.getSceneY() - nodeDragContext.anchorY) / scale));

            e.consume();
        });

        canvas.setOnScroll(e -> {
            double d = 1.2;

            double scale = getScale();
            double oldScale = scale;

            if (e.getDeltaY() < 0)
                scale /= d;
            else
                scale *= d;

            scale = clamp(scale, MIN_SCALE, MAX_SCALE);

            double f = (scale / oldScale) - 1;

            double dx = (e.getSceneX() - getBoundsX());
            double dy = (e.getSceneY() - getBoundsY());

            setScale(scale);

            setPivot(f*dx, f*dy);

            e.consume();
        });
    }

    public double clamp( double val, double min, double max) {

        if(Double.compare(val, min) < 0)
            return min;

        if(Double.compare(val, max) > 0)
            return max;

        return val;
    }

    @FXML
    public void onDraw() {
        GraphicsContext g = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(e -> {});
        canvas.setOnScroll(e -> {});
        canvas.setOnMouseDragged(e -> {
            double size = Double.parseDouble(brushSize.getText());
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;

            g.setFill(colorPicker.getValue());
            g.fillOval(x, y, size, size);
            addOperation();
        });
    }

    @FXML
    public void onErase() {
        GraphicsContext g = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(e -> {});
        canvas.setOnScroll(e -> {});
        canvas.setOnMouseDragged(e -> {
            double size = Double.parseDouble(brushSize.getText());
            double x = e.getX();
            double y = e.getY();

            g.setFill(Color.WHITE);
            g.fillOval(x, y, size, size);
            addOperation();
        });
    }

    @FXML
    public void onFill() {
        GraphicsContext g = canvas.getGraphicsContext2D();

        canvas.setOnMouseDragged(e -> {});
        canvas.setOnScroll(e -> {});
        canvas.setOnMouseClicked(e -> {
            double height = canvas.getHeight();
            double width = canvas.getWidth();

            g.setFill(colorPicker.getValue());
            g.fillRect(0, 0, width, height);
            addOperation();
        });
    }

    @FXML
    public void onInsertText() {
        GraphicsContext g = canvas.getGraphicsContext2D();

        canvas.setOnMouseDragged(e -> {});
        canvas.setOnScroll(e -> {});
        canvas.setOnMouseClicked(e -> {
            double x = e.getX();
            double y = e.getY();

            Stage stage = new Stage();

            TextField text = new TextField();

            Label line = new Label("Enter the text: ");

            Button butExit = new Button();
            butExit.setText("Enter");
            butExit.setOnAction(ev -> {
                g.setStroke(colorPicker.getValue());
                g.strokeText(text.getText(), x, y);
                addOperation();
                stage.close();
            });
            HBox box = new HBox(line, text, butExit);
            box.setSpacing(10);
            box.setAlignment(Pos.CENTER);

            Scene scene = new Scene(box, 300, 200);
            stage.setScene(scene);
            stage.setTitle("Settings for your text");
            stage.getIcons().add(new Image("Paint/resources/icons/paintIcon.png"));
            stage.show();
        });


    }

    @FXML
    public void onInsertImage() {
        Stage primStage = new Stage();
        GraphicsContext g = canvas.getGraphicsContext2D();
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

        File SelectedImage = fileChooser.showOpenDialog(primStage);

        canvas.setOnMouseClicked(e -> {});
        canvas.setOnMouseDragged(e -> {});
        if (SelectedImage != null) {
            String FilePath = SelectedImage.getPath();
            Image Image = new Image("file:" + FilePath);
            g.drawImage(Image, 0, 0);
            addOperation();
        }
    }

    @FXML
    public void onCreateNew() {
        TextField CanvasWidth = new TextField();
        CanvasWidth.setPromptText("Enter new canvas width");
        CanvasWidth.setPrefWidth(100);

        TextField CanvasHeight = new TextField();
        CanvasHeight.setPromptText("Enter new canvas height");
        CanvasHeight.setPrefWidth(100);

        Button CreateBut = new Button();
        CreateBut.setText("Create");

        VBox box = new VBox();
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(CanvasWidth, CanvasHeight, CreateBut);

        FlowPane pane = new FlowPane();
        pane.setPrefHeight(300);
        pane.setPrefWidth(300);
        pane.getChildren().add(box);
        pane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Settings for your new canvas");
        stage.getIcons().add(new Image("Paint/resources/icons/paintIcon.png"));
        stage.show();

        CreateBut.setOnAction(e -> {
            double CanvasNewWidth = Double.parseDouble(CanvasWidth.getText());
            double CanvasNewHeight = Double.parseDouble(CanvasHeight.getText());

            canvas = new Canvas();
            canvas.setHeight(CanvasNewHeight);
            canvas.setWidth(CanvasNewWidth);
            GraphicsContext g = canvas.getGraphicsContext2D();
            g.setFill(Color.WHITE);
            g.fillRect(0, 0, CanvasNewWidth, CanvasNewHeight);

            scrollPane.setContent(canvas);
            stage.close();
            addOperation();
        });
    }

    @FXML
    public void onLine(){
        GraphicsContext g = canvas.getGraphicsContext2D();

        canvas.setOnMouseDragged(e -> {});
        canvas.setOnMouseClicked(e -> {

            canvas.setOnMouseClicked(ev -> {

                g.setStroke(colorPicker.getValue());
                g.strokeLine(e.getX(), e.getY(), ev.getX(), ev.getY());
                addOperation();

                onLine();
            });
        });

    }

    @FXML
    public void onCircle(){
        GraphicsContext g = canvas.getGraphicsContext2D();

        canvas.setOnMouseDragged(e -> {});
        canvas.setOnMouseClicked(e -> {
            TextField radius = new TextField();

            Button draw = new Button("Draw");
            draw.setFont(Font.font("Arial", FontWeight.LIGHT, 14));

            HBox hBox = new HBox(10, radius, draw);
            hBox.setAlignment(Pos.CENTER);

            Stage stage = new Stage();
            stage.setScene(new Scene(hBox, 400, 300));
            stage.getIcons().add(new Image("Paint/resources/icons/paintIcon.png"));
            stage.setTitle("Enter radius of your circle");
            stage.show();

            draw.setOnAction(ev -> {
                g.setStroke(colorPicker.getValue());
                g.strokeOval(e.getX(), e.getY(), Double.parseDouble(radius.getText()),
                        Double.parseDouble(radius.getText()));
                addOperation();
                stage.close();
            });
        });
    }

    @FXML
    public void onOval(){
        GraphicsContext g = canvas.getGraphicsContext2D();

        canvas.setOnMouseDragged(e -> {});
        canvas.setOnMouseClicked(e -> {
            Stage stage = new Stage();

            TextField width = new TextField();
            width.setPrefWidth(100);

            TextField height = new TextField();
            height.setPrefWidth(100);

            Button draw = new Button("Draw");
            draw.getStylesheets().add("Paint/style/Style.css");

            VBox vBox = new VBox(10, width, height, draw);
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(20, 40, 20, 40));

            stage.setScene(new Scene(vBox, 400, 300));
            stage.getIcons().add(new Image("Paint/resources/icons/paintIcon.png"));
            stage.setTitle("Enter width and height of your oval");
            stage.show();

            draw.setOnAction(ev -> {
                g.setStroke(colorPicker.getValue());
                g.strokeOval(e.getX(), e.getY(), Double.parseDouble(width.getText()),
                        Double.parseDouble(height.getText()));
                addOperation();
                stage.close();
            });
        });
    }

    @FXML
    public void onRect() {
        GraphicsContext g = canvas.getGraphicsContext2D();

        canvas.setOnMouseDragged(e -> {});
        canvas.setOnMouseClicked(e -> {
            Stage stage = new Stage();

            TextField width = new TextField();
            width.setPrefWidth(100);

            TextField height = new TextField();
            height.setPrefWidth(100);

            Button draw = new Button("Draw");
            draw.getStylesheets().add("Paint/style/Style.css");

            VBox vBox = new VBox(10, width, height, draw);
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(20, 40, 20, 40));

            stage.setScene(new Scene(vBox, 400, 300));
            stage.getIcons().add(new Image("Paint/resources/icons/paintIcon.png"));
            stage.setTitle("Enter width and height of your rectangle");
            stage.show();

            draw.setOnAction(ev -> {
                g.setStroke(colorPicker.getValue());
                g.strokeRect(e.getX(), e.getY(), Double.parseDouble(width.getText()),
                        Double.parseDouble(height.getText()));
                addOperation();
                stage.close();
            });
        });

    }

    @FXML
    public void onFillRect() {
        GraphicsContext g = canvas.getGraphicsContext2D();

        canvas.setOnMouseDragged(e -> {});
        canvas.setOnMouseClicked(e -> {
            Stage stage = new Stage();

            TextField width = new TextField();
            width.setPrefWidth(100);

            TextField height = new TextField();
            height.setPrefWidth(100);

            Button draw = new Button("Draw");
            draw.getStylesheets().add("Paint/style/Style.css");

            VBox vBox = new VBox(10, width, height, draw);
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(20, 40, 20, 40));

            stage.setScene(new Scene(vBox, 400, 300));
            stage.getIcons().add(new Image("Paint/resources/icons/paintIcon.png"));
            stage.setTitle("Enter width and height of your rectangle");
            stage.show();

            draw.setOnAction(ev -> {
                g.setFill(colorPicker.getValue());
                g.fillRect(e.getX(), e.getY(), Double.parseDouble(width.getText()),
                        Double.parseDouble(height.getText()));
                addOperation();
                stage.close();
            });
        });

    }

    @FXML
    public void createGrid() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (grid.isSelected()) {
            g.setLineWidth(1.0);
            g.setStroke(colorPicker.getValue());
            for (double x = 0; x < width; x += 20) {
                g.moveTo(x, 0);
                g.lineTo(x, height);
                g.stroke();
            }

            for (double y = 0; y < height; y += 20) {
                g.moveTo(0, y);
                g.lineTo(width, y);
                g.stroke();
            }
        }
        else {
            g.setFill(Color.WHITE);
            g.fillRect(0, 0, width, height);
        }
        addOperation();
    }

    @FXML
    public void onSave() {
        Image snapshot = canvas.snapshot(null, null);

        TextField FileName = new TextField();
        Button enterName = new Button();
        enterName.setText("Save");

        FlowPane pane = new FlowPane(Orientation.HORIZONTAL, 10, 10, FileName, enterName);
        pane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(pane, 300, 100);
        Stage stage = new Stage();
        stage.setTitle("Enter file name");
        stage.getIcons().add(new Image("Paint/resources/icons/paintIcon.png"));
        stage.setScene(scene);
        stage.show();

        enterName.setOnAction(event -> {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null),
                        "png", new File(FileName.getText() + ".png"));
            } catch (Exception e) {
                System.out.println("You can't save this file because of" + e);
            }
            stage.close();
        });
    }

    @FXML
    public void onPrint() {
        Printer printer = Printer.getDefaultPrinter();

        Stage printStage = new Stage();
        PrinterJob printerJob = PrinterJob.createPrinterJob(printer);
        if (printerJob != null) {
            if (printerJob.showPrintDialog(printStage.getOwner()) && printerJob.printPage(canvas)) {
                printerJob.endJob();
            }
        }
    }

    @FXML
    public void onExit() {

        Platform.exit();
    }
}
