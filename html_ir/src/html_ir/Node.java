package html_ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import logicalcollections.LogicalMap;
import logicalcollections.LogicalList;
import logicalcollections.LogicalSet;

/**
 * Each instance of this class represents an element node or a text node in an HTML document.
 * 
 * @invar | getPeerGroupState() != null
 */
public class Node {

	/**
	 * Returns the state of this node as a map that maps property names to property
	 * values.
	 * 
	 * @post | result != null
	 * @post | result.equals(Map.of(
	 *       |     "parent", Optional.ofNullable(getParent()),
	 *       |     "children", getChildren()))
	 */
	public Map<String, Object> getState() {
		return getStatePrivate();
	}
	
	/**
	 * @pre | children != null
	 */
	private Map<String, Object> getStatePrivate() {
		return Map.of(
				"parent", Optional.ofNullable(parent),
				"children", List.copyOf(children));
	}
	
	/**
	 * Returns a map that maps each node related directly or indirectly to this node
	 * to its state, represented as a map from property names to property values.
	 * 
	 * @post | result != null
	 * @post
	 *    | result.equals(LogicalMap.<Node, Map<String, Object>>matching(map ->
	 *    |
	 *    |     // This node is in its peer group
	 *    |     map.containsKey(this) &&
	 *    |
	 *    |     map.keySet().allMatch(node ->
	 *    |
	 *    |         // Each peer node's children are not null and are in its peer group
	 *    |         node.getChildren().stream().allMatch(child -> child != null && map.containsKey(child)) &&
	 *    |
	 *    |         // No peer node's list of children contains the same node more than once 
	 *    |         LogicalList.distinct(node.getChildren()) &&
	 *    |
	 *    |         // Each peer node's parent, if any, is in its peer group
	 *    |         (node.getParent() == null || map.containsKey(node.getParent())) &&
	 *    |
	 *    |         map.containsEntry(node, node.getState())
	 *    |
	 *    |     ) &&
	 *    |
	 *    |     map.keySet().allMatch(node ->
	 *    |
	 *    |         // Each peer node N's children have N as their parent 
	 *    |         node.getChildren().stream().allMatch(child -> child.getParent() == node) &&
	 *    |
	 *    |         // Each peer node N's parent, if any, has N among its children 
	 *    |         (node.getParent() == null || node.getParent().getChildren().contains(node)) &&
	 *    |
	 *    |         // No node has itself among its ancestors
	 *    |         !LogicalSet.<Node>matching(ancestors ->
	 *    |             (node.getParent() == null || ancestors.contains(node.getParent())) &&
	 *    |             ancestors.allMatch(ancestor ->
	 *    |                 ancestor.getParent() == null || ancestors.contains(ancestor.getParent()))
	 *    |         ).contains(node) 
	 *    |     ))
	 *    | )
	 */
	public Map<Node, Map<String, Object>> getPeerGroupState() {
		return getPeerGroupStatePrivate();
	}
	
	private Map<Node, Map<String, Object>> getPeerGroupStatePrivate() {
		return LogicalMap.matching(map ->
			map.containsKey(this) &&
			map.keySet().allMatch(node ->
				node.children != null &&
				node.children.stream().allMatch(child -> child != null && map.containsKey(child)) &&
				LogicalList.distinct(node.children) &&
				(node.parent == null || map.containsKey(node.parent)) &&
				map.containsEntry(node, node.getStatePrivate())
			) &&
			map.keySet().allMatch(node ->
				node.children.stream().allMatch(child -> child.parent == node) &&
				(node.parent == null || node.parent.children.contains(node)) &&
				!LogicalSet.<Node>matching(ancestors ->                                              
				    (node.parent == null || ancestors.contains(node.parent)) &&            
				    ancestors.allMatch(ancestor ->                                                   
				        ancestor.parent == null || ancestors.contains(ancestor.parent))    
				).contains(node)                                                                     
			)
		);
	}
	
	/**
	 * @invar | getPeerGroupStatePrivate() != null
	 * @peerObject
	 */
	private Node parent;
	/**
	 * @representationObject
	 * @peerObjects
	 */
	private ArrayList<Node> children;
	
	public Node getParent() { return parent; }
	
	/**
	 * @post | result != null
	 */
	public List<Node> getChildren() {
		return List.copyOf(children);
	}
	
	/**
	 * Initializes this HTML document node either as an element node with a
	 * given tag, or as a text node with a given text content.
	 * 
	 * @post This node's list of children is empty.
	 *    | getChildren().isEmpty()
	 * @post This node is a root node.
	 *    | getParent() == null
	 */
	public Node() {
		this.children = new ArrayList<Node>();
	}
	
