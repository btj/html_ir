package html_ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Node {
	
	private String tag;
	private String text;
	private Node parent;
	private Node firstChild;
	private Node lastChild;
	private Node nextSibling;
	private Node previousSibling;
	
	public List<Node> getChildren() {
		ArrayList<Node> children = new ArrayList<>();
		for (Node child = firstChild; child != null; child = child.nextSibling)
			children.add(child);
		return children;
	}
	
	public Node(String tag, String text) {
		this.tag = tag;
		this.text = text;
	}
	
	public void addChild(Node child) {
		if (firstChild != null) {
			lastChild.nextSibling = child;
			child.previousSibling = lastChild;
			lastChild = child;
		} else {
			firstChild = child;
			lastChild = child;
		}
		child.parent = this;
	}
	
	public void removeChild(Node child) {
		if (child.previousSibling != null)
			child.previousSibling.nextSibling = child.nextSibling;
		else
			firstChild = child.nextSibling;
		if (child.nextSibling != null)
			child.nextSibling.previousSibling = child.previousSibling;
		else
			lastChild = child.previousSibling;
		child.nextSibling = null;
		child.previousSibling = null;
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
		Node child = firstChild;
		while (child != null) {
			result += child.toString();
			child = child.nextSibling;
		}
		result += "</" + tag + ">";
		return result;
	}

}
