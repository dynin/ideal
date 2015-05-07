/*
 * Copyright 2015 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.experiment.mini;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class hi {

  private static final String FRAME_TITLE = "ideal human interface";

  private static final int FRAME_WIDTH_PX = 1000;
  private static final int FRAME_HEIGHT_PX = 680;

  private static final int STATUS_RIGHT_BORDER_PX = 8;

  private static final int VIEW_PANE_SEPARATOR_PX = 1;
  private static final int LEFT_PANE_MAX_SIZE_PX = 240;

  // List.selectionInactiveBackground
  private static final Color VIEW_PANE_SEPARATOR_COLOR = new Color(202, 202, 202);

  // textHighlight
  private static final Color LIST_HIGHLIGHT_COLOR = new Color(164, 205, 255);

  private static final String[] TAB_NAMES = {
    "Module", "Schema", "Library", "View", "Style", "Datastore"
  };

  public void show_frame() {
    JFrame frame = new JFrame();
    frame.setTitle(FRAME_TITLE);
    frame.setSize(FRAME_WIDTH_PX, FRAME_HEIGHT_PX);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Container content_pane = frame.getContentPane();
    content_pane.setLayout(new BorderLayout());
    content_pane.add(make_tabbed_pane(), BorderLayout.CENTER);
    content_pane.add(make_status_bar(), BorderLayout.PAGE_END);

    frame.setVisible(true);
  }

  public JComponent make_tabbed_pane() {
    JTabbedPane tab_pane = new JTabbedPane();
    for (String tab_name : TAB_NAMES) {
      tab_pane.addTab(tab_name, make_view_pane(tab_name));
    }
    return tab_pane;
  }

  public JComponent make_view_pane(String tab_name) {
    final JPanel panel = new JPanel(null);  // Manual layout
    final JComponent left = make_list();
    left.setBorder(new MatteBorder(0, 0, 0, VIEW_PANE_SEPARATOR_PX, VIEW_PANE_SEPARATOR_COLOR));
    panel.add(left);
    final JComponent right = make_view_pane_placeholder(tab_name);
    panel.add(right);
    panel.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        int width = panel.getWidth();
        int height = panel.getHeight();
        int leftWidth = Math.min((int) (width / 3.0), LEFT_PANE_MAX_SIZE_PX);
        left.setBounds(0, 0, leftWidth, height);
        right.setBounds(leftWidth, 0, width - leftWidth, height);
      }
    });
    return panel;
  }

  public JComponent make_list() {
    final JPanel panel = new JPanel();
    panel.setLayout(null);  // Manual layout
    boolean highlight = false;
    for (String name : TAB_NAMES) {
      final JLabel label = new JLabel(name.toLowerCase());
      label.setBackground(LIST_HIGHLIGHT_COLOR);
      if (highlight) {
        label.setOpaque(true);
      }
      highlight = !highlight;
      label.setBorder(new EmptyBorder(3, 3, 3, 3));
      label.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          label.setOpaque(!label.isOpaque());
          label.repaint();
        }
      });
      panel.add(label);
    }
    panel.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        int width = panel.getWidth();
        int height = panel.getHeight();
        int y = 0;
        for (int i = 0; i < panel.getComponentCount(); ++i) {
          Component component = panel.getComponent(i);
          int componentHeight = component.getMinimumSize().height;
          component.setBounds(0, y, width, componentHeight);
          y += componentHeight;
        }
        Dimension panelSize = new Dimension(width, y);
        panel.setSize(panelSize);
        panel.setMinimumSize(panelSize);
        panel.setPreferredSize(panelSize);
      }
    });
    return new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
  }

  public JComponent make_view_pane_placeholder(String tab_name) {
    JLabel label = new JLabel("Hello, " + tab_name + "!");
    label.setHorizontalAlignment(SwingConstants.CENTER);
    return label;
  }

  public JComponent make_status_bar() {
    JPanel bar = new JPanel(new BorderLayout());

    bar.add(new JButton("Edit"), BorderLayout.LINE_START);
    bar.add(make_sync_widget(), BorderLayout.CENTER);

    JLabel status_label = new JLabel("Ok.");
    status_label.setBorder(new EmptyBorder(0, 0, 0, STATUS_RIGHT_BORDER_PX));
    bar.add(status_label, BorderLayout.LINE_END);

    return bar;
  }

  public JComponent make_sync_widget() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

    JButton sync_now = new JButton("Sync Now");
    sync_now.setEnabled(false);
    panel.add(sync_now);

    JButton smart_sync = new JButton("Enable Smart Sync");
    panel.add(smart_sync);

    return panel;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new hi().show_frame();
      }
    });
  }
}
