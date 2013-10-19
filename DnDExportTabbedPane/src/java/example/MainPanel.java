package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.beans.*;
import java.io.IOException;
import java.util.Arrays;
import javax.activation.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class MainPanel extends JPanel {
    private final DnDTabbedPane tab = new DnDTabbedPane();
    public MainPanel() {
        super(new BorderLayout());
        DnDTabbedPane sub = new DnDTabbedPane();
        sub.addTab("Title aa", new JLabel("aaa"));
        sub.addTab("Title bb", new JScrollPane(new JTree()));
        sub.addTab("Title cc", new JScrollPane(makeJTextArea()));

        tab.addTab("JTree 00",       new JScrollPane(new JTree()));
        tab.addTab("JLabel 01",      new JLabel("Test"));
        tab.addTab("JTable 02",      new JScrollPane(makeJTable()));
        tab.addTab("JTextArea 03",   new JScrollPane(makeJTextArea()));
        tab.addTab("JLabel 04",      new JLabel("<html>asfasfdasdfasdfsa<br>asfdd13412341234123446745fgh"));
        tab.addTab("null 05",        null);
        tab.addTab("JTabbedPane 06", sub);
        tab.addTab("Title 000000000000000006", new JScrollPane(new JTree()));
        ////ButtonTabComponent
        //for(int i=0;i<tab.getTabCount();i++) tab.setTabComponentAt(i, new ButtonTabComponent(tab));

        DnDTabbedPane sub2 = new DnDTabbedPane();
        sub2.addTab("Title aa", new JLabel("aaa"));
        sub2.addTab("Title bb", new JScrollPane(new JTree()));
        sub2.addTab("Title cc", new JScrollPane(makeJTextArea()));

        DropTargetListener dtl = new DropTargetAdapter() {
            private void clearDropLocationPaint(Component c) {
                System.out.println("------------------- "+ c.getName());
                if(c instanceof DnDTabbedPane) {
                    DnDTabbedPane t = (DnDTabbedPane)c;
                    t.setDropLocation(null, null, false);
                    t.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
            @Override public void drop(DropTargetDropEvent dtde) {
                System.out.println("DropTargetListener#drop");
                Component c = dtde.getDropTargetContext().getComponent();
                clearDropLocationPaint(c);
            }
            @Override public void dragExit(DropTargetEvent dte) {
                System.out.println("DropTargetListener#dragExit");
                Component c = dte.getDropTargetContext().getComponent();
                clearDropLocationPaint(c);
            }
            @Override public void dragEnter(DropTargetDragEvent dtde) {
                System.out.println("DropTargetListener#dragEnter");
            }
//             @Override public void dragOver(DropTargetDragEvent dtde) {
//                 //System.out.println("dragOver");
//             }
//             @Override public void dropActionChanged(DropTargetDragEvent dtde) {
//                 System.out.println("dropActionChanged");
//             }
        };
        tab.setName("000");
        sub.setName("111");
        sub2.setName("222");

        TransferHandler handler = new TabTransferHandler();
        try{
            for(JTabbedPane t:Arrays.asList(tab, sub, sub2)) {
                t.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                t.setTransferHandler(handler);
                t.getDropTarget().addDropTargetListener(dtl);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }

        JPanel p = new JPanel(new GridLayout(2,1));
        p.add(tab);
        p.add(sub2);
        add(p);
        add(makeCheckBoxPanel(), BorderLayout.NORTH);
        setPreferredSize(new Dimension(320, 240));
    }
    private JComponent makeCheckBoxPanel() {
        final JCheckBox tcheck  = new JCheckBox("Top", true);
        tcheck.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                tab.setTabPlacement(tcheck.isSelected()?JTabbedPane.TOP:JTabbedPane.RIGHT);
            }
        });
        final JCheckBox scheck  = new JCheckBox("SCROLL_TAB_LAYOUT", true);
        scheck.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                tab.setTabLayoutPolicy(scheck.isSelected()?JTabbedPane.SCROLL_TAB_LAYOUT:JTabbedPane.WRAP_TAB_LAYOUT);
            }
        });
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(tcheck);
        p.add(scheck);
        return p;
    }
    private JTextArea makeJTextArea() {
        JTextArea textArea = new JTextArea("asfasdfasfasdfas\nafasfasdfaf\n");
        //textArea.setTransferHandler(null); //XXX
        return textArea;
    }
    private JTable makeJTable() {
        String[] columnNames = {"String", "Integer", "Boolean"};
        Object[][] data = {
            {"AAA", 1, true}, {"BBB", 2, false},
        };
        TableModel model = new DefaultTableModel(data, columnNames) {
            @Override public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        return new JTable(model);
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class DnDTabbedPane extends JTabbedPane {
    private static final int LINEWIDTH = 3;
    private final Rectangle lineRect = new Rectangle();
    public int dragTabIndex = -1;

    public static final class DropLocation extends TransferHandler.DropLocation {
        private final int index;
        private DropLocation(Point p, int index) {
            super(p);
            this.index = index;
        }
        public int getIndex() {
            return index;
        }
        private boolean dropable = true;
        public void setDropable(boolean flag) {
            dropable = flag;
        }
        public boolean isDropable() {
            return dropable;
        }
//         @Override public String toString() {
//             return getClass().getName()
//                    + "[dropPoint=" + getDropPoint() + ","
//                    + "index=" + index + ","
//                    + "insert=" + isInsert + "]";
//         }
    }

    private void clickArrowButton(String actionKey) {
        ActionMap map = getActionMap();
        if(map != null) {
            Action action = map.get(actionKey);
            if(action != null && action.isEnabled()) {
                action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, 0, 0));
            }
        }
    }
    public static Rectangle rBackward = new Rectangle();
    public static Rectangle rForward  = new Rectangle();
    private static int rwh = 20;
    private static int buttonsize = 30; //XXX 30 is magic number of scroll button size
    public void autoScrollTest(Point pt) {
        Rectangle r = getTabAreaBounds();
        int tabPlacement = getTabPlacement();
        if(tabPlacement==TOP || tabPlacement==BOTTOM) {
            rBackward.setBounds(r.x, r.y, rwh, r.height);
            rForward.setBounds(r.x+r.width-rwh-buttonsize, r.y, rwh+buttonsize, r.height);
        }else if(tabPlacement==LEFT || tabPlacement==RIGHT) {
            rBackward.setBounds(r.x, r.y, r.width, rwh);
            rForward.setBounds(r.x, r.y+r.height-rwh-buttonsize, r.width, rwh+buttonsize);
        }
        if(rBackward.contains(pt)) {
            clickArrowButton("scrollTabsBackwardAction");
        }else if(rForward.contains(pt)) {
            clickArrowButton("scrollTabsForwardAction");
        }
    }
    public DnDTabbedPane() {
        super();
        Handler h = new Handler();
        addMouseListener(h);
        addMouseMotionListener(h);
        addPropertyChangeListener(h);
    }
    private DropMode dropMode = DropMode.INSERT;
    public DropLocation dropLocationForPoint(Point p) {
        boolean isTB = getTabPlacement()==JTabbedPane.TOP || getTabPlacement()==JTabbedPane.BOTTOM;
        switch(dropMode) {
          case INSERT:
            for(int i=0; i<getTabCount(); i++) {
                if(getBoundsAt(i).contains(p)) return new DropLocation(p, i);
            }
            if(getTabAreaBounds().contains(p)) return new DropLocation(p, getTabCount());
            break;
          case USE_SELECTION:
          case ON:
          case ON_OR_INSERT:
          default:
            assert false : "Unexpected drop mode";
        }
        return new DropLocation(p, -1);
    }
    private transient DropLocation dropLocation;
    public final DropLocation getDropLocation() {
        return dropLocation;
    }
    public Object setDropLocation(TransferHandler.DropLocation location, Object state, boolean forDrop) {
        DropLocation old = dropLocation;
        if(location==null || !forDrop) {
            dropLocation = new DropLocation(new Point(), -1);
        }else if(location instanceof DropLocation) {
            dropLocation = (DropLocation)location;
        }
        firePropertyChange("dropLocation", old, dropLocation);
        return null;
    }
    public void exportTab(int dragIndex, JTabbedPane target, int targetIndex) {
        System.out.println("exportTab");
        if(targetIndex<0) return;

        Component cmp    = getComponentAt(dragIndex);
        Container parent = target;
        while(parent!=null) {
            if(cmp==parent) return; //target==child: JTabbedPane in JTabbedPane
            parent = parent.getParent();
        }

        Component tab = getTabComponentAt(dragIndex);
        String str    = getTitleAt(dragIndex);
        Icon icon     = getIconAt(dragIndex);
        String tip    = getToolTipTextAt(dragIndex);
        boolean flg   = isEnabledAt(dragIndex);
        remove(dragIndex);
        target.insertTab(str, icon, cmp, tip, targetIndex);
        target.setEnabledAt(targetIndex, flg);
        ////ButtonTabComponent
        //if(tab instanceof ButtonTabComponent) tab = new ButtonTabComponent(target);
        target.setTabComponentAt(targetIndex, tab);
        target.setSelectedIndex(targetIndex);
        if(tab!=null && tab instanceof JComponent)
            ((JComponent)tab).scrollRectToVisible(tab.getBounds());
    }

    public void convertTab(int prev, int next) {
        System.out.println("convertTab");
        if(next<0 || prev==next) {
            return;
        }
        Component cmp = getComponentAt(prev);
        Component tab = getTabComponentAt(prev);
        String str    = getTitleAt(prev);
        Icon icon     = getIconAt(prev);
        String tip    = getToolTipTextAt(prev);
        boolean flg   = isEnabledAt(prev);
        int tgtindex  = prev>next ? next : next-1;
        remove(prev);
        insertTab(str, icon, cmp, tip, tgtindex);
        setEnabledAt(tgtindex, flg);
        //When you drag'n'drop a disabled tab, it finishes enabled and selected.
        //pointed out by dlorde
        if(flg) setSelectedIndex(tgtindex);
        //I have a component in all tabs (jlabel with an X to close the tab) and when i move a tab the component disappear.
        //pointed out by Daniel Dario Morales Salas
        setTabComponentAt(tgtindex, tab);
    }
    public Rectangle getDropLineRect() {
        DropLocation loc = getDropLocation();
        if(loc == null || !loc.isDropable()) return null;

        int index = loc.getIndex();
        if(index<0) {
            lineRect.setRect(0,0,0,0);
            return null;
        }
        boolean isZero = index==0;
        Rectangle r = getBoundsAt(isZero?0:index-1);
        if(getTabPlacement()==TOP || getTabPlacement()==BOTTOM) {
            lineRect.setRect(r.x-LINEWIDTH/2+r.width*(isZero?0:1), r.y,LINEWIDTH,r.height);
        }else{
            lineRect.setRect(r.x,r.y-LINEWIDTH/2+r.height*(isZero?0:1), r.width,LINEWIDTH);
        }
        return lineRect;
    }
    public Rectangle getTabAreaBounds() {
        Rectangle tabbedRect = getBounds();
        int xx = tabbedRect.x;
        int yy = tabbedRect.y;
        Component c = getSelectedComponent();
        if(c==null) return tabbedRect;
        Rectangle compRect = getSelectedComponent().getBounds();
        int tabPlacement = getTabPlacement();
        if(tabPlacement==TOP) {
            tabbedRect.height = tabbedRect.height - compRect.height;
        }else if(tabPlacement==BOTTOM) {
            tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
            tabbedRect.height = tabbedRect.height - compRect.height;
        }else if(tabPlacement==LEFT) {
            tabbedRect.width = tabbedRect.width - compRect.width;
        }else if(tabPlacement==RIGHT) {
            tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
            tabbedRect.width = tabbedRect.width - compRect.width;
        }
        tabbedRect.translate(-xx,-yy);
        //tabbedRect.grow(2, 2);
        return tabbedRect;
    }

    private class Handler extends MouseAdapter implements PropertyChangeListener { //, BeforeDrag
        private void repaintDropLocation(DropLocation loc) {
            Component c = getRootPane().getGlassPane();
            if(c instanceof GhostGlassPane) {
                GhostGlassPane glassPane = (GhostGlassPane)c;
                glassPane.setTargetTabbedPane(DnDTabbedPane.this);
                glassPane.repaint();
            }
        }
        // PropertyChangeListener
        @Override public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if("dropLocation".equals(propertyName)) {
                //System.out.println("propertyChange: dropLocation");
                repaintDropLocation(getDropLocation());
            }
        }
        // MouseListener
        @Override public void mousePressed(MouseEvent e) {
            DnDTabbedPane src = (DnDTabbedPane)e.getSource();
            if(src.getTabCount()<=1) {
                startPt = null;
                return;
            }
            Point tabPt = e.getPoint(); //e.getDragOrigin();
            int idx = src.indexAtLocation(tabPt.x, tabPt.y);
            //disabled tab, null component problem.
            //pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
            boolean flag = idx<0 || !src.isEnabledAt(idx) || src.getComponentAt(idx)==null;
            startPt = flag ? null : tabPt;
        }
        private Point startPt;
        int gestureMotionThreshold = DragSource.getDragThreshold();
        //private final Integer gestureMotionThreshold = (Integer)Toolkit.getDefaultToolkit().getDesktopProperty("DnD.gestureMotionThreshold");
        @Override public void mouseDragged(MouseEvent e)  {
            Point tabPt = e.getPoint(); //e.getDragOrigin();
            if(startPt!=null && Math.sqrt(Math.pow(tabPt.x-startPt.x, 2)+Math.pow(tabPt.y-startPt.y, 2))>gestureMotionThreshold) {
                DnDTabbedPane src = (DnDTabbedPane)e.getSource();
                TransferHandler th = src.getTransferHandler();
                dragTabIndex = src.indexAtLocation(tabPt.x, tabPt.y);
                th.exportAsDrag(src, e, TransferHandler.MOVE);
                lineRect.setRect(0,0,0,0);
                src.getRootPane().getGlassPane().setVisible(true);
                src.setDropLocation(new DropLocation(tabPt, -1), null, true);
                startPt = null;
            }
        }
//         @Override public void mouseClicked(MouseEvent e)  {}
//         @Override public void mouseEntered(MouseEvent e)  {}
//         @Override public void mouseExited(MouseEvent e)   {}
//         @Override public void mouseMoved(MouseEvent e)    {}
//         @Override public void mouseReleased(MouseEvent e) {}
    }
}

