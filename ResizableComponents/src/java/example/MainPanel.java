// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

public class MainPanel extends JPanel {
  protected final JLayeredPane layeredPane = new JLayeredPane() {
    @Override public boolean isOptimizedDrawingEnabled() {
      return false;
    }
  };
  protected final JToolBar toolbar = new JToolBar("Resizable Components");
  protected final Point pt = new Point();

  public MainPanel() {
    super(new BorderLayout());
    JPopupMenu popup = new JPopupMenu() {
      @Override public void show(Component c, int x, int y) {
        pt.setLocation(x, y);
        super.show(c, x, y);
      }
    };
    popup.add("table").addActionListener(e -> createTable());
    popup.add("tree").addActionListener(e -> createTree());

    layeredPane.setComponentPopupMenu(popup);
    // ??? for 1.5.0
    // layeredPane.addMouseListener(new MouseAdapter() {
    //   /* Dummy listener */
    // });
    add(layeredPane);
    toolbar.add(new AbstractAction("add table") {
      @Override public void actionPerformed(ActionEvent e) {
        pt.setLocation(pt.x + 20, pt.y + 20);
        createTable();
      }
    });
    toolbar.addSeparator();
    toolbar.add(new AbstractAction("add tree") {
      @Override public void actionPerformed(ActionEvent e) {
        pt.setLocation(pt.x + 20, pt.y + 20);
        createTree();
      }
    });
    add(toolbar, BorderLayout.NORTH);
    setPreferredSize(new Dimension(320, 240));
  }

  protected final void createTree() {
    JTree tree = new JTree();
    tree.setVisibleRowCount(8);
    Component c = new JScrollPane(tree);
    Dimension r = c.getPreferredSize();
    JResizer resizer = new JResizer(new BorderLayout());
    resizer.add(c);
    resizer.setBounds(pt.x, pt.y, r.width, r.height);
    layeredPane.add(resizer);
    layeredPane.moveToFront(resizer);
  }

  protected final void createTable() {
    JTable table = new JTable(12, 3);
    table.setPreferredScrollableViewportSize(new Dimension(160, 160));
    Component c = new JScrollPane(table);
    Dimension r = c.getPreferredSize();
    JResizer resizer = new JResizer(new BorderLayout());
    resizer.add(c);
    resizer.setBounds(pt.x, pt.y, r.width, r.height);
    layeredPane.add(resizer);
    layeredPane.moveToFront(resizer);
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(MainPanel::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
      Toolkit.getDefaultToolkit().beep();
    }
    JFrame frame = new JFrame("@title@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

class JResizer extends JPanel { // implements Serializable {
  private transient MouseInputListener resizeListener;

  protected JResizer(LayoutManager layout) {
    super(layout);
  }

  @Override public void updateUI() {
    removeMouseListener(resizeListener);
    removeMouseMotionListener(resizeListener);
    super.updateUI();
    resizeListener = new ResizeMouseListener();
    addMouseListener(resizeListener);
    addMouseMotionListener(resizeListener);
    setBorder(new DefaultResizableBorder(6));
  }
  // private void writeObject(ObjectOutputStream out) throws IOException {}
  // private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {}
  // private void readObjectNoData() throws ObjectStreamException {}
  // private void readObject() {
  //   this.resizeListener = new ResizeMouseListener();
  // }
  // private Object readResolve() {
  //   this.resizeListener = new ResizeMouseListener();
  //   return this;
  // }

  @Override public void setBorder(Border border) {
    removeMouseListener(resizeListener);
    removeMouseMotionListener(resizeListener);
    if (border instanceof ResizableBorder) {
      addMouseListener(resizeListener);
      addMouseMotionListener(resizeListener);
    }
    super.setBorder(border);
  }
}

// Resizable Components - Santhosh Kumar's Weblog
// http://www.jroller.com/santhosh/entry/resizable_components
interface ResizableBorder extends Border {
  int getResizeCursor(MouseEvent e);
}

class DefaultResizableBorder implements ResizableBorder, SwingConstants {
  private final int dist;

  private static int[] locations = {
    NORTH,
    SOUTH,
    WEST,
    EAST,
    NORTH_WEST,
    NORTH_EAST,
    SOUTH_WEST,
    SOUTH_EAST,
    0, // move
    -1, // no location
  };

  private static int[] cursors = {
    Cursor.N_RESIZE_CURSOR,
    Cursor.S_RESIZE_CURSOR,
    Cursor.W_RESIZE_CURSOR,
    Cursor.E_RESIZE_CURSOR,
    Cursor.NW_RESIZE_CURSOR,
    Cursor.NE_RESIZE_CURSOR,
    Cursor.SW_RESIZE_CURSOR,
    Cursor.SE_RESIZE_CURSOR,
    Cursor.MOVE_CURSOR,
    Cursor.DEFAULT_CURSOR,
  };

  protected DefaultResizableBorder(int dist) {
    super();
    this.dist = dist;
  }

  @Override public Insets getBorderInsets(Component component) {
    return new Insets(dist, dist, dist, dist);
  }

  @Override public boolean isBorderOpaque() {
    return false;
  }

  @Override public void paintBorder(Component component, Graphics g, int x, int y, int w, int h) {
    g.setColor(Color.black);
    g.drawRect(x + dist / 2, y + dist / 2, w - dist, h - dist);
    Rectangle rect = new Rectangle(dist, dist);
    for (int i = 0; i < locations.length - 2; i++) {
      rect.setLocation(getPoint(x, y, w, h, locations[i]));
      g.setColor(Color.WHITE);
      g.fillRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
      g.setColor(Color.BLACK);
      g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
    }
  }

  private Point getPoint(int x, int y, int w, int h, int location) {
    switch (location) {
      case NORTH: return new Point(x + w / 2 - dist / 2, y);
      case SOUTH: return new Point(x + w / 2 - dist / 2, y + h - dist);
      case WEST: return new Point(x, y + h / 2 - dist / 2);
      case EAST: return new Point(x + w - dist, y + h / 2 - dist / 2);
      case NORTH_WEST: return new Point(x, y);
      case NORTH_EAST: return new Point(x + w - dist, y);
      case SOUTH_WEST: return new Point(x, y + h - dist);
      case SOUTH_EAST: return new Point(x + w - dist, y + h - dist);
      default: return new Point(-100, -100); // throw new AssertionError("Unknown location");
    }
  }

  @Override public int getResizeCursor(MouseEvent e) {
    Component c = e.getComponent();
    int w = c.getWidth();
    int h = c.getHeight();
    Point pt = e.getPoint();

    Rectangle bounds = new Rectangle(w, h);
    if (!bounds.contains(pt)) {
      return Cursor.DEFAULT_CURSOR;
    }

    Rectangle actualBounds = new Rectangle(dist, dist, w - 2 * dist, h - 2 * dist);
    if (actualBounds.contains(pt)) {
      return Cursor.DEFAULT_CURSOR;
    }
    Rectangle r = new Rectangle(dist, dist);
    for (int i = 0; i < locations.length - 2; i++) {
      r.setLocation(getPoint(0, 0, w, h, locations[i]));
      if (r.contains(pt)) {
        return cursors[i];
      }
    }
    return Cursor.MOVE_CURSOR;
  }
}