	/**
	 * Adds the given node to the end of this node's list of children.
	 * 
	 * @pre {@code child} is not null.
	 *    | child != null
	 * @pre The given node is a root node.
	 *    | child.getParent() == null
	 * @pre The given node is not an ancestor of this node.
	 *      Equivalently: the given node is not in this node's peer group.
	 *    | !this.getPeerGroupState().containsKey(child)
	 * @mutates | this, child
	 * @post This node's list of children equals its old list of children with the given node added to the end.
	 *    | getChildren().equals(LogicalList.plus(old(getChildren()), child))
	 * @post The given node's parent equals this node.
	 *    | child.getParent() == this
	 * @post This node's other properties have remained unchanged.
	 *    | LogicalMap.equalsExcept(getState(), old(getState()), "children")
	 * @post The given node's other properties have remained unchanged.
	 *    | LogicalMap.equalsExcept(child.getState(), old(child.getState()), "parent")
	 * @post The existing nodes in this node's peer group, except for this node, have remained unchanged.
	 *    | LogicalMap.extendsExcept(
	 *    |     getPeerGroupState(), old(getPeerGroupState()), this)
	 * @post The existing nodes in the given node's peer group, except for the given node, have remained unchanged.
	 *    | LogicalMap.extendsExcept(
	 *    |     child.getPeerGroupState(), old(child.getPeerGroupState()), child)
	 */
	public void addChild(Node child) {
		children.add(child);
		child.parent = this;
	}
	
	/**
	 * Removes the given node from this node's list of children.
	 * 
	 * @pre {@code child} is not null.
	 *    | child != null
	 * @pre The given node is a root node.
	 *    | getChildren().contains(child)
	 * @mutates this
	 * @post This node's list of children equals its old list of children with the given node removed.
	 *    | getChildren().equals(LogicalList.minus(old(getChildren()), child))
	 * @post The given node is a root node.
	 *    | child.getParent() == null
	 * @post This node's other properties have remained unchanged.
	 *    | LogicalMap.equalsExcept(getState(), old(getState()), "children")
	 * @post The given node's other properties have remained unchanged.
	 *    | LogicalMap.equalsExcept(child.getState(), old(child.getState()), "parent")
	 * @post The nodes in this node's peer group, except for this node, have remained unchanged.
	 *    | LogicalMap.extendsExcept(
	 *    |     old(getPeerGroupState()), getPeerGroupState(), this)
	 * @post The nodes in the given node's peer group, except for the given node, have remained unchanged.
	 *    | LogicalMap.extendsExcept(
	 *    |     old(child.getPeerGroupState()), child.getPeerGroupState(), child)
	 */
	public void removeChild(Node child) {
		children.remove(child);
		child.parent = null;
	}
	
	/**
	 * Removes this node from its parent's list of children.
	 * 
	 * @pre This node is not a root node.
	 *    | getParent() != null
	 * @mutates this
	 * @post This node's list of children equals its old list of children with the given node removed.
	 *    | old(getParent()).getChildren().equals(LogicalList.minus(old(getParent().getChildren()), this))
	 * @post The given node is a root node.
	 *    | getParent() == null
	 * @post This node's parent's other properties have remained unchanged.
	 *    | LogicalMap.equalsExcept(old(getParent()).getState(), old(getParent().getState()), "children")
	 * @post This node's other properties have remained unchanged.
	 *    | LogicalMap.equalsExcept(getState(), old(getState()), "parent")
	 * @post The nodes in this node's old parent's peer group, except for this node's old parent,
	 *       have remained unchanged.
	 *    | LogicalMap.extendsExcept(
	 *    |     old(getParent().getPeerGroupState()), old(getParent()).getPeerGroupState(), old(getParent()))
	 * @post The nodes in this node's peer group, except for this node, have remained unchanged.
	 *    | LogicalMap.extendsExcept(
	 *    |     old(getPeerGroupState()), getPeerGroupState(), this)
	 */
	public void remove() {
		parent.removeChild(this);
	}
	
	/**
	 * Returns a textual representation of this HTML document node and its descendants.
	 * 
	 * @post | result != null
	 */
	public String toString() {
		if (this instanceof TextNode) {
			return ((TextNode)this).getText();
		} else {
			String result = "<" + ((ElementNode)this).getTag()+ ">";
			for (Node child : children) {
			//for (int i = 0; i < children.size(); i++) {
				//Node child = children.get(i);
				result += child.toString();
			}
			result += "</" + ((ElementNode)this).getTag() + ">";
			return result;
		}
	}
	
	public int getTotalNbCharacters() {
		throw new AssertionError("Not yet implemented");
	}

}
