/*
 * Copyright 2015 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.experiment.mini;

import javax.swing.*;

public class hi {

  private static final String FRAME_TITLE = "ideal human interface";

  private static final int FRAME_WIDTH_PX = 1000;
  private static final int FRAME_HEIGHT_PX = 680;

  private static final String[] TAB_NAMES = {
    "Module", "Schema", "Library", "View", "Style", "Datastore"
  };

  public static void show_frame() {
    JFrame frame = new JFrame();
    frame.setTitle(FRAME_TITLE);
    frame.setSize(FRAME_WIDTH_PX, FRAME_HEIGHT_PX);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.getContentPane().add(make_tabbed_pane());

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

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        show_frame();
      }
    });
  }
}
