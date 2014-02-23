package com.insightfullogic.honest_profiler.javafx.profile;

import com.insightfullogic.honest_profiler.collector.ProfileNode;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;

import static com.insightfullogic.honest_profiler.javafx.Rendering.renderMethod;
import static com.insightfullogic.honest_profiler.javafx.Rendering.renderTimeShare;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.WHEAT;

public class TreeViewCell extends TreeCell<ProfileNode> {

    private static final int IMAGE_WIDTH = 50;
    private static final int IMAGE_HEIGHT = 15;

    private static final int TEXT_HORIZONTAL_INSET = 10;
    private static final int TEXT_VERTICAL_INSET = 10;

    @Override
    public void updateIndex(int i) {
        if (removedFromView(i)) {
            hide();
        }
        super.updateIndex(i);
    }

    private void hide() {
        setText(null);
        setGraphic(null);
    }

    private boolean removedFromView(int i) {
        return i == -1;
    }

    @Override
    protected void updateItem(ProfileNode profileNode, boolean empty) {
        super.updateItem(profileNode, empty);

        if (!empty && isVisible()) {
            setText(renderMethod(profileNode.getMethod()));
            Canvas canvas = new Canvas(IMAGE_WIDTH, IMAGE_HEIGHT);
            GraphicsContext context = canvas.getGraphicsContext2D();
            context.setFill(Color.BLACK);
            context.strokeRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

            double timeShare = profileNode.getTimeShare();
            double scaledShare = timeShare * IMAGE_WIDTH;
            double xStart = IMAGE_WIDTH - scaledShare;
            context.setFill(Color.GREEN);
            context.fillRect(xStart, 0, scaledShare, IMAGE_HEIGHT);

            Color color = timeShare > 0.5 ? WHEAT : RED;
            context.setFill(color);
            context.fillText(renderTimeShare(timeShare), TEXT_HORIZONTAL_INSET, TEXT_VERTICAL_INSET);

            setGraphic(canvas);
        }
    }

}
