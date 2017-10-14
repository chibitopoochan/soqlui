/**
 *
 */
package com.gmail.chibitopoochan.soqlui.controller.initialize.service;

import com.gmail.chibitopoochan.soqlui.controller.Controller;
import com.gmail.chibitopoochan.soqlui.controller.initialize.Initializer;

import javafx.concurrent.WorkerStateEvent;

/**
 * @author mamet
 *
 */
public interface ServiceInitializer<C extends Controller> extends Initializer<C> {
	void failed(WorkerStateEvent e);
	void succeeded(WorkerStateEvent e);

}
