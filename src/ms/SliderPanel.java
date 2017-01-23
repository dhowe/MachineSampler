package ms;
import java.awt.Dimension;
import java.lang.reflect.Method;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderPanel extends JPanel implements ChangeListener
{
  private static final String SIZE_VARIANT = "JComponent.sizeVariant";
  
  private Object parent;
  private String[] labels;
  private JSlider[] sliders;

  public SliderPanel(Object parent, String[] labels) 
  {
    super();
    
    this.parent = parent;
    
    this.setPreferredSize(new Dimension(220, labels.length*75));
    
    this.labels = labels;
    sliders = new JSlider[labels.length];
    
    for (int i = 0; i < labels.length; i++)
    {
      JSlider js = new JSlider(0,100,50);
      js.putClientProperty( SIZE_VARIANT, "small");
      js.setPreferredSize(new Dimension(200,65));
      js.setBorder(BorderFactory.createTitledBorder(labels[i]));
      js.setMinorTickSpacing(25);
      js.setPaintTicks(true);
      js.setPaintLabels(true);
      js.setVisible(true);
      js.addChangeListener(this);
      
      Hashtable lTable = new Hashtable();
      JLabel label = new JLabel("0.0");
      label.putClientProperty( SIZE_VARIANT, "mini");
      lTable.put(0, label );
      label = new JLabel("0.5");
      label.putClientProperty( SIZE_VARIANT, "mini");
      lTable.put(new Integer(50), label );
      label = new JLabel("1.0");
      label.putClientProperty( SIZE_VARIANT, "mini");
      lTable.put(new Integer(100), label);
      js.setLabelTable(lTable);

      add(sliders[i] = js);
    }
    setVisible(true);
  }

  public void stateChanged(ChangeEvent e)
  {
    JSlider js = (JSlider)e.getSource();
    for (int i = 0; i < sliders.length; i++)
    {
      if (js == sliders[i]) {
        try {
          invokeSetter(parent, getSetterName(labels[i]), js.getValue()/100f);
        }
        catch (Exception e1) {
          e1.printStackTrace();
        }
      }
    }
  }
  
  private String getSetterName(String varName)
  {
    return "set"+varName.substring(0, 1).toUpperCase()+varName.substring(1);
  }


  public static Object invokeSetter(Object callee, String methodName, float val) throws Exception//throws Exception
  { 
System.out.println("invokeSetter("+callee.getClass()+", "+methodName+", "+ val+")");      
      Method m;
      try
      {
        
        m = callee.getClass().getMethod(methodName, new Class[] { Float.class });
      }
      catch (Exception e)
      {
        System.out.println(getMethods(callee));
        throw new NoSuchMethodError(methodName+"(Float f);");
      }
      //if (m == null) throw new NoSuchMethodError(); 
      Object[] params = new Object[]{methodName, new Object[] { val } };
      try
      {
        return m.invoke(callee, params);
      }
      catch (Exception e)
      {
        System.out.println(getMethods(callee));        
        throw new Exception(e);
      }  
  }

  private static String getMethods(Object callee)
  {
    String s = "Found:\n";
    Method[] all = callee.getClass().getMethods();
    for (int i = 0; i < all.length; i++)
    {
      s += "  "+all[i].getName()+"(";
      
      Class[] c = all[i].getParameterTypes();
      for (int j = 0; j < c.length; j++)
      {
        s += c.getClass().getName();
        if (j < c.length-1)
          s += ", ";
      }
      s += ");";
    }
    return s;
  }
  
  public JSlider[] getSliders()
  {
    return sliders;
  }

  public static void main(String[] args)
  {
    JFrame jf = new JFrame();
    jf.add(new SliderPanel(null, new String[]{ "width", "height", "prob", "speed" }));
    jf.pack();
    jf.setLocation(600,400);
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jf.setVisible(true);
  }

}// end
