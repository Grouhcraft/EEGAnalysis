package graphwindow.plot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import filters.utils.Range;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface GraphSetting {
	/**
	 * Limit the value to a specific range.
	 * Suitable only for number variables
	 * Have to contain 2 number (upper and lower bound) 
	 */
	double[] limits() default {};
	
	/**
	 * Limit the value to a specific list
	 * Suitable only for number variables  
	 */
	double[] list() default {};
	
	/**
	 * Mandatory, label.
	 */
	String value() default "";
	
	/**
	 * Only suitale for Strings variables
	 * if <i>rows</i> > 1, then the component will be 
	 * a text area containing <i>rows</i> rows.   
	 * Else, it will be a simple JTextField
	 */
	int rows() default 1;

	boolean js() default false; 
}
