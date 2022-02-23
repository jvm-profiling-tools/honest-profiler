package com.insightfullogic.honest_profiler.ports.javafx.model;

import static com.insightfullogic.honest_profiler.ports.javafx.util.ConversionUtil.convert;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.format;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.getDefaultBundle;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.getDefaultLocale;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import com.insightfullogic.honest_profiler.ports.javafx.controller.RootController;
import com.insightfullogic.honest_profiler.ports.javafx.model.configuration.Configuration;
import com.insightfullogic.honest_profiler.ports.javafx.model.configuration.FormattingConfiguration;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.concurrent.Task;
import javafx.scene.control.Tab;

/**
 * The ApplicationContext contains state which needs to be shared by all controllers in the application, and methods to
 * access and change that state.
 */
public final class ApplicationContext
{
    // Instance Properties

    // - I18N
    private Locale currentLocale;
    private ResourceBundle currentBundle;

    // - InfoBar current text
    private SimpleStringProperty info;

    // - Profile Context mappings
    private Map<String, ProfileContext> nameToContextMap;
    private Map<String, ProfileContext> pathToContextMap;

    // - Root Controller
    private RootController rootController;

    // - Task Execution
    private ExecutorService executorService = newCachedThreadPool();

    // - Application Configuration
    private SimpleObjectProperty<Configuration> configuration;

    // - Display Formatting
    private DecimalFormat integerDisplayFormat;
    private DecimalFormat numberDisplayFormat;
    private DecimalFormat percentDisplayFormat;
    private DecimalFormat timeDisplayFormat;
    private Function<Long, Double> timeDisplayConverter;

    // - Export Formatting
    private DecimalFormat integerExportFormat;
    private DecimalFormat numberExportFormat;
    private DecimalFormat percentExportFormat;
    private DecimalFormat timeExportFormat;
    private Function<Long, Double> timeExportConverter;

    // Instance Constructors

    /**
     * Create the ApplicationContext with teh specified {@link RootController}.
     * <p>
     * 
     * @param rootController the {@link RootController} for the application
     */
    public ApplicationContext(RootController rootController)
    {
        currentLocale = getDefaultLocale();
        currentBundle = getDefaultBundle();
        info = new SimpleStringProperty();
        this.rootController = rootController;
        nameToContextMap = new HashMap<String, ProfileContext>();
        pathToContextMap = new HashMap<String, ProfileContext>();
        configuration = new SimpleObjectProperty<>();
    }

    // Instance Accessors

    /**
     * Returns the id of the {@link ProfileContext} which tracks the profile based on the specified {@link File}.
     * <p>
     * 
     * @param file the {@link File} containing the Profiling Agent output
     * @return the id of the {@link ProfileContext} which tracks the profile based on the specified {@link File}
     */
    public Integer getContextIdByPath(File file)
    {
        ProfileContext ctx = pathToContextMap.get(file.getAbsolutePath());
        return ctx == null ? null : ctx.getId();
    }

    /**
     * Returns the {@link ProfileContext} with the specified name.
     * <p>
     *
     * @param name the name of the {@link ProfileContext}
     * @return the corresponding {@link ProfileContext}
     */
    public ProfileContext getProfileContext(String name)
    {
        return nameToContextMap.get(name);
    }

    /**
     * Registers a {@link ProfileContext} with this ApplicationContext, making it available as shared state.
     * <p>
     *
     * @param context the {@link ProfileContext} to be registered
     */
    public void registerProfileContext(ProfileContext context)
    {
        nameToContextMap.put(context.getName(), context);
        pathToContextMap.put(context.getFile().getAbsolutePath(), context);
    }

    /**
     * Returns a list of the names of all known {@link ProfileContext}s.
     * <p>
     *
     * @return a list of the names of all known {@link ProfileContext}s
     */
    public List<String> getOpenProfileNames()
    {
        return nameToContextMap.keySet().stream().sorted().collect(toList());
    }

    public ObservableObjectValue<Configuration> getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(Configuration configuration)
    {
        FormattingConfiguration displayFmtCfg = configuration.getDisplayFormattingConfiguration();
        this.integerDisplayFormat = displayFmtCfg.getIntegerFormatter();
        this.numberDisplayFormat = displayFmtCfg.getNumberFormatter();
        this.percentDisplayFormat = displayFmtCfg.getPercentFormatter();
        this.timeDisplayFormat = displayFmtCfg.getTimeFormatter();
        this.timeDisplayConverter = nanos -> convert(displayFmtCfg.getTimeUnit(), nanos);

        FormattingConfiguration exportFmtCfg = configuration.getExportFormattingConfiguration();
        this.integerExportFormat = exportFmtCfg.getIntegerFormatter();
        this.numberExportFormat = exportFmtCfg.getNumberFormatter();
        this.percentExportFormat = exportFmtCfg.getPercentFormatter();
        this.timeExportFormat = exportFmtCfg.getTimeFormatter();
        this.timeExportConverter = nanos -> convert(exportFmtCfg.getTimeUnit(), nanos);

        this.configuration.set(configuration);
    }

    // I18N-related Methods

    /**
     * Returns the internationalized String stored in the application {@link ResourceBundle} for the specified key based
     * on the current {@link Locale}.
     * <p>
     * 
     * @param key the key for the internationalized String in the application {@link ResourceBundle}
     * @return the internationalized String for the specified key
     */
    public String textFor(String key)
    {
        return currentBundle.getString(key);
    }

