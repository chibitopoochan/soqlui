package com.gmail.chibitopoochan.soqlui.controller.initialize;

import com.gmail.chibitopoochan.soqlui.controller.Controller;

public interface Initializer<C extends Controller> {
	void setController(C controller);
	void initialize();
}
