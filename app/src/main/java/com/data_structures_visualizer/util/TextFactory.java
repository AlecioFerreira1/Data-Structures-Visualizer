package com.data_structures_visualizer.util;

import com.data_structures_visualizer.models.color.Colors;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public final class TextFactory {
    private static final Font DEFAULT_FONT = Font.font("System", FontWeight.SEMI_BOLD, 16);

    private TextFactory() { }

    public static Text create(String value, Color color) {
        Text t = new Text(value);
        t.setFill(color);
        t.setFont(DEFAULT_FONT);
        return t;
    }

    public static Text styledText(String type, String content) {
        return switch (type) {
            case "next"       -> create(content, Colors.next); 
            case "prev"       -> create(content, Colors.prev); 
            case "node"       -> create(content, Colors.node);
            case "new_node"   -> create(content, Colors.newNode);
            case "found"      -> create(content, Color.web("#2E7D32")); 
            case "not_found"  -> create(content, Color.web("#C62828")); 
            case "inserted"   -> create(content, Color.web("#00FF00")); 
            case "remove"     -> create(content, Colors.remove);
            default           -> create(content, Color.web("#212121")); 
        };
    }
}