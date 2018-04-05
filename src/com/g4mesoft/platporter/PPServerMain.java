package com.g4mesoft.platporter;

public final class PPServerMain extends PlatPorter {

	private PPServerMain() {
		super(false);
	}
	
	public static void main(String[] args) {
		new PPServerMain().start();
	}
}