    /**
     * Returns the internationalized String constructed by looking up the specified key in the application
     * {@link ResourceBundle} based on the current {@link Locale}, interpreting it as a format and formatting it using
     * the specified argument.
     * <p>
     * 
     * @param key the key for the pattern in the application {@link ResourceBundle}
     * @param args the arguments for formatting the pattern
     * @return the constructed internationalized String
     */
    public String textFor(String key, Object... args)
    {
        return format(currentLocale, currentBundle, key, args);
    }

    // InfoBar Methods

    /**
     * Set the text in the InfoBar as per {@link #textFor(String)}.
     * <p>
     * 
     * @param key the key for the internationalized String in the application {@link ResourceBundle}
     */
    public void setInfoFromBundle(String key)
    {
        info.set(textFor(key));
    }

    /**
     * Set the text in the InfoBar as per {@link #textFor(String, Object...)}.
     * <p>
     * 
     * @param key the key for the pattern in the application {@link ResourceBundle}
     * @param args the arguments for formatting the pattern
     */
    public void setInfoFromBundle(String key, Object... args)
    {
        info.set(textFor(key, args));
    }

    /**
     * Sets the text in the InfoBar.
     * 
     * @param text the raw text
     */
    public void setRawInfo(String text)
    {
        info.set(text);
    }

    /**
     * Clears the text in the InfoBar.
     */
    public void clearInfo()
    {
        info.set("");
    }

    /**
     * Returns the InfoBar {@link ObservableStringValue}.
     * <p>
     * 
     * @return the InfoBar {@link ObservableStringValue}.
     */
    public ObservableStringValue getInfo()
    {
        return info;
    }

    // Formatting-related Methods

    /**
     * Return a {@link String} representing the number, formatted using the configured settings, discarding any fraction
     * digits.
     *
     * @param number the {@link Number} to be displayed
     * @return a {@link String} representing the number, formatted using the configured settings
     */
    public String displayIntegral(Number number)
    {
        return integerDisplayFormat.format(number.longValue());
    }

    /**
     * Return a {@link String} representing the number, formatted using the configured settings.
     *
     * @param number the {@link Number} to be displayed
     * @return a {@link String} representing the number, formatted using the configured settings
     */
    public String displayNumber(Number number)
    {
        return numberDisplayFormat.format(number);
    }

    /**
     * Return a {@link String} representing the percentage, formatted using the configured settings.
     *
     * @param number the percentage to be displayed
     * @return a {@link String} representing the percentage, formatted using the configured settings
     */
    public String displayPercent(Number number)
    {
        return percentDisplayFormat.format(number);
    }

    /**
     * Return a {@link String} representing the amount of time, formatted and converted using the configured settings.
     *
     * @param nanos the amount of time, in nanoseconds, to be displayed
     * @return a {@link String} representing the amount of time, formatted using the configured settings
     */
    public String displayTime(Number nanos)
    {
        return timeDisplayFormat.format(timeDisplayConverter.apply(nanos.longValue()));
    }

    /**
     * Return a {@link String} representing the number, formatted using the configured settings, discarding any fraction
     * digits, for exporting to a file.
     *
     * @param number the {@link Number} to be exported
     * @return a {@link String} representing the number, formatted using the configured settings
     */
    public String exportIntegral(Number number)
    {
        return integerExportFormat.format(number.longValue());
    }

    /**
     * Return a {@link String} representing the number, formatted using the configured settings, for exporting to a
     * file.
     *
     * @param number the {@link Number} to be exported
     * @return a {@link String} representing the number, formatted using the configured settings
     */
    public String exportNumber(Number number)
    {
        return numberExportFormat.format(number);
    }

    /**
     * Return a {@link String} representing the percentage, formatted using the configured settings, for exporting to a
     * file.
     *
     * @param number the percentage to be exported
     * @return a {@link String} representing the percentage, formatted using the configured settings
     */
    public String exportPercent(Number number)
    {
        return percentExportFormat.format(number);
    }

    /**
     * Return a {@link String} representing the amount of time, formatted and converted using the configured settings,
     * for exporting to a file.
     *
     * @param nanos the amount of time, in nanoseconds, to be exported
     * @return a {@link String} representing the amount of time, formatted using the configured settings
     */
    public String exportTime(Number nanos)
    {
        return timeExportFormat.format(timeExportConverter.apply(nanos.longValue()));
    }

    // Task-related Methods

    /**
     * Executes a task on a background worker thread.
     * <p>
     * 
     * @param task the task to be executed
     */
    public void execute(Task<?> task)
    {
        executorService.execute(task);
    }

    /**
     * Stop the executorService. If this isn't called on application shutdown, the application shutdown will be held up
     * for a while.
     */
    public void stop()
    {
        this.executorService.shutdown();
    }

    // View Creation Methods

    /**
     * Create a {@link Tab} containing the Diff Views for the specified profiles.
     * <p>
     * 
     * @param baseName the name of the {@link ProfileContext} for the Base profile
     * @param newName the name of the {@link ProfileContext} for the New profile
     */
    public void createDiffView(String baseName, String newName)
    {
        rootController.createDiffTab(baseName, newName);
    }
}
