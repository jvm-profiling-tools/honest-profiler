package com.insightfullogic.honest_profiler.ports.javafx.util;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.layout.Priority.ALWAYS;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

import com.insightfullogic.honest_profiler.ports.javafx.controller.dialog.DialogController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public final class DialogUtil
{
    public static String FILTER = "/com/insightfullogic/honest_profiler/ports/javafx/fxml/FilterDialog.fxml";
    public static String FILTER_CREATION = "/com/insightfullogic/honest_profiler/ports/javafx/fxml/FilterCreationDialog.fxml";

    private static File CACHED_PARENT_DIR;

    public static <T> DialogController<T> createDialog(String fxml, String title,
        boolean resetOnShow)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(DialogUtil.class.getResource(fxml));
            Parent dialogPane = (Parent) loader.load();
            DialogController<T> controller = loader.getController();

            Dialog<T> dialog = new Dialog<>();
            controller.setDialog(dialog);

            dialog.setDialogPane((DialogPane) dialogPane);
            dialog.setResultConverter(controller.createResultHandler());
            dialog.setTitle(title);
            if (resetOnShow)
            {
                dialog.setOnShown(event -> controller.reset());
            }

            return controller;
        }
        catch (IOException ioe)
        {
            throw new RuntimeException("Failed to instantiate DialogPane for " + fxml, ioe);
        }
    }

    public static File selectLogFile()
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(CACHED_PARENT_DIR);

        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("HP Log Files", "*.hpl"),
            new ExtensionFilter("All Files", "*.*"));

        fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));
        fileChooser.setTitle("Open an Honest Profiler Log File");

        File file = fileChooser.showOpenDialog(null);
        if (file != null)
        {
            CACHED_PARENT_DIR = file.getParentFile();
        }
        return file;
    }

    public static void showExportDialog(Window window, String initialFileName,
        Consumer<PrintWriter> exportMethod)
    {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(CACHED_PARENT_DIR);
        chooser.setInitialFileName(initialFileName);

        File file = chooser.showSaveDialog(window);

        if (file != null)
        {
            CACHED_PARENT_DIR = file.getParentFile();

            try (PrintWriter out = new PrintWriter(file, "UTF-8"))
            {
                exportMethod.accept(out);
                out.flush();
            }
            catch (IOException ioe)
            {
                showExceptionDialog(
                    window,
                    "I/O Issue",
                    "File could not be written",
                    "An issue occurred trying to export data to file "
                        + file.getAbsolutePath()
                        + ".",
                    ioe);
            }
        }
    }

    public static void showExceptionDialog(Window window, String title, String headerText,
        String contentText,
        Throwable t)
    {
        Alert alert = new Alert(ERROR);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        String exceptionText = null;
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw))
        {
            t.printStackTrace(pw);
            exceptionText = sw.toString();
        }
        catch (IOException ioe)
        {
            // Ignore
        }

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, ALWAYS);
        GridPane.setHgrow(textArea, ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    private DialogUtil()
    {
        // Empty Constructor for utility class
    }
}
