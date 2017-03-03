/**
 * Contains the necessary data structures, types and logic for aggregating a
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} or other
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation}s in various ways.
 *
 * <h1>Aggregation Basic terminology</h1>
 * <p>
 * In this section a common terminology is established for describing the aggregation as applied to the data gathered by
 * the profiler.
 * <p>
 * Basically, the aggregation process transforms a set of data items, which consist of an identifying key and some
 * quantitative data, by grouping or partitioning them according to some criterium, and for each partition, calculating
 * derived quantities (typically sums or averages).
 * <p>
 * The result then is a new set of data items, each representing one of the partitions, with a new key (the aggregation
 * key) and associated aggregated quantities.
 * <p>
 * In the profiler, the initial input for the aggregation process is information about stack frames gathered during one
 * or more profiling sessions : how often they were seen, and how much time the CPU spent in the code represented by
 * that frame. This information is gathered in the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} data structure, which contains a
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode} with the data for a particular stack frame.
 * <p>
 * See {@link com.insightfullogic.honest_profiler.core.profiles.lean} for a detailed description of stack frame
 * information and how it is stored in the {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile}.
 * <h1>Data Structures</h1>
 * <h2>Data Structures with Frame-level Information</h2>
 * <p>
 * The information items identifying a specific frame, and which can be used for grouping them for aggregation purposes,
 * are stored in the following data structures in the {@link com.insightfullogic.honest_profiler.core.profiles.lean}
 * package :
 * <ul>
 * <li>the thread id is stored in {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.ThreadInfo}</li>
 * <li>the thread name is stored in {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.ThreadInfo}</li>
 * <li>the method id is stored in {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo}</li>
 * <li>the name of the Java source file is stored in
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo}</li>
 * <li>the class name is stored in {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo}</li>
 * <li>the method name is stored in {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo}</li>
 * <li>the line number in the source code is stored in
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo}</li>
 * <li>the byte code index is stored in
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo}</li>
 * <li>the list of its calling methods, i.e. the ancestor frames, is stored implicitly in the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode} parent-child relationships</li>
 * <li>the profile session the frame was captured in is implicit in the Object identity of the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} : a
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} is the result of the information captured
 * in a session.</li>
 * </ul>
 * <p>
 * <b>NOTE 1</b> The method name by itself does not include the class name, so it isn't terribly useful. Instead, the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo} stores a derived piece of information
 * : the Fully Qualified Method Name (FQMN), which is the FQCN (Fully Qualified Class Name, stored as class name in the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo}) followed by "." followed by the
 * method name.
 * <p>
 * <b>NOTE 2</b> The method signature is currently not available in the profiler agent output. It would be useful to
 * distinguish between methods with the same name but different signatures.
 * <p>
 * The numerical data gathered for the frame is stored in
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo}.
 * <p>
 * This information for a frame is encapsulated in the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode} when combined with the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} metadata : it contains the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo} and the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo} directly. The
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo} is stored in the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} method metadata map, and can be retrieved
 * using the method id from the {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo}. The
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.ThreadInfo} is stored in the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} thread metadata map, and can be retrieved
 * by determining the root ancestor {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode}. The
 * list of ancestor frames finally is implicity determined by the parent property of the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}.
 * </p>
 * <h2>Aggregation Concepts and Data Structures</h2>
 * <h3>Aggregation Key</h3>
 * <p>
 * As stated before, the aggregation key is the key identifying a group of data items aggregated together. In this
 * application, the key is always a String
 * <p>
 * That said, there is actually an "implicit" part to the "real" key : the list of ancestor frames. If this information
 * is (implicitly) used to group data items, the result will be a tree data structure preserving the parent-child
 * relationship.
 * </p>
 * <h3>Grouping / Partitioning</h3>
 * <p>
 * There are only a limited number of ways the frames can be partitioned for aggregation in a meaningful way. These have
 * been codified in the {@link com.insightfullogic.honest_profiler.core.aggregation.grouping} package :
 * <ul>
 * <li>the {@link com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping} indicates how to
 * partition frames using thread information from
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.ThreadInfo}. Thread-based partitioning is
 * performed only on {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode}s. All descendants of
 * the {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode}s in a partition belong to the same
 * partition. Following groupings are (currently) defined :
 * <ul>
 * <li>By thread id : this is the most distinctive way of partitioning on thread information, every thread gets a
 * separate group.</li>
 * <li>By thread name : this intermediate grouping is useful for 2 purposes : it can group together all "noise" (error
 * frames emitted by the profiler agent can be grouped into a single "bucket" easily), and it can be used to group
 * frames from 2 different profiler sessions together for comparison.</li>
 * <li>All threads together : lumps all frames into a single group.</li>
 * </ul>
 * </li>
 * <li>the {@link com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping} indicates how to
 * partition frames using frame information from
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo} and
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo}. Frame-based partitioning is performed
 * only on {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s which are not
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode}s. Following groupings are (currently)
 * defined :
 * <ul>
 * <li>By method id : this uses the internal JVM id for identifying a method. Frames for methods with the same name but
 * different signatures will end up in different groups.</li>
 * <li>By FQMN : this distinguishes methods with a different name, but groups together methods with the same name but
 * different signature. Useful when comparing frames from different sessions with different versions of a code base,
 * since the line numbers for particular frames can change even though they are in the same method, which breaks
 * comparison if line numbers are included.</li>
 * <li>By FQMN and line number : this is more distinctive than "By FQMN" or "By method id" since frames executing
 * different lines in the same method will end up in different groups.</li>
 * <li>By FQMN and BCI : probably slightly more distinctive than "By FQMN and line number", since a line of code may be
 * translated in multiple bytecode instructions.</li>
 * </ul>
 * </li>
 * <li>the {@link com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping} combines a
 * {@link com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping} and a
 * {@link com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping}. The partitioning can be
 * performed on both {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode}s and
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s.</li>
 * </ul>
 * <p>
 * The Grouping classes contain logic which generates a String aggregation key based on the type of the grouping. E.g.
 * when grouping by FQMN, the key is the FQMN.
 * </p>
 * <h3>Basic Aggregation Results</h3>
 * <p>
 * The {@link com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation} is the superclass for results of
 * a basic aggregation operation. There are 2 subclasses :
 * <ul>
 * <li>a {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat} is an
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation} in which the parent-child key
 * information is not preserved. It contains e {@link com.sun.tools.javac.util.List} of
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry}s. An
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry} is the result of aggregating items
 * in a single partition.</li>
 * <li>a {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree} is an
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation} which implicitly preserves (some of)
 * the parent-child key information. It consists of
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node}s arranged in a tree structure.</li>
 * </ul>
 * <p>
 * An {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry} contains :
 * <ul>
 * <li>a reference to the containing
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation}</li>
 * <li>the key generated by the {@link com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping}
 * used for partitioning the {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s</li>
 * <li>a {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo} object containing the sum of
 * the corresponding quantities in the aggregated
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s</li>
 * <li>a collection containing references to the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s aggregated by the
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry}</li>
 * <li>a {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo} instance called the Reference,
 * used for calculating percentages. The Reference is typically either the result of adding the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo} of all
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s in a
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile}, or adding together all
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo}s from the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s belonging to the same thread.</li>
 * </ul>
 * <p>
 * A {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node} contains in addition to the
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry} superclass contents, a
 * {@link java.util.Map} mapping the aggregation key to the child
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node} with that key.
 * </p>
 * <h3>Aggregators</h3>
 * <p>
 * The aggregation processes (or transformations) are called aggregators, and are codified in the
 * {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator} package. The
 * {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.ProfileAggregator} interface should be
 * implemented by aggregators which only act on a
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile}. The
 * {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.SubAggregator} interface is for aggregators
 * which operate on an {@link com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation} or a subset
 * thereof.
 * <p>
 * The {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.FlatProfileAggregator} aggregates a
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} into a
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat}, applying the specified
 * {@link com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping} and discarding the
 * parent-child relationship information between frames.
 * <p>
 * The {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.FlatProfileAggregator}
 * {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.ProfileAggregator} aggregates a
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} into a
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat}, applying the specified
 * {@link com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping} and discarding the
 * parent-child relationship information between frames.
 * <p>
 * The {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.TreeProfileAggregator}
 * {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.ProfileAggregator} aggregates a
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} into a
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree}, applying the specified
 * {@link com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping} and preserving the
 * parent-child relationship information between frames by only adding
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s in the same position in the tree.
 * <p>
 * The {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.DescendantFlatAggregator}
 * {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.SubAggregator} aggregates all the descendants
 * of the {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s aggregated in a
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node} into a
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat} preserving the aggregation key
 * (i.e. implicitly using the same
 * {@link com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping}) as used for generating the
 * original {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node}.
 * <p>
 * The {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.DescendantTreeAggregator}
 * {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.SubAggregator} aggregates all the descendants
 * of the {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s aggregated in a
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry} into a
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree} preserving the explicit aggregation
 * key (i.e. implicitly using the same
 * {@link com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping}) as used for generating the
 * original {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry}, but by partitioning
 * using the parent-child relationship of the {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s.
 * <p>
 * The {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.AncestorTreeAggregator}
 * {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.SubAggregator} does essentially the same thing
 * as the {@link com.insightfullogic.honest_profiler.core.aggregation.aggregator.DescendantTreeAggregator} but
 * aggregates the ancestor {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s instead of the
 * descendants.
 * </p>
 * <h3>Diff Aggregation Results</h3>
 * <p>
 * A Diff Aggregation is an aggregation of two distinct
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation}s, typically used to compare two
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile}s and calculating the differences.
 * <p>
 * These data structures are codified in the {@link com.insightfullogic.honest_profiler.core.aggregation.result.diff}
 * package. The {@link com.insightfullogic.honest_profiler.core.aggregation.result.diff.AbstractDiff} superclass
 * encapsulates two {@link com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation}s of the same type,
 * called the Baseline {@link com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation} and the New
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation}.
 * <p>
 * The {@link com.insightfullogic.honest_profiler.core.aggregation.result.diff.FlatDiff} and
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.diff.TreeDiff} structures are used to compare two
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat}s or two
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree}s respectively. They contain
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry}s and
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode}s respectively, which in turn each
 * encapsulate two {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry}s or two
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node}s with the same aggregation key.
 * </p>
 * <h1>Filters</h1>
 * <p>
 * When investigating aggregations it is often useful to be able to filter them. The concepts and mechanisms to filter
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation}s and
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.diff.AbstractDiff}s have been codified in the
 * {@link com.insightfullogic.honest_profiler.core.aggregation.filter} package.
 *
 * <h2>FilterItem</h2>
 * <p>
 * A "simple" filter is codified by the {@link com.insightfullogic.honest_profiler.core.aggregation.filter.FilterItem}
 * and contains 3 components :
 * <ul>
 * <li>a {@link com.insightfullogic.honest_profiler.core.aggregation.filter.Target} which describes the aggregated
 * quantity which will be used to filter on</li>
 * <li>a {@link com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison}, which specifies the comparison
 * operation which will be carried out on the {@link com.insightfullogic.honest_profiler.core.aggregation.filter.Target}
 * quantity</li>
 * <li>a value (of type {@link com.insightfullogic.honest_profiler.core.aggregation.filter.ValueType}) which the
 * {@link com.insightfullogic.honest_profiler.core.aggregation.filter.Target} quantity will be compared against.</li>
 * </ul>
 * <p>
 * The {@link com.insightfullogic.honest_profiler.core.aggregation.filter.Target} enumeration values additionally
 * contain the logic for extracting the target quantity from an
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry} or
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry}. The
 * {@link com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison} enumeration values contain the logic
 * for carrying out the comparison against the various
 * {@link com.insightfullogic.honest_profiler.core.aggregation.filter.ValueType}s.
 * <p>
 * A {@link com.insightfullogic.honest_profiler.core.aggregation.filter.FilterPredicate} is a
 * {@link java.util.function.Predicate} which encapsulates a
 * {@link com.insightfullogic.honest_profiler.core.aggregation.filter.Target},
 * {@link com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison} and a value, and can test an
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry} or
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry}.
 * <p>
 * <b>NOTE</b> Yes, actually the {@link com.insightfullogic.honest_profiler.core.aggregation.filter.FilterPredicate} and
 * {@link com.insightfullogic.honest_profiler.core.aggregation.filter.FilterItem} could probably be rolled into one
 * class.
 * <h2>FilterSpecification</h2>
 * <p>
 * The {@link com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification} specifies a filter
 * composed of "simple" filters, i.e. {@link com.insightfullogic.honest_profiler.core.aggregation.filter.FilterItem},
 * with 2 additional specialized filtering options : the quick filter and the error filter. It emits a
 * {@link java.util.function.Predicate} chain of {@link java.util.function.Predicate}s which are "and"-ed together.
 * <p>
 * When the error filter is enabled, the filter {@link java.util.function.Predicate} chain emitted by the
 * {@link com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification} starts with a
 * {@link java.util.function.Predicate} filtering out any
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s whose the method name indicates that the
 * frame is actually a non-Java "error frame" emitted by the profiler agent. See the
 * {@link com.insightfullogic.honest_profiler.core.parser.LogParser} source code for the list of the possible errors.
 * <p>
 * The quick filter is a simple filter which adds a {@link java.util.function.Predicate} to the chain which checks
 * whether the String aggregation key of the
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry} or
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry} contains the String specified by
 * the quick filter.
 * <p>
 * The {@link java.util.function.Predicate} chain finishes by applying the {@link java.util.function.Predicate}s emitted
 * by the contained {@link com.insightfullogic.honest_profiler.core.aggregation.filter.FilterItem}s.
 * <h2>Filtering Logic</h2>
 * <p>
 * For a collection of {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry}s or
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry}s the filtering logic is trivial :
 * apply the filter to each item, and return a new collection containing only the accepted items.
 * <p>
 * When filtering a {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree} there are several
 * ways to apply the filter. Only one is currently explicitly implemented, in
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node#copyWithFilter(java.util.function.Predicate)}.
 * This will visit all descendant {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node}s of
 * the {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node} the method is called on (using
 * the predicate emitted by a {@link com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification}),
 * and retain all {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node}s which are accepted,
 * as well as their ancestors.
 * <p>
 * The descendants of an accepted {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node} in
 * this algorithm are not retained if none of them are accepted by the filter.
 * <p>
 * It is possible that the alternative mechanism (also retain all descendants of an accepted
 * {@link com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node}) will be implemented, if the
 * feature is called for, in which case the
 * {@link com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification} probably needs to be
 * configurable with a FilterStrategy or something similar.
 */
package com.insightfullogic.honest_profiler.core.aggregation;