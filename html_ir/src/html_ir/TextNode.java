package html_ir;

public class TextNode extends Node {
	
	/** @invar | text != null */
	private String text;
	
	public String getText() {
		return text;
	}
	
	/**
	 * @pre | text != null
	 * @param text
	 */
	public TextNode(String text) {
		this.text = text;
	}

}
