package com.insightfullogic.honest_profiler.core.filters;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;

public final class SelfTimeShareFilter extends TimeShareFilter {

  SelfTimeShareFilter(double minShare) {
    super(minShare);
  }

  public SelfTimeShareFilter(Mode mode, double minShare) {
    super(mode, minShare);
  }

  @Override
  protected double flatField(FlatProfileEntry entry) {
    return entry.getSelfTimeShare();
  }

  @Override
  protected double treeField(ProfileNode node) {
    return node.getSelfTimeShare();
  }
}
