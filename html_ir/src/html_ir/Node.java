package html_ir;

public class Node {
	
	private String tag;
	private String text;
	private Node firstChild;
	private Node lastChild;
	private Node nextSibling;

	public Node(String tag, String text) {
		this.tag = tag;
		this.text = text;
	}
	
	public void addChild(Node child) {
		if (firstChild != null) {
			lastChild.nextSibling = child;
			lastChild = child;
		} else {
			firstChild = child;
			lastChild = child;
		}
	}
	
	public void removeChild(Node child) {
		if (firstChild == child) {
			firstChild = firstChild.nextSibling;
			if (firstChild == null)
				lastChild = null;
		} else {
			Node currentChild = firstChild;
			while (currentChild.nextSibling != child)
				currentChild = currentChild.nextSibling;
			if (currentChild.nextSibling.nextSibling == null)
				lastChild = currentChild;
			currentChild.nextSibling = currentChild.nextSibling.nextSibling;
		}
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
