package com.myassistant;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class WebSite extends Application {
    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        

        engine.load( getClass().getResource("/com/myassistant/index.html").toExternalForm() );
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
        if (newState == Worker.State.SUCCEEDED) {
            JSObject window = (JSObject) engine.executeScript("window");
            if (window != null) {
                window.setMember("javaConsole", new Object() {
                    public void log(String msg) {
                        System.out.println(msg);
                    }
                });

                engine.executeScript("""
                    console.log = function(msg) { javaConsole.log(msg); };
                """);
            } else {
                System.err.println("window object is null after load");
            }
        }
});

        engine.executeScript("console.log = function(msg) { javaConsole.log(msg); };");

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("bridge", new StaticBridge());
            }
        });

        Scene scene = new Scene(webView, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static class StaticBridge {
        public String loadDocs() {
            ObjectMapper mapper = new ObjectMapper();
            try {
                System.out.println("MAPPING");
                ArrayList<Document> temp = WebSite.loadDocs();
                System.out.println("Values loaded: " + temp.size());
                System.out.println("Result: " + temp.toString());
                return mapper.writeValueAsString(temp.toString());
            } catch (JsonProcessingException e) {
                System.out.println(e);
                return null;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static ArrayList<Document> loadDocs() {
		try {
			ArrayList<Document> docs = new ArrayList<>();
			long startTime = System.currentTimeMillis();
			
				List<Path> files = FileLoader.getAllTextFiles("/Users/johnmiller/Personal-study-Ai/java_core/data/");
            System.out.println(files.size());
				for (Path file : files) {
					String content = FileLoader.readFile(file);
			String fileName = file.toString();
			int start = fileName.lastIndexOf("/") + 1;
			int end = fileName.lastIndexOf(".");
			String title = fileName.substring(start, end);
			Document doc = new Document(title, content);
			docs.add(doc);
				}
			System.out.println("docs created " + (System.currentTimeMillis()-startTime) + " ms");
			return docs;
		} catch (IOException e) {
            System.out.println("EXCEPTION :(");
            e.printStackTrace();
			return null;
        } 
	}
}
