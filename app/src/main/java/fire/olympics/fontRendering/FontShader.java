package fire.olympics.fontRendering;

import fire.olympics.App;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class FontShader extends ShaderProgram{
	
	private int location_colour;
	private int location_translation;
	
	public FontShader() {
		super(App.resource("shaders", "shader_for_text.vert").toString(),
				App.resource("shaders", "shader_for_text.frag").toString());
	}

	@Override
	protected void getAllUniformLocations() {
		location_colour = super.getUniformLocation("colour");
		location_translation = super.getUniformLocation("translation");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	public void loadColour(Vector3f colour){
		super.loadVector(location_colour, colour);
	}
	
	public void loadTranslation(Vector2f translation){
		super.load2DVector(location_translation, translation);
	}


}
