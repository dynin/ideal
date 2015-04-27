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

  public static void showFrame() {
    JFrame frame = new JFrame();
    frame.setTitle("ideal human interface");
    frame.setSize(500, 300);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JLabel label = new JLabel("Hello, world!");
    label.setHorizontalAlignment(SwingConstants.CENTER);
    frame.getContentPane().add(label);

    frame.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        showFrame();
      }
    });
  }
}
