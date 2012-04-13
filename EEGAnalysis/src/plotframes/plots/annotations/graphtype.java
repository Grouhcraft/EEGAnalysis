package plotframes.plots.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import plotframes.graphlayouts.IGraphLayout;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface graphtype {
	String name();
	Class<? extends IGraphLayout> layout();
}