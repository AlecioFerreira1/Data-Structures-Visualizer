package com.data_structures_visualizer.visual.context.queue;

import com.data_structures_visualizer.models.entities.Stack;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.VisualNode;

public final class EnqueueExecutionContext {
    private final Stack<VisualNode> createdNodes = new Stack<VisualNode>(null);
    private final Stack<Arrow> createdArrows = new Stack<Arrow>(null);

    public Stack<VisualNode> getCreatedNodes() {
        return createdNodes;
    }

    public Stack<Arrow> getCreatedArrows() {
        return createdArrows;
    }
}