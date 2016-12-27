package com.insightfullogic.honest_profiler.core.filters;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;

public final class TotalTimeShareFilter extends TimeShareFilter {

  TotalTimeShareFilter(double minShare) {
    super(minShare);
  }

  public TotalTimeShareFilter(Mode mode, double minShare) {
    super(mode, minShare);
  }

  @Override
  protected double flatField(FlatProfileEntry entry) {
    return entry.getTotalTimeShare();
  }

  @Override
  protected double treeField(ProfileNode node) {
    return node.getTotalTimeShare();
  }
}
