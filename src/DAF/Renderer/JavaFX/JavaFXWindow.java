package DAF.Renderer.JavaFX;

import java.time.Clock;
import java.util.LinkedList;
import java.util.Observable;

import DAF.Renderer.RenderManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JavaFXWindow extends Application
{
    public static final double MAX_FPS = 33.3;

	private Pane _nodesBuffer;

	private boolean _notifyChanged;

    private Scene _scene;

    private Stage _window = null;

    private boolean _didRender = false;

    private Clock _clock;

    private LinkedList<Node> _buffer;

	public JavaFXWindow() {
		JavaFXWindow._instance = this;

		this._nodesBuffer = new Pane();

        this._buffer = new LinkedList<>();
        
        this._clock = Clock.systemDefaultZone();
	}

	private static JavaFXWindow _instance;
	public static JavaFXWindow getInstance() { return _instance; }

	public synchronized void notifyChanged() {
        this._notifyChanged = true;
        this._didRender = false;
	}

    public synchronized void notifyRendered() {
        this._didRender = true;
        this._notifyChanged = false;
    }

    public synchronized boolean isRendered() { 
        if(this._window == null) {
            return false;
        }
        return this._didRender;
    }

	public synchronized boolean isChanged() {
        if(this._window == null) {
            return false;
        }
		return this._notifyChanged;
	}

	public synchronized void clearNodes() {
        this._buffer.clear();
	}

	public synchronized void renderNode(Node n) {
        this._buffer.add(n);
	}

    public Scene getScene() { return this._scene; }

    @Override
    public void init() {
        _scene = new Scene(this._nodesBuffer, 1024, 768);
    }


	@Override
	public void start(Stage stage) {
        _window = stage;

        notifyRendered();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(MAX_FPS), e -> refresh()));
        timeline.setCycleCount(Animation.INDEFINITE); // loop forever
        timeline.play();

        _window.setScene(_scene);

        _window.show();
	}

    /**
     * Our frame limiter for rendering the elements.
     * The limit is based on MAX_FPS.
     * Here we calculate the nano seconds from the last frame.
     */
    public synchronized void refresh() {
        if(!isChanged()) {
            return;
        }

        ObservableList<Node> list = _nodesBuffer.getChildren();
        for(Node n : this._buffer) {
            if(!list.contains(n)) {
                list.add(n);
            }
        }
        LinkedList<Node> toDelete = new LinkedList<>();
        for(Node n : list) {
            if(!this._buffer.contains(n)) {
                toDelete.add(n);
            }
        }
        list.removeAll(toDelete);
        
        RenderManager.getInstance().setRenderedTime(_clock.instant().getNano());

        notifyRendered();
    }
}