package graphwindow;

import filters.utils.Range;
import graphwindow.plot.GraphButton;
import graphwindow.plot.GraphSetting;
import graphwindow.plot.IPlot;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.print.attribute.standard.JobName;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextArea;

import main.utils.Logger;

public class GraphSettingsPanel extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -89899500613999721L;
	private JPanel panel = new JPanel();
	private PlotFrame parentFrame;
	private HashMap<Field, Object[]> annoted = new HashMap<Field, Object[]>(); 
	
	public GraphSettingsPanel(PlotFrame parent) {
		this.parentFrame = parent;
		setViewportView(panel);
		setAlignmentY(TOP_ALIGNMENT);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
	}

	@SuppressWarnings("unchecked")
	private Component[] parseField(final Field f, final IPlot plot) {
		GraphSetting gs = f.getAnnotation(GraphSetting.class);
		Component comp = null;
		JLabel label = new JLabel(gs.value());
		Object o = null;
		try {
			o = f.get(plot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(o == null) {
			throw new AnnotationFormatError("target field must have a value != null");
		}
		if(o instanceof Enum) {
			Object[] possibleValues = ((Enum<?>)o).getDeclaringClass().getEnumConstants();
			comp = new JComboBox(possibleValues);
			((JComboBox)comp).setSelectedItem(o);
			((JComboBox)comp).addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					Object value = arg0.getItem();
					try {
						f.set(plot, value);
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
				}
			});
		}
		else if(o instanceof Range ) {
			comp = new JPanel(new GridLayout());
			JSpinner lower = new JSpinner();
			JSpinner upper = new JSpinner();
			((JPanel)comp).add(lower);
			((JPanel)comp).add(upper);
			for(JSpinner sp : new JSpinner[]{ lower, upper }) {
				if(gs.list().length > 0) {
					Number[] list = new Number[gs.list().length]; 
					for(int i=0; i<gs.list().length; i++) list[i] = (Number)gs.list()[i];
					sp.setModel(new SpinnerListModel(list));
				} else if (gs.limits().length == 2) {
					sp.setModel(new SpinnerNumberModel(gs.limits()[0], gs.limits()[0], gs.limits()[1], 1));
				}
			}
			lower.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					Object value = ((JSpinner)e.getSource()).getValue();
					try {
						((Range<Number>) f.get(plot)).setLower((Number) value);
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
				}
			});
			upper.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					Object value = ((JSpinner)e.getSource()).getValue();
					try {
						((Range<Number>) f.get(plot)).setHigher((Number) value);
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
				}
			});
			lower.setValue(((Range<Number>)o).lower);
			upper.setValue(((Range<Number>)o).higher);
		}
		else if(o instanceof Number ) {
			comp = new JSpinner();
			((JSpinner)comp).setValue( o );
			if(gs.list().length > 0) {
				Number[] list = new Number[gs.list().length]; 
				for(int i=0; i<gs.list().length; i++) list[i] = (Number)gs.list()[i];
				((JSpinner)comp).setModel(new SpinnerListModel(list));
			} else if (gs.limits().length == 2) {
				if(o instanceof Integer) o = (Double)((Integer)o).doubleValue();
				if(o instanceof Float) o = (Double)((Float)o).doubleValue();
				((JSpinner)comp).setModel(new SpinnerNumberModel(
						(Number) o, gs.limits()[0], gs.limits()[1], 1));
			}
			((JSpinner)comp).addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					Number value = (Number) ((JSpinner)e.getSource()).getValue();
					try {
						if(f.get(plot) instanceof Integer) value = new Integer(value.intValue());
						if(f.get(plot) instanceof Float) value = new Float(value.floatValue());
						if(f.get(plot) instanceof Double) value = new Double(value.doubleValue());
						f.set(plot, value);
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
				}
			});
		} else if ( o instanceof Boolean ) {
			comp = new JCheckBox();
			((JCheckBox)comp).setSelected((Boolean)o);
			((JCheckBox)comp).addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					Object value = ((JCheckBox)e.getSource()).isSelected();
					try {
						f.set(plot, value);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
		} else if (o instanceof String) {
			if(gs.js()) {
				comp = new RSyntaxTextArea(gs.rows() > 1 ? gs.rows() : 10, 30);
				((RSyntaxTextArea)comp).setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
			} else if(gs.rows() > 1) {
				comp = new JTextArea();
				((JTextArea)comp).setRows(gs.rows());
			} else {
				comp = new JTextField();
			}
			((JTextComponent)comp).setText((String)o);
			((JTextComponent)comp).addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					Object value = ((JTextComponent)e.getSource()).getText();
					try {
						f.set(plot, value);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				@Override
				public void keyPressed(KeyEvent e) {}

				@Override
				public void keyReleased(KeyEvent e) {}
				
			});
		} else {
			Logger.log("type ??? => " + o.getClass().getName());
			return null;
		}
		return new Component[] { label, comp };
	}
	
	private void parseFields(IPlot plot) {
		annoted.clear(); 
		ArrayList<Field> annotedAL = new ArrayList<Field>();
		for(Field f : plot.getClass().getFields()) {
			if(f.isAnnotationPresent(GraphSetting.class)) {
				annotedAL.add(f);
			}
		}
		if(!annotedAL.isEmpty()) 
		{		
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.weighty = 0;
			c.anchor = GridBagConstraints.NORTH;
			
			for(int i=0; i<annotedAL.size()-1; i++) {
				Component[] comps = parseField(annotedAL.get(i), plot);
				try {
					annoted.put(annotedAL.get(i), new Object[] {annotedAL.get(i).get(plot), comps[1]});
				} catch (Exception e) {
					e.printStackTrace();
				}
				c.gridx = 0;
				c.weightx = 0.35;
				panel.add(comps[0], c);
				c.weightx = 0.65;
				c.gridx = 1;
				panel.add(comps[1], c);
				panel.add(new JSeparator());
			}
			Component[] comps = parseField(annotedAL.get(annotedAL.size()-1), plot);
			try {
				annoted.put(annotedAL.get(annotedAL.size()-1), new Object[] {annotedAL.get(annotedAL.size()-1).get(plot), comps[1]});
			} catch (Exception e) {
				e.printStackTrace();
			}
			c.weighty = 1;
			c.gridx = 0;
			c.weightx = 0.35;
			panel.add(comps[0], c);
			c.weightx = 0.65;
			c.gridx = 1;
			panel.add(comps[1], c);
		}
	}
	
	private void parseMethods(IPlot plot) {
		ArrayList<Method> annoted = new ArrayList<Method>(); 
		for(Method m : plot.getClass().getMethods()) {
			if(m.isAnnotationPresent(GraphButton.class)) {
				annoted.add(m);
			}
		}
		if(!annoted.isEmpty()) 
		{	
			for(Method m : annoted) 
				parseMethod(plot, m);
		}
	}
	
	private void parseMethod(final IPlot plot, final Method m) {		
		String btnLabel = m.getAnnotation(GraphButton.class).value();
		if(m.getParameterTypes().length > 0) {
			throw new AnnotationFormatError("Clickable methods can't have parameters (method: " + m.getName() +")");
		}
		JButton btn = new JButton(btnLabel);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.SOUTH;
		panel.add(btn, c);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					m.invoke(plot);
					for(java.util.Map.Entry<Field, Object[]> entry : annoted.entrySet()) {
						// Variable changed !
						Object value = ((Object[])entry.getValue())[0];
						Component comp = (Component) ((Object[])entry.getValue())[1];						
						if(!entry.getKey().get(plot).equals(value)) {
							updateComponentValue(comp, entry.getKey().get(plot));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private void updateComponentValue(Component comp, Object value) {
				if(comp instanceof JCheckBox) {
					((JCheckBox)comp).setSelected((Boolean) value);
				} else if( comp instanceof JTextComponent ) {
					((JTextComponent)comp).setText((String) value);
				} else if( comp instanceof JSpinner ) {
					((JSpinner)comp).setValue(((Number)value).doubleValue());
				} else if( comp instanceof JPanel ) {
					//TODO
					try {
						throw new Exception("Unimplemented !");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if( comp instanceof JComboBox ) {
					((JComboBox)comp).setSelectedItem(value);
				}
			}
		});
	}

	public void parseSettingsFrom(IPlot plot) {
		panel.removeAll();
		parseFields(plot);
		parseMethods(plot);
		addUpdateButton(plot);
		revalidate();
	}

	private void addUpdateButton(final IPlot plot) {
		JButton updateBtn = new JButton("Update graph");	
		updateBtn.setFont(new Font("Arial", Font.BOLD, 14));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.SOUTH;
		panel.add(updateBtn, c);
		updateBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getParentFrame().updateGraph();
			}
		});
	}
	
	private PlotFrame getParentFrame() {
		return parentFrame;
	}
}
