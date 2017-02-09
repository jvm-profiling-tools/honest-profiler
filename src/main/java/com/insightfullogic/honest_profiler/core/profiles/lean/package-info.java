/**
 * Contains the data structures for storing the information gathered during a profiling session with as little
 * redundancy as possible.
 * <h1>Profiler Basics</h1>
 * <h2>Collected Information</h2>
 * <p>
 * During a profiling session, the following information is gathered :
 * <ul>
 * <li>Stack trace start events, specifying :
 * <ul>
 * <li>the id of the thread the following stack trace sample comes from</li>
 * <li>the timestamp in nanoseconds</li>
 * </ul>
 * <li>Stack trace samples consisting of a series of stack frames, each specifying :
 * <ul>
 * <li>the id of the method being executed in the frame</li>
 * <li>the line number correponding to the code being executed in the frame</li>
 * <li>the Byte Code Index (BCI), pointing to the bytecode instruction being executed in the frame</li>
 * </ul>
 * </li>
 * <li>Thread metadata, which associates a name with the thread id</li>
 * <li>Method metadata, specifying :
 * <ul>
 * <li>the id of the method</li>
 * <li>the name of the Java source file defining the method</li>
 * <li>the fully qualified name of the class defining the method</li>
 * <li>the name of the method</li>
 * </ul>
 * </li>
 * </ul>
 * <h2>Stack Trace Samples</h2>
 * <p>
 * A stack trace sample contains the information about the method currently being executed by the JVM when the sample
 * was taken. It is structured as a hierarchical list of stack frames, where the "bottom frame" (received first)
 * specifies the method whose bytecode is executed in the stack frame. Every subsequent stack frame is the parent of the
 * previous stack frame, and specifies which method called the method in the previous frame.
 * <p>
 * The final "root" frame specifies the top-level method executed by the containing {@link java.lang.Thread}. Typically
 * this is the <code>java.lang.Thread.run()</code> method, but it can also be class initialization or classloader
 * launching code.
 * <h1>{@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} Aggregation</h1>
 * <p>
 * All of this information is aggregated into a
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} object by the
 * {@link com.insightfullogic.honest_profiler.core.collector.lean.LeanLogCollector}.
 * <h2>Metadata</h2>
 * <p>
 * For the metadata, no aggregation takes place, except for the thread name : sometimes multiple Thread metadata events
 * are received for the same thread id, with only one containing the name. In that case, the last metadata with a
 * non-trivial (non-null and not empty) name is retained.
 * </p>
 * <h3>Thread Metadata</h3>
 * <p>
 * The metadata for a thread is stored in the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.ThreadInfo} object. These objects are stored in
 * the {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} in a {@link java.util.Map} with the
 * thread id as key.
 * </p>
 * <h3>Method Metadata</h3>
 * <p>
 * The metadata for a method is stored in the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo} object. These objects are stored in
 * the {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} in a {@link java.util.Map} with the
 * method id as key.
 * </p>
 * <h2>Core Profile Data</h2>
 * <p>
 * The stack frame samples are all aggregated into trees of
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s.
 * </p>
 * <h3>Aggregated Numeric Data</h3>
 * <p>
 * Aggregating data means that you split up the data in groups, each group having a unique identifier or key which we'll
 * call an aggregation key, and calculating some values for each group, which we'll call the aggregated data.
 * <p>
 * The aggregated data in {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile}s is stored in the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo} object. There are only four basic
 * quantities which are directly or indirectly measured, and these are all represented as members of
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo} :
 * <ul>
 * <li>Total Sample Count : the number of times a frame was seen in a stack sample</li>
 * <li>Self Sample Count : the number of times a frame was seen in a stack sample as "bottom frame"</li>
 * <li>Total Time : the amount of time, in nanoseconds, which was spent executing the method or a method called by this
 * method</li>
 * <li>Self Time : the amount of time, in nanoseconds, which was spent executing the method itself</li>
 * </ul>
 * <p>
 * For each frame, these quantities can be calculated. The Count quantities are pretty straightforward. The Time
 * quantities, for a particular frame, are approximated by taking the difference between the timestamp in the Stack
 * trace start events immediately preceding and following the stack trace sample the frame is seen in, and adding this
 * to the Total Time, and if the frame is the bottom frame, to the Self Time as well.
 * </p>
 * <h3>{@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode} Trees</h3>
 * <p>
 * For the basic {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} aggregation, stack frames
 * are grouped by method id + line number + bci, and by the identities of its ancestors as well as the id of the thread
 * the frame was executed on.
 * <p>
 * This is modeled by a {@link java.util.Map} of trees of
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s. There are two kinds :
 * <ul>
 * <li>the {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode} subclass which represents the
 * thread</li>
 * <li>a "pure" {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode} which represents a frame</li>
 * </ul>
 * <p>
 * The root {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode} of every tree is a
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode} representing the thread by which the
 * frames were executed. The quantities in its
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo} are the sum of those of all its
 * descendants.
 * <p>
 * The {@link java.util.Map} in the {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile}
 * associates the thread id with the corresponding root
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode}.
 * <p>
 * Every {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode} contains a {@link java.util.Map} of its
 * child {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}s, which represent the child stack
 * frames called at least once in a stack trace sample by the frame represented by the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}. The key in the child {@link java.util.Map}
 * is the {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo} for the frame represented by the
 * child {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode}. In case of a
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode}, the children are the frames
 * representing methods called directly by the thread.
 * <p>
 * Every {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode} in the tree is therefore identified by
 * the list of its ancestors and the contents of its
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo}.
 * <p>
 * For navigation and aggregation purposes, every
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode} also contains the reference to its parent
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode} (with the exception of
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode}s which have null as parent reference).
 * </p>
 * <h1>Summary</h1> As a form of synopsis, here's a diagram of the
 * {@link com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile} contents :
 *
 * <pre>
 * {@code
 * LeanProfile
 * |
 * ---MAP : Thread id -> ThreadInfo --- Thread id
 * |                                 |
 * |                                 -- Thread name
 *
 * |
 * ---MAP : Method id -> MethodInfo --- Method id
 * |                                 |
 * |                                 -- File name
 * |                                 |
 * |                                 -- Class name (FQCN)
 * |                                 |
 * |                                 -- Method name
 * |
 * |
 * --MAP : Thread id -> LeanThreadNode --- ThreadInfo
 *                                      |
 *                                      -- NumericInfo --- Total Sample Count (sum of descendant TSC)
 *                                      |               |
 *                                      |               -- Self Sample Count (sum of descendant SSC)
 *                                      |               |
 *                                      |               -- Total Time (sum of descendant TT)
 *                                      |               |
 *                                      |               -- Self Time (sum of descendant ST)
 *                                      |
 *                                      -- Parent -> null
 *                                      |
 *                                      -- MAP : FrameInfo -> LeanNode --- Parent -> [Parent Lean(Thread)Node]
 *                                                                      |
 *                                                                      -- NumericInfo --- Total Sample Count
 *                                                                      |               |
 *                                                                      |               -- Self Sample Count
 *                                                                      |               |
 *                                                                      |               -- Total Time
 *                                                                      |               |
 *                                                                      |               -- Self Time
 *                                                                      |
 *                                                                      -- MAP : FrameInfo -> LeanNode --- ...
 * }
 * </pre>
 */
package com.insightfullogic.honest_profiler.core.profiles.lean;