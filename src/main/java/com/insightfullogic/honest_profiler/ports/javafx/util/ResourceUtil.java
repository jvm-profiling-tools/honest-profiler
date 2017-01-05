package com.insightfullogic.honest_profiler.ports.javafx.util;

import static java.util.Locale.ENGLISH;
import static java.util.ResourceBundle.getBundle;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class ResourceUtil
{
    public static final String INFO_MENU_ROOT = "menu.root.info";

    public static final String INFO_CHOICE_VIEWTYPE = "choice.viewtype.info";
    public static final String INFO_CHOICE_FILTERTYPE = "choice.filtertype.info";
    public static final String INFO_CHOICE_FILTERTARGET = "choice.filtertarget.info";
    public static final String INFO_CHOICE_COMPARISONOPERATOR = "choice.comparisonOperator.info";

    public static final String INFO_BUTTON_FREEZE_UNFROZEN = "button.freeze.unfrozen.info";
    public static final String INFO_BUTTON_FREEZE_FROZEN = "button.freeze.frozen.info";
    public static final String INFO_BUTTON_COMPARE = "button.compare.info";
    public static final String INFO_BUTTON_FILTER = "button.filter.info";
    public static final String INFO_BUTTON_EXPORT = "button.flat.export.info";
    public static final String INFO_BUTTON_QUICKFILTER = "button.quickFilter.info";
    public static final String INFO_BUTTON_EXPANDALL = "button.expandAll.info";
    public static final String INFO_BUTTON_COLLAPSEALLALL = "button.collapseAll.info";
    public static final String INFO_BUTTON_ADDFILTER = "button.addFilter.info";
    public static final String INFO_BUTTON_REMOVEFILTER = "button.removeFilter.info";

    public static final String INFO_INPUT_QUICKFILTER = "input.quickFilter.info";
    public static final String INFO_INPUT_FILTERVALUE = "input.filterValue.info";

    public static final String INFO_LABEL_PROFILESAMPLECOUNT = "label.profileSampleCount.info";
    public static final String INFO_LABEL_BASESOURCE = "label.baseSource.info";
    public static final String INFO_LABEL_NEWSOURCE = "label.newSource.info";

    public static final String INFO_CHECK_HIDEERRORTHREADS = "check.hideErrorThreads.info";

    public static final String INFO_TABLE_FLAT = "table.flat.info";
    public static final String INFO_TABLE_FLATDIFF = "table.flatDiff.info";
    public static final String INFO_TABLE_TREE = "table.tree.info";
    public static final String INFO_TABLE_TREEDIFF = "table.treeDiff.info";

    public static final String INFO_LIST_FILTERS = "list.filters.info";

    public static final String INFO_TAB_PROFILE = "tab.profile.info";
    public static final String INFO_TAB_PROFILEDIFF = "tab.profileDiff.info";

    public static final String CONTENT_LABEL_PROFILESAMPLECOUNT = "label.profileSampleCount.content";
    public static final String CONTENT_TAB_LOADING = "tab.loading.content";

    public static final String TOOLTIP_BUTTON_FREEZE_UNFROZEN = "button.freeze.unfrozen.tooltip";
    public static final String TOOLTIP_BUTTON_FREEZE_FROZEN = "button.freeze.frozen.tooltip";

    public static final String COLUMN_METHOD = "column.method";
    public static final String COLUMN_SELF_PCT_GRAPH = "column.selfSamplePercentGraph";
    public static final String COLUMN_TOTAL_PCT_GRAPH = "column.totalSamplePercentGraph";
    public static final String COLUMN_SELF_PCT = "column.selfSamplePercent";
    public static final String COLUMN_TOTAL_PCT = "column.totalSamplePercent";
    public static final String COLUMN_SELF_CNT = "column.selfSampleCount";
    public static final String COLUMN_TOTAL_CNT = "column.totalSampleCount";
    public static final String COLUMN_PARENT_CNT = "column.parentSampleCount";
    public static final String COLUMN_PROFILE_CNT = "column.profileSampleCount";
    public static final String COLUMN_SELF_PCT_DIFF = "column.selfSamplePercentDiff";
    public static final String COLUMN_TOTAL_PCT_DIFF = "column.totalSamplePercentDiff";
    public static final String COLUMN_SELF_CNT_DIFF = "column.selfSampleCountDiff";
    public static final String COLUMN_TOTAL_CNT_DIFF = "column.totalSampleCountDiff";
    public static final String COLUMN_PARENT_CNT_DIFF = "column.parentSampleCountDiff";
    public static final String COLUMN_PROFILE_CNT_DIFF = "column.profileSampleCountDiff";

    public static final String TITLE_DIALOG_SPECIFYFILTERS = "dialog.specifyFilters.title";

    private static final Locale DEFAULT_LOCALE = ENGLISH;
    private static final String BUNDLE_BASE = "com.insightfullogic.honest_profiler.ports.javafx.i18n.HPUIBundle";
    private static final ResourceBundle DEFAULT_BUNDLE = getBundle(BUNDLE_BASE, DEFAULT_LOCALE);

    public static final Locale getDefaultLocale()
    {
        return DEFAULT_LOCALE;
    }

    public static ResourceBundle getDefaultBundle()
    {
        return DEFAULT_BUNDLE;
    }

    public static String format(Locale locale, ResourceBundle bundle, String key, Object... args)
    {
        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(locale);
        formatter.applyPattern(bundle.getString(key));
        return formatter.format(args);
    }

    private ResourceUtil()
    {
        // Private constructor for Utility Class
    }
}
