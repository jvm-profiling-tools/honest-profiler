package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;

/**
 * Subclass of {@link DiffEntry} which allows to arrange the items into a tree.
 */
public class DiffNode extends DiffEntry
{
    // Instance Properties

    private final Map<String, DiffNode> children;

    // Instance Constructors

    /**
     * Constructor whach takes the two {@link Node}s to be compared as arguments.
     * <p>
     * @param baseNode the {@link Node} from the Base {@link Aggregation}
     * @param newNode the {@link Node} from the New {@link Aggregation}
     */
    public DiffNode(Node baseNode, Node newNode)
    {
        super(baseNode, newNode);

        children = new HashMap<>();
        addBaseChildren(baseNode);
        addNewChildren(newNode);
    }

    /**
     * Specialized internal constructor for {@link #copyWithFilter(Predicate)}.
     * <p>
     * @param node the {@link DiffNode} being copied
     * @param children the new, filtered children
     */
    private DiffNode(DiffNode node, List<DiffNode> children)
    {
        super(node.getBaseEntry(), node.getNewEntry());

        this.children = new HashMap<>();
        children.forEach(child -> this.children.put(child.getKey(), child));
    }

    // Instance Accessors

    /**
     * Sets the Base {@link Node}.
     * <p>
     * The return value is provided as a convenience for
     * {@link TreeDiff#set(com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree, com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree)}.
     * <p>
     * @param node the Base {@link Node}
     * @return this {@link DiffNode}
     */
    public DiffNode setBase(Node node)
    {
        super.setBase(node);

        addBaseChildren(node);
        return this;
    }

    /**
     * Sets the New {@link Node}.
     * <p>
     * The return value is provided as a convenience for
     * {@link TreeDiff#set(com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree, com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree)}.
     * <p>
     * @param node the New {@link Node}
     * @return this {@link DiffNode}
     */
    public DiffNode setNew(Node node)
    {
        super.setNew(node);

        addNewChildren(node);
        return this;
    }

    /**
     * Returns the children of this node.
     * <p>
     * @return a {@link List} containing the children of this node.
     */
    public List<DiffNode> getChildren()
    {
        return new ArrayList<>(children.values());
    }

    /**
     * Filter the descendants of this DiffNode recursively, creating copies of the "survivors". If this node has
     * survivor descendants or is accepted by the filter, the copy is returned, otherwise the method returns null.
     * <p>
     * @param filter the filter to be applied to this node and its descendants.
     * @return a new {@link DiffNode} containing the filtered information, or null
     */
    public DiffNode copyWithFilter(Predicate<DiffNode> filter)
    {
        List<DiffNode> newChildren = children.values().stream()
            .map(child -> child.copyWithFilter(filter)).filter(child -> child != null)
            .collect(toList());
        return newChildren.size() > 0 || filter.test(this) ? new DiffNode(this, newChildren) : null;
    }

    // Helper Methods

    /**
     * Create child DiffNodes or set the Base {@link Node} for existing ones, based on the children from the provided
     * {@link Node}.
     * <p>
     * @param node the Base {@link Node} whose children need to be incorporated into the children of this node
     */
    private void addBaseChildren(Node node)
    {
        if (node == null)
        {
            return;
        }
        node.getChildren().forEach(this::addBaseChild);
    }

    /**
     * Create child DiffNodes or set the New {@link Node} for existing ones, based on the children from the provided
     * {@link Node}.
     * <p>
     * @param node the Base {@link Node} whose children need to be incorporated into the children of this node
     */
    private void addNewChildren(Node node)
    {
        if (node == null)
        {
            return;
        }
        node.getChildren().forEach(this::addNewChild);
    }

    /**
     * Sets the Base {@link Node} of the correct child DiffNode to the provided {@link Node}, or create a new child
     * DiffNode if it doesn't exist yet, and set the Base {@link Node} in it to the provided {@link Node}.
     * <p>
     * @param child the {@link Node} to be added as Base {@link Node} of a child DiffNode
     */
    private void addBaseChild(Node child)
    {
        children.compute(
            child.getKey(),
            (k, v) -> v == null ? new DiffNode(child, null) : v.setBase(child));
    }

    /**
     * Sets the New {@link Node} of the correct child DiffNode to the provided {@link Node}, or create a new child
     * DiffNode if it doesn't exist yet, and set the New {@link Node} in it to the provided {@link Node}.
     * <p>
     * @param child the {@link Node} to be added as New {@link Node} of a child DiffNode
     */
    private void addNewChild(Node child)
    {
        children.compute(
            child.getKey(),
            (k, v) -> v == null ? new DiffNode(null, child) : v.setNew(child));
    }

    /**
     * Return a {@link Stream} of DiffNodes consisting of this Node and all its descendants.
     * <p>
     * @return a {@link Stream} of DiffNodes consisting of this Node and all its descendants
     */
    public Stream<DiffNode> flatten()
    {
        return concat(of(this), children.values().stream().flatMap(DiffNode::flatten));
    }
}
