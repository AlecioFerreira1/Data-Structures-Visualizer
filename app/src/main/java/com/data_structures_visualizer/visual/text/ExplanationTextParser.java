package com.data_structures_visualizer.visual.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.data_structures_visualizer.models.text.ExplanationText;
import com.data_structures_visualizer.util.TextFactory;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public final class ExplanationTextParser {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\{(\\w+):(.*?)\\}");

    public static Text[] parse(List<ExplanationText> explanations) {

        if(explanations == null || explanations.isEmpty()) {
            return new Text[0];
        }

        List<Text> result = new ArrayList<Text>();

        for(ExplanationText explanation : explanations)  {
            parseSingle(explanation.getText(), result);

            Text newline = new Text("\n");
            newline.setWrappingWidth(0);
            result.add(newline);
        }

        return result.toArray(Text[]::new);
    }

    private static void parseSingle(String raw, List<Text> out) {
        Matcher matcher = TOKEN_PATTERN.matcher(raw);
        int lastIndex = 0;

        while(matcher.find()) {
            if(matcher.start() > lastIndex){
                out.add(TextFactory.create(
                    raw.substring(lastIndex, matcher.start()),
                    Color.BLACK
                ));
            }

            String type = matcher.group(1);
            String content = matcher.group(2);

            out.add(TextFactory.styledText(type, content));

            lastIndex = matcher.end();
        }

        if(lastIndex < raw.length()){
            out.add(TextFactory.create(
                raw.substring(lastIndex),
                Color.BLACK
            ));
        }
    }
}