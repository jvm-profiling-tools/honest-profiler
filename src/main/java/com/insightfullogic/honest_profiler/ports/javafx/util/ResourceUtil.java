package com.insightfullogic.honest_profiler.ports.javafx.util;

import static java.util.Locale.ENGLISH;
import static java.util.ResourceBundle.getBundle;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utility class for working with the I18N resources.
 * <p>
 * Not sure if this shouldn't be called I18NUtil.
 */
public final class ResourceUtil
{
    // Class Properties

    ///////////////////////////////////////////////////////////////
    // Constants recording the keys in the I18N resource bundle. //
    ///////////////////////////////////////////////////////////////

    // - InfoBar help messages

    // -- For the root MenuBar
    public static final String INFO_MENU_ROOT = "menu.root.info";

    // -- For the ChoiceBoxes
    public static final String INFO_CHOICE_VIEWTYPE = "choice.viewtype.info";
    public static final String INFO_CHOICE_FILTERTYPE = "choice.filtertype.info";
    public static final String INFO_CHOICE_FILTERTARGET = "choice.filtertarget.info";
    public static final String INFO_CHOICE_COMPARISONOPERATOR = "choice.comparisonOperator.info";

    // -- For the Buttons
    public static final String INFO_BUTTON_FREEZE_UNFROZEN = "button.freeze.unfrozen.info";
    public static final String INFO_BUTTON_FREEZE_FROZEN = "button.freeze.frozen.info";
    public static final String INFO_BUTTON_COMPARE = "button.compare.info";
    public static final String INFO_BUTTON_FILTER = "button.filter.info";
    public static final String INFO_BUTTON_COLUMNVIEW = "button.columnView.info";
    public static final String INFO_BUTTON_EXPORT = "button.flat.export.info";
    public static final String INFO_BUTTON_QUICKFILTER = "button.quickFilter.info";
    public static final String INFO_BUTTON_EXPANDALL = "button.expandAll.info";
    public static final String INFO_BUTTON_COLLAPSEALLALL = "button.collapseAll.info";
    public static final String INFO_BUTTON_ADDFILTER = "button.addFilter.info";
    public static final String INFO_BUTTON_REMOVEFILTER = "button.removeFilter.info";

    // -- For text input fields
    public static final String INFO_INPUT_QUICKFILTER = "input.quickFilter.info";
    public static final String INFO_INPUT_FILTERVALUE = "input.filterValue.info";

    // -- For Labels
    public static final String INFO_LABEL_PROFILESAMPLECOUNT = "label.profileSampleCount.info";
    public static final String INFO_LABEL_BASESOURCE = "label.baseSource.info";
    public static final String INFO_LABEL_NEWSOURCE = "label.newSource.info";

    public static final String INFO_LABEL_ANCESTORS = "label.ancestors.info";
    public static final String INFO_LABEL_DESCENDANTS = "label.descendants.info";

    // -- For CheckBoxes
    public static final String INFO_CHECK_HIDEERRORTHREADS = "check.hideErrorThreads.info";

    public static final String INFO_TABLE_FLAT = "table.flat.info";
    public static final String INFO_TABLE_FLATDIFF = "table.flatDiff.info";
    public static final String INFO_TABLE_TREE = "table.tree.info";
    public static final String INFO_TABLE_TREEDIFF = "table.treeDiff.info";

    // -- For ListViews
    public static final String INFO_LIST_FILTERS = "list.filters.info";

    // -- For Tabs
    public static final String INFO_TAB_PROFILE = "tab.profile.info";
    public static final String INFO_TAB_PROFILEDIFF = "tab.profileDiff.info";

    // - Text Content

    // -- For Labels
    public static final String CONTENT_LABEL_PROFILESAMPLECOUNT = "label.profileSampleCount.content";
    public static final String CONTENT_LABEL_EXCEPTION = "label.exception.content";

    // -- For Tab headers
    public static final String CONTENT_TAB_LOADING = "tab.loading.content";

    // - Context Menu Item labels

