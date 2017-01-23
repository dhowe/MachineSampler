/* MyGUIActionEvent - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package mkv.MyGUI;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

public class MyGUIActionEvent
{
    private String _command;
    private MyGUIObject _source;
    Method actionEventMethod;
    
    public MyGUIActionEvent(MyGUIObject myguiobject, String string) {
	_command = string;
	_source = myguiobject;
    }
    
    public void sendEvent(Object object) {
	ActionEvent actionevent = new ActionEvent(_source, 1001, _command);
	try {
	    actionEventMethod
		= object.getClass().getMethod("actionPerformed",
					      (new Class[]
					       { actionevent.getClass() }));
	    actionEventMethod.invoke(object, new Object[] { actionevent });
	} catch (Exception exception) {
	    System.out.println
		("No method named actionPerformed was found in root. ");
	}
    }
    
    public void listMethods(Object object) {
	Class var_class = object.getClass();
	Method[] methods = var_class.getMethods();
	for (int i = 0; i < methods.length; i++) {
	    String string = methods[i].getName();
	    System.out.println(new StringBuilder().append("Name: ").append
				   (string).toString());
	    String string_0_ = methods[i].getReturnType().getName();
	    System.out.println(new StringBuilder().append
				   ("   Return Type: ").append
				   (string_0_).toString());
	    Class[] var_classes = methods[i].getParameterTypes();
	    System.out.print("   Parameter Types:");
	    for (int i_1_ = 0; i_1_ < var_classes.length; i_1_++) {
		String string_2_ = var_classes[i_1_].getName();
		System.out.print(new StringBuilder().append(" ").append
				     (string_2_).toString());
	    }
	    System.out.println();
	}
    }
}
