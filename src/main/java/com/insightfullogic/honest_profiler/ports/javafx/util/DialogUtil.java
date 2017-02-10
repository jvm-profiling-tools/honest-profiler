package com.insightfullogic.honest_profiler.ports.javafx.util;

import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CONTENT_LABEL_EXCEPTION;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.HEADER_DIALOG_ERR_EXPORTPROFILE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.MESSAGE_DIALOG_ERR_EXPORTPROFILE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.TITLE_DIALOG_ERR_EXPORTPROFILE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.TITLE_DIALOG_OPENFILE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.TYPE_FILE_ALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.TYPE_FILE_HP;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.layout.Priority.ALWAYS;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * Utility class for generating various {@link Dialog}s.
 * <p>
 * Whenever a {@link File} is selected (for reading or export) by one of the methods in the class, the parent directory
 * is cached so that subsequent invocations of methods involving file selection start from the currently cached
 * directory. This generally avoids a lot of repetitive file system navigation on the part of the user when working with
 * multiple profiles or exporting a lot of information.
 * <p>
 * This concept could be extended a bit by keeping a mapping the type of file operation (open profile, export data) to
 * separate cached directory, and by providing preferences in the application for preferred locations.
 */
public final class DialogUtil
{
    // Class Properties

    private static File CACHED_PARENT_DIR;

    // Class Methods

    /**
     * Present a {@link Dialog} to the user which allows the selection of a log file, and return the selected
     * {@link File}, if any.
     * <p>
     * The initial directory is the current working directory, if this is the first invocation, or cached directory.
     * When a {@link File} is selected, its parent directory is cached.
     * <p>
     * The rationale behind this is that the directory from which the profiler front-end is started often may be "far
     * away" from the directory where the profiler log files are stored. Also it is quite likely that log files are
     * stored "close to each other" on the file system.
     * <p>
     *
     * @param appCtx the {@link ApplicationContext} for the application
     * @return the selected {@link File}
     */
    public static File selectLogFile(ApplicationContext appCtx)
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(CACHED_PARENT_DIR);

        // Initially show only .hpl files, but support random file extensions too, just in case.
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter(appCtx.textFor(TYPE_FILE_HP), "*.hpl"),
            new ExtensionFilter(appCtx.textFor(TYPE_FILE_ALL), "*.*"));
        fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));

        fileChooser.setTitle(appCtx.textFor(TITLE_DIALOG_OPENFILE));

        File file = fileChooser.showOpenDialog(null);
        if (file != null)
        {
            CACHED_PARENT_DIR = file.getParentFile();
        }
        return file;
    }

    /**
     * Present a {@link Dialog} to the user which allows the selection of a {@link File} into which data will be
     * exported, and write the data to the {@link File}.
     * <p>
     * The initial directory is the current working directory, if this is the first invocation, or cached directory.
     * When a {@link File} is selected, its parent directory is cached.
     * <p>
     * The rationale behind this is that the directory from which the profiler front-end is started often may be "far
     * away" from the directory where the profiler log files are stored. Also it is quite likely that log files are
     * stored "close to each other" on the file system.
     * <p>
     *
     * @param appCtx the {@link ApplicationContext} for the application
     * @param window the containing {@link Window}
     * @param initialFileName the filename initially proposed in the filename input text input
     * @param exportMethod the method which writes the exported data to the {@link File}
     */
    public static void showExportDialog(ApplicationContext appCtx, Window window,
        String initialFileName, Consumer<PrintWriter> exportMethod)
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
                    appCtx,
                    appCtx.textFor(TITLE_DIALOG_ERR_EXPORTPROFILE),
                    appCtx.textFor(HEADER_DIALOG_ERR_EXPORTPROFILE),
                    appCtx.textFor(MESSAGE_DIALOG_ERR_EXPORTPROFILE, file.getAbsolutePath()),
                    ioe);
            }
        }
    }

    /**
     * Show an error {@link Dialog} with the specified properties.
     * <p>
     *
     * @param title the title of the {@link Dialog}
     * @param header the header of the {@link Dialog}
     * @param content the content of the {@link Dialog}
     */
    public static void showErrorDialog(String title, String header, String content)
    {
        Alert alert = new Alert(ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    /**
     * Shows an exception {@link Dialog} with the specified properties. This is an error {@link Dialog} which contains
     * the stack trace of the {@link Throwable} as expandable content.
     * <p>
     *
     * @param appCtx the {@link ApplicationContext} for the application
     * @param title the title of the {@link Dialog}
     * @param header the header of the {@link Dialog}
     * @param content the content of the {@link Dialog}
     * @param t the {@link Throwable} whose stacktrace will be included in the {@link Dialog}
     */
    public static void showExceptionDialog(ApplicationContext appCtx, String title, String header,
        String content, Throwable t)
    {
        Alert alert = new Alert(ERROR);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

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

        Label label = new Label(appCtx.textFor(CONTENT_LABEL_EXCEPTION));

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

    // Instance Constructors

    /**
     * Empty Constructor for utility class.
     */
    private DialogUtil()
    {
        // Empty Constructor for utility class
    }
}
