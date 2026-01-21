package com.data_structures_visualizer.visual.context.list;

import com.data_structures_visualizer.models.entities.Stack;
import com.data_structures_visualizer.visual.ui.Arrow;
import com.data_structures_visualizer.visual.ui.VisualNode;

public final class DeleteExecutionContext {
    private final Stack<VisualNode> removedNodes = new Stack<VisualNode>(null);
    private final Stack<Arrow> removedArrows = new Stack<Arrow>(null);
    private final Stack<Arrow> removedPrevArrows = new Stack<Arrow>(null);

    public Stack<VisualNode> getRemovedNodes() {
        return removedNodes;
    }

    public Stack<Arrow> getRemovedArrows() {
        return removedArrows;
    }

    public Stack<Arrow> getRemovedPrevArrows() {
        return removedPrevArrows;
    }
}
