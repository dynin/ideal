/*
 * Copyright 2015 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.experiment.mini;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class hi {

  private static final String FRAME_TITLE = "ideal human interface";

  private static final int FRAME_WIDTH_PX = 1000;
  private static final int FRAME_HEIGHT_PX = 680;

  private static final int STATUS_RIGHT_BORDER_PX = 8;

  private static final String[] TAB_NAMES = {
    "Module", "Schema", "Library", "View", "Style", "Datastore"
  };

  public static void show_frame() {
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

  public static JTabbedPane make_tabbed_pane() {
    JTabbedPane tab_pane = new JTabbedPane();
    for (String tab_name : TAB_NAMES) {
      JLabel label = new JLabel("Hello, " + tab_name + "!");
      label.setHorizontalAlignment(SwingConstants.CENTER);

      tab_pane.addTab(tab_name, label);
    }
    return tab_pane;
  }

  public static Container make_status_bar() {
    JPanel bar = new JPanel();
    bar.setLayout(new BorderLayout());

    bar.add(new JButton("Edit"), BorderLayout.LINE_START);

    JLabel status_label = new JLabel("Ok.");
    status_label.setBorder(new EmptyBorder(0, 0, 0, STATUS_RIGHT_BORDER_PX));
    bar.add(status_label, BorderLayout.LINE_END);

    return bar;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        show_frame();
      }
    });
  }
}
