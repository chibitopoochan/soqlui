/**
 *
 */
package com.gmail.chibitopoochan.soqlui.initializer.service;

import com.gmail.chibitopoochan.soqlui.controller.Controller;
import com.gmail.chibitopoochan.soqlui.initializer.Initializer;

import javafx.concurrent.WorkerStateEvent;

/**
 * @author mamet
 *
 */
public interface ServiceInitializer<C extends Controller> extends Initializer<C> {
	void failed(WorkerStateEvent e);
	void succeeded(WorkerStateEvent e);

}