    // -- For TreeItems
    public static final String CTXMENU_TREE_EXPANDFULLY = "ctxmenu.tree.expandFully";
    public static final String CTXMENU_TREE_EXPANDFIRSTONLY = "ctxmenu.tree.expandFirstOnly";
    public static final String CTXMENU_TREE_COLLAPSE = "ctxmenu.tree.collapse";
    public static final String CTXMENU_TREE_EXPORTSUBTREE = "ctxmenu.tree.exportSubtree";

    // -- For Column View Groups
    public static final String CTXMENU_COLVW_SHOWSELFALL = "ctxmenu.colView.showSelfAll";
    public static final String CTXMENU_COLVW_HIDESELFALL = "ctxmenu.colView.hideSelfAll";
    public static final String CTXMENU_COLVW_SHOWTOTALALL = "ctxmenu.colView.showTotalAll";
    public static final String CTXMENU_COLVW_HIDETOTALALL = "ctxmenu.colView.hideTotalAll";
    public static final String CTXMENU_COLVW_SHOWCNTALL = "ctxmenu.colView.showCntAll";
    public static final String CTXMENU_COLVW_HIDECNTALL = "ctxmenu.colView.hideCntAll";
    public static final String CTXMENU_COLVW_SHOWPCTALL = "ctxmenu.colView.showPctAll";
    public static final String CTXMENU_COLVW_HIDEPCTALL = "ctxmenu.colView.hidePctAll";
    public static final String CTXMENU_COLVW_SHOWTIMEALL = "ctxmenu.colView.showTimeAll";
    public static final String CTXMENU_COLVW_HIDETIMEALL = "ctxmenu.colView.hideTimeAll";
    public static final String CTXMENU_COLVW_SHOWNONDIFFALL = "ctxmenu.colView.showNonDiffAll";
    public static final String CTXMENU_COLVW_HIDENONDIFFALL = "ctxmenu.colView.hideNonDiffAll";
    public static final String CTXMENU_COLVW_SHOWDIFFALL = "ctxmenu.colView.showDiffAll";
    public static final String CTXMENU_COLVW_HIDEDIFFALL = "ctxmenu.colView.hideDiffAll";

    // - Tooltip messages

    // -- For Buttons
    public static final String TOOLTIP_BUTTON_FREEZE_UNFROZEN = "button.freeze.unfrozen.tooltip";
    public static final String TOOLTIP_BUTTON_FREEZE_FROZEN = "button.freeze.frozen.tooltip";

    // - Column Headers
    public static final String COLUMN_METHOD = "column.method";
    public static final String COLUMN_SELF_PCT_GRAPH = "column.selfCntPctGraph";
    public static final String COLUMN_TOTAL_PCT_GRAPH = "column.totalCntPctGraph";

    public static final String COLUMN_SELF_CNT = "column.selfCnt";
    public static final String COLUMN_SELF_TIME = "column.selfTime";
    public static final String COLUMN_SELF_CNT_DIFF = "column.selfCntDiff";
    public static final String COLUMN_SELF_TIME_DIFF = "column.selfTimeDiff";
    public static final String COLUMN_SELF_CNT_PCT = "column.selfCntPct";
    public static final String COLUMN_SELF_TIME_PCT = "column.selfTimePct";
    public static final String COLUMN_SELF_CNT_PCT_DIFF = "column.selfCntPctDiff";
    public static final String COLUMN_SELF_TIME_PCT_DIFF = "column.selfTimePctDiff";

    public static final String COLUMN_TOTAL_CNT = "column.totalCnt";
    public static final String COLUMN_TOTAL_TIME = "column.totalTime";
    public static final String COLUMN_TOTAL_CNT_DIFF = "column.totalCntDiff";
    public static final String COLUMN_TOTAL_TIME_DIFF = "column.totalTimeDiff";
    public static final String COLUMN_TOTAL_CNT_PCT = "column.totalCntPct";
    public static final String COLUMN_TOTAL_TIME_PCT = "column.totalTimePct";
    public static final String COLUMN_TOTAL_CNT_PCT_DIFF = "column.totalCntPctDiff";
    public static final String COLUMN_TOTAL_TIME_PCT_DIFF = "column.totalTimePctDiff";

