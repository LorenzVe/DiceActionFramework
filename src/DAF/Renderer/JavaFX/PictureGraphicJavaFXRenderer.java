package DAF.Renderer.JavaFX;

import java.io.File;
import java.util.HashMap;

import DAF.Math.Vector2;
import DAF.Renderer.Components.AGraphic;
import DAF.Renderer.Components.PictureGraphic;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PictureGraphicJavaFXRenderer extends JavaFXRenderer {
	private HashMap<String, Image> _cachedImages; //TODO: Replace with own FlyweightAssetManager-class.

	private HashMap<Integer, ImageView> _cachedNodes;

	public PictureGraphicJavaFXRenderer() {
		super();

		this._cachedImages = new HashMap<>();
		this._cachedNodes = new HashMap<>();
	}

	@Override
	public Node renderNode(AGraphic g) {
		PictureGraphic pictureGraphic;
		try {
			pictureGraphic = (PictureGraphic)g;
		} catch(ClassCastException e) {
			return null;
		}

		String imagePath = pictureGraphic.getPicturePath();
		
		Image image = this._cachedImages.get(imagePath);
		if(image == null) {
			String path = pictureGraphic.getPicturePath();
			if(path != null) {
				image = new Image(new File(pictureGraphic.getPicturePath()).toURI().toString(), true);
			}
		}

		int gameObjectId = g.getGameObject().getId();
		ImageView imageView = this._cachedNodes.get(gameObjectId);
		if(imageView == null) {
			imageView = new ImageView();
			imageView.setImage(image);
			this._cachedNodes.put(gameObjectId, imageView);
		}

		if(imageView.getImage() == null) {
			imageView.setImage(image);
		} else {
			String previousImageUrl = imageView.getImage().getUrl();
			String newImageUrl = image.getUrl();
			if(previousImageUrl.compareTo(newImageUrl) != 0) {
				imageView.setImage(image);
			}
		}

		Vector2 scale = g.getTransform().getScale();
		double rotation = g.getTransform().getRotation();
		imageView.setFitWidth(pictureGraphic.getWidth() * scale.x);
		imageView.setFitHeight(pictureGraphic.getHeight() * scale.y);
		imageView.setRotate(rotation);
		imageView.setX(pictureGraphic.getLeft());	
		imageView.setY(pictureGraphic.getTop());	

		return imageView;
	}

	@Override
	public void render(AGraphic g) {
		throw new NullPointerException("Need a Window to render in it, can't be alone called.");
	}
}
