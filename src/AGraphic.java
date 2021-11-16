@Serializable
public abstract class AGraphic extends AbstractComponent {

	@JsonElement
	Integer RenderingLayer = 1;
	
	@Override
	public void start() {
		RenderManager.getInstance().add(this);
	}

	public Integer getRenderingLayer() {
		return RenderingLayer;
	}

	public void setRenderingLayer(Integer level) {
		this.RenderingLayer = level;		
	}

	@Init
	private void initValues() {
		this.RenderingLayer = 0;
	}
}

