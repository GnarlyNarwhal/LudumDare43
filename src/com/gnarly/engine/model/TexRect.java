package com.gnarly.engine.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.shaders.Shader;
import com.gnarly.engine.shaders.Shader2t;
import com.gnarly.engine.texture.Texture;

public class TexRect extends Rect {

	private Texture texture;
	private Shader2t shader = Shader.SHADER2T;
	protected float direction = 1;
	private float alpha = 1;
	private float r;
	private float g;
	private float b;
	private float a;
	private float amount = 1;
	
	public TexRect(Camera camera, String path, float x, float y, float z, float width, float height, float rotation, boolean gui) {
		super(camera, x, y, z, width, height, rotation, gui);
		texture = new Texture(path);
	}
	
	public TexRect(Camera camera, Texture texture, float x, float y, float z, float width, float height, float rotation, boolean gui) {
		super(camera, x, y, z, width, height, rotation, gui);
		this.texture = texture;
	}
	
	public void render() {
		texture.bind();
		shader.enable();
		Matrix4f cmat = gui ? camera.getProjection() : camera.getMatrix();
		shader.setMVP(cmat.translate(position.add(width * scale / 2, height * scale / 2, 0, new Vector3f())).rotateZ(rotation).scale(width * scale * direction, height * scale, 1).translate(-0.5f, -0.5f, 0));
		shader.setAlpha(alpha);
		shader.setMixColor(r, g, b, a, amount);
		vao.render();
		shader.disable();
		texture.unbind();
	}
	
	public void setCenter(float x, float y) {
		position.x = x - width / 2;
		position.y = y - height / 2;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public void setMix(float r, float g, float b, float a, float amount) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.amount = 1 - amount;
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public float getAlpha() {
		return alpha;
	}
}