    // - Dialog Titles

    public static final String TITLE_DIALOG_OPENFILE = "dialog.openFile.title";
    public static final String TITLE_DIALOG_SPECIFYFILTERS = "dialog.specifyFilters.title";
    public static final String TITLE_DIALOG_SPECIFYFILTER = "dialog.specifyFilter.title";

    public static final String TITLE_DIALOG_ERR_OPENPROFILE = "dialog.err.openProfile.title";
    public static final String TITLE_DIALOG_ERR_EXPORTPROFILE = "dialog.err.exportProfile.title";
    public static final String TITLE_DIALOG_ERR_ALREADYOPENPROFILE = "dialog.err.alreadyOpen.title";

    // - Dialog Headers

    public static final String HEADER_DIALOG_ERR_OPENPROFILE = "dialog.err.openProfile.header";
    public static final String HEADER_DIALOG_ERR_EXPORTPROFILE = "dialog.err.exportProfile.header";
    public static final String HEADER_DIALOG_ERR_ALREADYOPENPROFILE = "dialog.err.alreadyOpen.header";

    // - Dialog Content

    public static final String MESSAGE_DIALOG_ERR_OPENPROFILE = "dialog.err.openProfile.message";
    public static final String MESSAGE_DIALOG_ERR_TASKCANCELED = "dialog.err.taskCanceled.message";
    public static final String MESSAGE_DIALOG_ERR_EXPORTPROFILE = "dialog.err.exportProfile.message";
    public static final String MESSAGE_DIALOG_ERR_ALREADYOPENPROFILE = "dialog.err.alreadyOpen.message";

    // - File Types

    public static final String TYPE_FILE_HP = "type.file.hp";
    public static final String TYPE_FILE_ALL = "type.file.all";

    // - Exception Messages

    public static final String EXCEPTION_DIALOGCREATIONFAILED = "exception.dialogCreationFailed.message";

    // - General Strings

    public static final String GENERAL_UNKNOWN = "general.unknown";
    public static final String GENERAL_THREAD = "general.thread";
    public static final String GENERAL_DEPTH = "general.depth";
    public static final String GENERAL_SAMPLECOUNT = "general.sampleCount";

    ///////////////////////////////////////////
    // Bundle- and Locale-related constants. //
    ///////////////////////////////////////////

    private static final Locale DEFAULT_LOCALE = ENGLISH;
    private static final String BUNDLE_BASE = "com.insightfullogic.honest_profiler.ports.javafx.i18n.HPUIBundle";
    private static final ResourceBundle DEFAULT_BUNDLE = getBundle(BUNDLE_BASE, DEFAULT_LOCALE);

    /**
     * Returns the default {@link Locale} for the application, which is set to {@link Locale#ENGLISH} since that's the
     * only available {@link ResourceBundle} at time of writing.
     * <p>
     * @return the default {@link Locale}
     */
    public static final Locale getDefaultLocale()
    {
        return DEFAULT_LOCALE;
    }

    /**
     * Returns the {@link ResourceBundle} for the default {@link Locale}.
     * <p>
     * @return the {@link ResourceBundle} for the default {@link Locale}
     */
    public static ResourceBundle getDefaultBundle()
    {
        return DEFAULT_BUNDLE;
    }

    /**
     * Formats a message from the specified {@link ResourceBundle} using the specified arguments.
     * <p>
     * @param locale the {@link Locale} (see {@link MessageFormat#setLocale(Locale)})
     * @param bundle the {@link ResourceBundle} containing the message pattern
     * @param key the key of the message pattern in the {@link ResourceBundle}
     * @param args the arguments needed for formatting the message
     * @return the formatted message
     */
    public static String format(Locale locale, ResourceBundle bundle, String key, Object... args)
    {
        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(locale);
        formatter.applyPattern(bundle.getString(key));
        return formatter.format(args);
    }

    // Instance Constructors

    /**
     * Private constructor for Utility Class.
     */
    private ResourceUtil()
    {
        // Private constructor for Utility Class
    }
}
