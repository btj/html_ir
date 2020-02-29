package html_ir;

import static org.junit.jupiter.api.Assertions.*;

import java.net.http.HttpResponse.BodyHandler;

import org.junit.jupiter.api.Test;

class HtmlTest {

	@Test
	void test() {
		Node html = new Node("html", null);
		Node head = new Node("head", null);
		html.addChild(head);
		Node title = new Node("title", null);
		head.addChild(title);
		Node titleText = new Node(null, "JLearner");
		title.addChild(titleText);
		Node script = new Node("script", null);
		head.addChild(script);
		//head.removeChild(script);
		script.remove();
		head.addChild(script);
		Node scriptText = new Node(null, "alert('Hello world!')");
		script.addChild(scriptText);
		Node nonsense = new Node("nonsense", null);
		head.addChild(nonsense);
		//head.removeChild(nonsense);
		nonsense.remove();
		
		assertEquals(
				"<html>" + 
				"<head>" + 
				"<title>JLearner</title>" + 
				"<script>alert('Hello world!')</script>" +
				"</head>" + 
				"</html>",
				html.toString());
	}

}
