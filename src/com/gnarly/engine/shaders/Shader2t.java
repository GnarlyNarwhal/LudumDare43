package com.gnarly.engine.shaders;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform4f;

public class Shader2t extends Shader {

	private int alphaLoc;
	private int colorLoc;
	private int amountLoc;
	
	protected Shader2t() {
		super("res/shaders/s2t/vert.gls", "res/shaders/s2t/frag.gls");
		getUniforms();
	}

	public void setMixColor(float r, float g, float b, float a, float amount) {
		glUniform4f(colorLoc, r, g, b, a);
		glUniform1f(amountLoc, amount);
	}
	
	public void setAlpha(float a) {
		glUniform1f(alphaLoc, a);
	}
	
	@Override
	protected void getUniforms() {
		alphaLoc  = glGetUniformLocation(program, "alpha");
		colorLoc  = glGetUniformLocation(program, "iColor");
		amountLoc = glGetUniformLocation(program, "amount");
	}
}