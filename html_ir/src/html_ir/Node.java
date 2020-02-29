package html_ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @invar | (getTag() == null) != (getText() == null)
 * @invar | getParent() == null || Arrays.stream(getParent().getChildren()).anyMatch(c -> c == this)
 * @invar | Arrays.stream(getChildren()).allMatch(c -> c != null && c.getParent() == this)
 */
public class Node {
	
	/**
	 * @invar Either {@code tag} or {@code text} must be non-null
	 *      | tag != null || text != null
	 * @invar Either {@code tag} or {@code text} must be null
	 *      | tag == null || text == null
	 * @invar
	 *      | (tag == null) == (text != null)
	 * @invar
	 *      | children != null
	 * @invar If this node has a parent, this node is among its parent's children
	 *      | parent == null || parent.children.stream().anyMatch(c -> c == this)
	 * @invar For each child of this node, the child's parent equals this node.
	 *      | children.stream().allMatch(c -> c != null && c.parent == this)
	 */
	private String tag;
	private String text;
	private Node parent;
	private ArrayList<Node> children;
	
	public String getTag() { return tag; }
	public String getText() { return text; }
	
	public Node getParent() { return parent; }
	
	public Node[] getChildren() {
		return children.toArray(new Node[0]);
	}
	
	public Node(String tag, String text) {
		this.tag = tag;
		this.text = text;
		this.children = new ArrayList<Node>();
	}
	
	public void addChild(Node child) {
		children.add(child);
		child.parent = this;
	}
	
	public void removeChild(Node child) {
		children.remove(child);
		child.parent = null;
	}
	
	public void remove() {
		if (parent != null)
			parent.removeChild(this);
	}
	
	public String toString() {
		if (text != null)
			return text;
		String result = "<" + tag + ">";
		for (Node child : children) {
		//for (int i = 0; i < children.size(); i++) {
			//Node child = children.get(i);
			result += child.toString();
		}
		result += "</" + tag + ">";
		return result;
	}

}
