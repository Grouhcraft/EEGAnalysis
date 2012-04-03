package graphwindow.plot;

import graphwindow.graphlayouts.IGraphLayout;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface graphtype {
	String name();
	Class<? extends IGraphLayout> layout();
}