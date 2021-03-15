package com.mygdx.runnjump.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.runnjump.Runnjump;

import java.awt.Dimension;
import java.awt.Toolkit;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Run N Jump";
		config.forceExit = false;
		config.height = 600;
		config.width = 900;
		new LwjglApplication(new Runnjump(), config);

	}
}
