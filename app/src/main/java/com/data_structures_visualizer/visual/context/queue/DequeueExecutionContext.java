package com.data_structures_visualizer.visual.context.queue;

import com.data_structures_visualizer.models.entities.Stack;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.VisualNode;

public class DequeueExecutionContext {
    private final Stack<VisualNode> removedNodes = new Stack<VisualNode>(null);
    private final Stack<Arrow> removedArrows = new Stack<Arrow>(null);

    public Stack<VisualNode> getRemovedNodes() {
        return removedNodes;
    }

    public Stack<Arrow> getRemovedArrows() {
        return removedArrows;
    }
}