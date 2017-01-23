package ms;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import pitaru.sonia_v2_9.Sonia;
import processing.core.PApplet;

public class SamplerFrame extends JFrame implements ActionListener, ChangeListener
{
  static int MENU_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));      
  
  static File sampleDir, projDir;
  
  static protected JDialog aboutBox, prefsBox;

  private static JMenu fileMenu, helpMenu, globalMenu;
  private static JMenuItem openMI, optionsMI, quitMI,/* saveMI,*/ aboutMI, docsMI, supportMI;
  private static JMenuItem restartMI, /*pauseMI, */muteMI, soloMI;
  
  static private PApplet p;
  static float[] BG = new float[] {0,0,40};
  static boolean exiting;
  static String defaultDir;

  public SamplerFrame(PApplet sketch, int w, int h)
  {
    MachineSampler.USE_OSC = false;
    MachineSampler.DISABLE_USER_INPUT = false;
    MachineSampler.NUM_PER_COLUMN = MachineSampler.USE_OSC ? 12: 15;
    
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    
    setTitle("MachineSampler");
    
    setIconImage(new ImageIcon(getClass()
      .getResource("icon.png")).getImage());
    
    p = sketch;
    
    addMenus();
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        quit();
    }});
    
    JPanel panel = new javax.swing.JPanel();
    Color purple = new Color(40);
    BG = new float[] { purple.getRed(), purple.getGreen(), purple.getBlue() };
    BG = new float[] {0,0,40};
    panel.setBackground(purple);
    panel.setBounds(20, 0, w, h + 40);
    panel.add(sketch);
    this.add(panel);

    aboutBox = createAbout();
 
    registerForMacOSXEvents();

    sketch.init(); // start the sketch
    
    doLayout(w, h);
    
    sketch.requestFocus();
  }

  // Generic registration with the Mac OS X application menu
  public void registerForMacOSXEvents()
  {
    if (MAC_OS_X) {
      try {
        // Generate and register the OSXAdapter, passing the methods we wish to
        // use as delegates for various com.apple.awt.ApplicationListener methods
        OsxMSAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[]) null));
        OsxMSAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[]) null));
        OsxMSAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[]) null));
        //OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", new Class[] { String.class }));
      }
      catch (Exception e)
      {
        System.err.println("Error while loading OSXAdapter code...");
        e.printStackTrace();
      }
    }
  }
  
  private JDialog createAbout()
  {
    JDialog ab = new JDialog(this, "About");
    ab.getContentPane().setLayout(new BorderLayout());
    ab.getContentPane().add(new JLabel("MachineSampler["+MachineSampler.VERSION+"]", JLabel.CENTER));
    ab.getContentPane().add(new JLabel("\u00A92010 Daniel C. Howe", JLabel.CENTER), BorderLayout.SOUTH);
    ab.setSize(200, 200);
    ab.setResizable(false);
    return ab;
  }

  private void doLayout(int w, int h)
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int yoff= (screenSize.height - h)/2; // the window Dimensions
    setBounds((screenSize.width - w)/2, yoff, w, h + 40); 
    setUndecorated(yoff < 20);
    setVisible(true);
  }

  public void addMenus()
  {
    //System.out.println("SamplerFrame.addMenus(MAC_OS_X="+MAC_OS_X+")");
    
    JMenu fileMenu = new JMenu("File");
    
    JMenuBar mainMenuBar = new JMenuBar();
    
    // FILE_MENU ----------------------
    mainMenuBar.add(fileMenu = new JMenu("File"));
    fileMenu.setMnemonic('F');

    fileMenu.add(openMI = new JMenuItem("Open..."));
    openMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, MENU_MASK));
    openMI.addActionListener(this);
    
    fileMenu.addSeparator();
    
    mainMenuBar.add(globalMenu= new JMenu("Global"));
    globalMenu.setMnemonic('G');
    
    globalMenu.add(restartMI = new JMenuItem("Restart"));
    restartMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_MASK));
    restartMI.addActionListener(this);
    
 /*   actionMenu.add(pauseMI = new JMenuItem("Pause"));
    //pauseMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, MENU_MASK));
    pauseMI.addActionListener(this);*/
    
    globalMenu.add(muteMI = new JMenuItem("Mute"));
    muteMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, MENU_MASK));
    muteMI.addActionListener(this);
    
    globalMenu.add(soloMI = new JMenuItem("Solo"));
    //muteMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_MASK));
    soloMI.addActionListener(this);
    
    //mainMenuBar.add(actionMenu= new JMenu("Sample"));
    //actionMenu.setMnemonic('S');
      
    // Quit/prefs menu items are provided on Mac OS X; only add your own on other platforms
    if (!MAC_OS_X)
    {
      fileMenu.addSeparator();
      fileMenu.add(optionsMI = new JMenuItem("Options"));
      optionsMI.addActionListener(this);
      
      fileMenu.addSeparator();
      
      fileMenu.add(quitMI = new JMenuItem("Quit"));
      quitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, MENU_MASK));
      quitMI.addActionListener(this);
    }
    
    // help & about menus ---------------------
    
    mainMenuBar.add(helpMenu = new JMenu("Help"));
    helpMenu.add(docsMI = new JMenuItem("Online Documentation"));
    helpMenu.addSeparator();
    helpMenu.add(supportMI = new JMenuItem("Technical Support"));
    
    // About menu item is provided on Mac OS X; only add your own on other platforms
    if (!MAC_OS_X)
    {
      helpMenu.addSeparator();
      helpMenu.add(aboutMI = new JMenuItem("About SamplerFi"));
      aboutMI.addActionListener(this);
    }

    setJMenuBar(mainMenuBar);
  }

  private void loadSamples()
  {
    String loadDir = System.getProperty("user.dir");
    if (defaultDir != null) loadDir = defaultDir;
    final JFileChooser fc = new JFileChooser(loadDir);
    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    fc.setFileFilter(new FileFilter()
    {
      public String getDescription() {
        return "";
      }
      public boolean accept(File f)
      {
        if (f.isDirectory()) return true;
        String name = f.getName();
        return name.toString().endsWith(".wav")
        || name.toString().endsWith(".aiff")
        || name.toString().endsWith(".aif");
    }});
    
    new Thread() 
    {
      public void run() {
        fc.showOpenDialog(p);
        File f = fc.getSelectedFile();
        if (f == null) return;
        if (f.isDirectory()) {
          ((MachineSampler)p).replaceSamplesFrom(f.getPath());
          defaultDir = f.getPath();
        }
        else {
          ((MachineSampler)p).replaceSampleFrom(f);  
        }
      }
    }.start();
  }

  public void about()
  {
    aboutBox.setLocation((int) this.getLocation().getX() + 22, (int) this.getLocation().getY() + 22);
    aboutBox.setVisible(true);
  }

  public void preferences()
  {
    prefsBox.setLocation(getWidth()/2-150,getHeight()/2-150);
    prefsBox.setVisible(true);
  }

  public void saveProject() 
  {
/*    final JFileChooser fc = new JFileChooser(PROJ_DIR);
    new Thread() {
      public void run() {
        fc.showSaveDialog(p);
        File f = fc.getSelectedFile();
        if (f != null)  {
          projDir = f;
          System.out.println("Selected: "+projDir);
          ((SamplerFi)p).saveToXml(projDir);
        }
      }
    }.start();*/
  }
  
  public boolean quit()
  {
    int option = JOptionPane.YES_OPTION;
    if (MachineSampler.CONFIRM_ON_QUIT) { 
      option = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION);
    }
    if (option == JOptionPane.YES_OPTION) {
      
      exiting = true;
      
      System.out.print("[INFO] Exiting...");
      for (int i = 0; isSaving(); i++) {
        try {
          Thread.sleep(100);
        }
        catch (InterruptedException e1) {
          System.err.println("[WARN] "+e1.getMessage());
        }
        System.out.print(".");
        if (i%50==49) 
          System.out.println();
      }
      System.out.println("OK");
      try {
        Sonia.stop();
      }
      catch (Exception e) {}
      System.exit(1);
    }
    
    return (option == JOptionPane.YES_OPTION);
  }

  private boolean isSaving()
  {
    return false;
  }

  public void stateChanged(ChangeEvent e)
  {
    Object o = e.getSource();
    if (o instanceof JSpinner) {
      JSpinner js = (JSpinner)o;
      String name = js.getName();
    }
  }

  private static String TYPE_UNKNOWN = "Type Unknown";
  private static String HIDDEN_FILE = "Hidden File";
  
  public void actionPerformed(ActionEvent e)
  {
    Object src = e.getSource();
    MachineSampler app = (MachineSampler) p;
    String cmd = e.getActionCommand();

    // File, Help, Global menus -----------------------------
    
    if (src == quitMI)
    {
      quit();
    }
    else if (src == soloMI)
    {
      app.handleSolo(app.getSelectedSlider());
    }
    else if (src == optionsMI)
    {
      preferences();
    }
    else if (src == restartMI)
    {
      app.restart();
    }
    else if (src == muteMI)
    {
      app.toggleMute();
    }
    else if (src == aboutMI)
    {
      about();
    }
    else if (src == openMI) // open a proj. config file
    {
      loadSamples();
    }
     
  }


}