package com.gmail.chibitopoochan.soqlui.initializer;

import com.gmail.chibitopoochan.soqlui.controller.Controller;

public interface Initializer<C extends Controller> {
	void setController(C controller);
	void initialize();
}
