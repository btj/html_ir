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
	 *      | parent == null || Arrays.stream(parent.children).anyMatch(c -> c == this)
	 * @invar For each child of this node, the child's parent equals this node.
	 *      | Arrays.stream(children).allMatch(c -> c != null && c.parent == this)
	 */
	private String tag;
	private String text;
	private Node parent;
	private Node[] children;
	
	public String getTag() { return tag; }
	public String getText() { return text; }
	
	public Node getParent() { return parent; }
	
	public Node[] getChildren() {
		return Arrays.copyOf(children, children.length);
	}
	
	public Node(String tag, String text) {
		this.tag = tag;
		this.text = text;
		this.children = new Node[0];
	}
	
	public void addChild(Node child) {
		Node[] newChildren = new Node[children.length + 1];
		System.arraycopy(children, 0, newChildren, 0, children.length);
		newChildren[children.length] = child;
		children = newChildren;
		child.parent = this;
	}
	
	public void removeChild(Node child) {
		Node[] newChildren = new Node[children.length - 1];
		int index = 0;
		while (children[index] != child)
			index++;
		System.arraycopy(children, 0, newChildren, 0, index);
		System.arraycopy(children, index + 1, newChildren, index, children.length - index - 1);
		children = newChildren;
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
		//for (int i = 0; i < children.length; i++) {
			//Node child = children[i];
			result += child.toString();
		}
		result += "</" + tag + ">";
		return result;
	}

}