class TabTransferHandler extends TransferHandler {
    private final DataFlavor localObjectFlavor;
    public TabTransferHandler() {
        System.out.println("TabTransferHandler");
        localObjectFlavor = new ActivationDataFlavor(DnDTabbedPane.class, DataFlavor.javaJVMLocalObjectMimeType, "DnDTabbedPane");
    }
    private DnDTabbedPane source = null;
    @Override protected Transferable createTransferable(JComponent c) {
        System.out.println("createTransferable");
        if(c instanceof DnDTabbedPane) source = (DnDTabbedPane)c;
        return new DataHandler(c, localObjectFlavor.getMimeType());
    }
    @Override public boolean canImport(TransferSupport support) {
        //System.out.println("canImport");
        if(!support.isDrop() || !support.isDataFlavorSupported(localObjectFlavor)) {
            System.out.println("canImport:"+support.isDrop()+" "+support.isDataFlavorSupported(localObjectFlavor));
            return false;
        }
        support.setDropAction(MOVE);
        DropLocation tdl = support.getDropLocation();
        Point pt = tdl.getDropPoint();
        DnDTabbedPane target = (DnDTabbedPane)support.getComponent();
        target.autoScrollTest(pt);
        DnDTabbedPane.DropLocation dl = (DnDTabbedPane.DropLocation)target.dropLocationForPoint(pt);
        int idx = dl.getIndex();
        boolean isDropable = false;

//         if(!isWebStart()) {
//             //System.out.println("local");
//             try{
//                 source = (DnDTabbedPane)support.getTransferable().getTransferData(localObjectFlavor);
//             }catch(Exception ex) {
//                 ex.printStackTrace();
//             }
//         }
        if(target==source) {
            //System.out.println("target==source");
            isDropable = target.getTabAreaBounds().contains(pt) && idx>=0 && idx!=target.dragTabIndex && idx!=target.dragTabIndex+1;
        }else{
            //System.out.format("target!=source%n  target: %s%n  source: %s", target.getName(), source.getName());
            if(source!=null && target!=source.getComponentAt(source.dragTabIndex)) {
                isDropable = target.getTabAreaBounds().contains(pt) && idx>=0;
            }
        }
        glassPane.setVisible(false);
        target.getRootPane().setGlassPane(glassPane);
        // Bug ID: 6700748 Cursor flickering during D&D when using CellRendererPane with validation
        // http://bugs.sun.com/view_bug.do?bug_id=6700748
        glassPane.setCursor(isDropable?DragSource.DefaultMoveDrop:DragSource.DefaultMoveNoDrop);
        glassPane.setVisible(true);
        target.setCursor(isDropable?DragSource.DefaultMoveDrop:DragSource.DefaultMoveNoDrop);
        //Component c = target.getRootPane().getGlassPane();
        //c.setCursor(isDropable?DragSource.DefaultMoveDrop:DragSource.DefaultMoveNoDrop);
        if(isDropable) {
            support.setShowDropLocation(true);
            dl.setDropable(true);
            target.setDropLocation(dl, null, true);
            return true;
        }else{
            support.setShowDropLocation(false);
            dl.setDropable(false);
            target.setDropLocation(dl, null, false);
            return false;
        }
    }
//     private static boolean isWebStart() {
//         try{
//             javax.jnlp.ServiceManager.lookup("javax.jnlp.BasicService");
//             return true;
//         }catch(Throwable ex) {
//             return false;
//         }
//     }
    private BufferedImage makeDragTabImage(DnDTabbedPane tabbedPane) {
        Rectangle rect = tabbedPane.getBoundsAt(tabbedPane.dragTabIndex);
        BufferedImage image = new BufferedImage(tabbedPane.getWidth(), tabbedPane.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        tabbedPane.paint(g);
        g.dispose();
        if(rect.x<0) {
            rect.translate(-rect.x,0);
        }
        if(rect.y<0) {
            rect.translate(0,-rect.y);
        }
        if(rect.x+rect.width>image.getWidth()) {
            rect.width = image.getWidth() - rect.x;
        }
        if(rect.y+rect.height>image.getHeight()) {
            rect.height = image.getHeight() - rect.y;
        }
        return image.getSubimage(rect.x,rect.y,rect.width,rect.height);
    }

    private static GhostGlassPane glassPane;
    @Override public int getSourceActions(JComponent c) {
        System.out.println("getSourceActions");
        DnDTabbedPane src = (DnDTabbedPane)c;
        if(glassPane==null) {
            c.getRootPane().setGlassPane(glassPane = new GhostGlassPane(src));
        }
        if(src.dragTabIndex<0) return NONE;
//*
        glassPane.setImage(makeDragTabImage(src));
/*/ //java 1.7.0
        setDragImage(makeDragTabImage(src));
//*/
        c.getRootPane().getGlassPane().setVisible(true);
        return MOVE;
    }
    @Override public boolean importData(TransferSupport support) {
        System.out.println("importData");
        if(!canImport(support)) return false;

        DnDTabbedPane target = (DnDTabbedPane)support.getComponent();
        DnDTabbedPane.DropLocation dl = target.getDropLocation();
        try{
            DnDTabbedPane source = (DnDTabbedPane)support.getTransferable().getTransferData(localObjectFlavor);
            int index = dl.getIndex(); //boolean insert = dl.isInsert();
            if(target==source) {
                source.convertTab(source.dragTabIndex, index); //getTargetTabIndex(e.getLocation()));
            }else{
                source.exportTab(source.dragTabIndex, target, index);
            }
            return true;
        }catch(UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }
    @Override protected void exportDone(JComponent c, Transferable data, int action) {
        System.out.println("exportDone");
        DnDTabbedPane src = (DnDTabbedPane)c;
        src.setDropLocation(null, null, false);
        src.repaint();
        glassPane.setVisible(false);
        src.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        //glassPane = null;
        //source = null;
    }
}
//*
class GhostGlassPane extends JPanel {
    private DnDTabbedPane tabbedPane;
    public GhostGlassPane(DnDTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
        //System.out.println("new GhostGlassPane");
        setOpaque(false);
        // Bug ID: 6700748 Cursor flickering during D&D when using CellRendererPane with validation
        // http://bugs.sun.com/view_bug.do?bug_id=6700748
        //setCursor(null); //XXX
    }
    private BufferedImage draggingGhost = null;
    public void setImage(BufferedImage draggingGhost) {
        this.draggingGhost = draggingGhost;
    }
    public void setTargetTabbedPane(DnDTabbedPane tab) {
        tabbedPane = tab;
    }
    @Override public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        DnDTabbedPane.DropLocation dl = tabbedPane.getDropLocation();
        Point p = getMousePosition(true); //dl.getDropPoint();
        if(draggingGhost != null && dl != null && p != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            Rectangle rect = tabbedPane.getDropLineRect();
            if(rect!=null && dl.isDropable()) {
                Rectangle r = SwingUtilities.convertRectangle(tabbedPane, rect, this);
                g2.setColor(Color.RED);
                g2.fill(r);
                //tabbedPane.paintDropLine(g2);
            }
            //Point p = SwingUtilities.convertPoint(tabbedPane, dl.getDropPoint(), this);
            double xx = p.getX() - draggingGhost.getWidth(this) /2d;
            double yy = p.getY() - draggingGhost.getHeight(this)/2d;
            g2.drawImage(draggingGhost, (int)xx, (int)yy , this);
        }
    }
}
/*/ //java 1.7.0
class GhostGlassPane extends JPanel {
    private DnDTabbedPane tabbedPane;
    public GhostGlassPane(DnDTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
        setOpaque(false);
    }
    public void setTargetTabbedPane(DnDTabbedPane tab) {
        tabbedPane = tab;
    }
    @Override public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //tabbedPane.paintDropLine(g2);
        Rectangle rect = tabbedPane.getDropLineRect();
        if(rect!=null) {
            Rectangle r = SwingUtilities.convertRectangle(tabbedPane, rect, this);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2.setColor(Color.RED);
            g2.fill(r);
            //tabbedPane.paintDropLine(g2);
        }
    }
}
//*/

//// a closeable tab test
//// http://download.oracle.com/javase/tutorial/uiswing/examples/components/index.html#TabComponentsDemo
// class ButtonTabComponent extends JPanel {
//     private final JTabbedPane pane;
//     public ButtonTabComponent(final JTabbedPane pane) {
//         super(new FlowLayout(FlowLayout.LEFT, 0, 0));
//         if(pane == null) {
//             throw new NullPointerException("TabbedPane is null");
//         }
//         this.pane = pane;
//         setOpaque(false);
//         JLabel label = new JLabel() {
//             @Override public String getText() {
//                 int i = pane.indexOfTabComponent(ButtonTabComponent.this);
//                 if(i != -1) {
//                     return pane.getTitleAt(i);
//                 }
//                 return null;
//             }
//         };
//         add(label);
//         label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
//         JButton button = new TabButton();
//         add(button);
//         setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
//     }
//     private class TabButton extends JButton implements ActionListener {
//         public TabButton() {
//             int size = 17;
//             setPreferredSize(new Dimension(size, size));
//             setToolTipText("close this tab");
//             setUI(new javax.swing.plaf.basic.BasicButtonUI());
//             setContentAreaFilled(false);
//             setFocusable(false);
//             setBorder(BorderFactory.createEtchedBorder());
//             setBorderPainted(false);
//             addMouseListener(buttonMouseListener);
//             setRolloverEnabled(true);
//             addActionListener(this);
//         }
//         @Override public void actionPerformed(ActionEvent e) {
//             int i = pane.indexOfTabComponent(ButtonTabComponent.this);
//             if(i != -1) pane.remove(i);
//         }
//         @Override public void updateUI() {}
//         @Override protected void paintComponent(Graphics g) {
//             super.paintComponent(g);
//             Graphics2D g2 = (Graphics2D) g.create();
//             g2.setStroke(new BasicStroke(2));
//             g2.setColor(Color.BLACK);
//             if(getModel().isRollover()) {
//                 g2.setColor(Color.ORANGE);
//             }
//             if(getModel().isPressed()) {
//                 g2.setColor(Color.BLUE);
//             }
//             int delta = 6;
//             g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
//             g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
//             g2.dispose();
//         }
//     }
//     private final static MouseListener buttonMouseListener = new MouseAdapter() {
//         @Override public void mouseEntered(MouseEvent e) {
//             Component component = e.getComponent();
//             if(component instanceof AbstractButton) {
//                 AbstractButton button = (AbstractButton) component;
//                 button.setBorderPainted(true);
//             }
//         }
//         @Override public void mouseExited(MouseEvent e) {
//             Component component = e.getComponent();
//             if(component instanceof AbstractButton) {
//                 AbstractButton button = (AbstractButton) component;
//                 button.setBorderPainted(false);
//             }
//         }
//     };
// }
