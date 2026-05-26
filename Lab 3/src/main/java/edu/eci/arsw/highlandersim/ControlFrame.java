package edu.eci.arsw.highlandersim;

import edu.eci.arsw.immortals.Immortal;
import edu.eci.arsw.immortals.ImmortalManager;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public final class ControlFrame extends JFrame {

  private ImmortalManager manager;
  private final JTextArea output = new JTextArea(14, 40);
  private final JButton startBtn = new JButton("Start");
  private final JButton pauseAndCheckBtn = new JButton("Pause & Check");
  private final JButton resumeBtn = new JButton("Resume");
  private final JButton stopBtn = new JButton("Stop");

  private final JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(8, 2, 5000, 1));
  private final JSpinner healthSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 10000, 10));
  private final JSpinner damageSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
  private final JComboBox<String> fightMode = new JComboBox<>(new String[] { "ordered", "naive" });

  private Timer monitorTimer;

  public ControlFrame(int count, String fight) {
    setTitle("Highlander Simulator — ARSW");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout(8, 8));

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
    top.add(new JLabel("Count:"));
    countSpinner.setValue(count);
    top.add(countSpinner);
    top.add(new JLabel("Health:"));
    top.add(healthSpinner);
    top.add(new JLabel("Damage:"));
    top.add(damageSpinner);
    top.add(new JLabel("Fight:"));
    fightMode.setSelectedItem(fight);
    top.add(fightMode);
    add(top, BorderLayout.NORTH);

    output.setEditable(false);
    output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    add(new JScrollPane(output), BorderLayout.CENTER);

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
    bottom.add(startBtn);
    bottom.add(pauseAndCheckBtn);
    bottom.add(resumeBtn);
    bottom.add(stopBtn);
    add(bottom, BorderLayout.SOUTH);

    startBtn.addActionListener(this::onStart);
    pauseAndCheckBtn.addActionListener(this::onPauseAndCheck);
    resumeBtn.addActionListener(this::onResume);
    stopBtn.addActionListener(this::onStop);

    pauseAndCheckBtn.setEnabled(false);
    resumeBtn.setEnabled(false);
    stopBtn.setEnabled(false);

    // Monitor only checks for victory silently at a low frequency
    monitorTimer = new Timer(500, (evt) -> checkSimulationStatus());

    pack();
    setLocationByPlatform(true);
    setVisible(true);
  }

  private void onStart(ActionEvent e) {
    int n = (Integer) countSpinner.getValue();
    int health = (Integer) healthSpinner.getValue();
    int damage = (Integer) damageSpinner.getValue();
    String fight = (String) fightMode.getSelectedItem();

    manager = new ImmortalManager(n, fight, health, damage);
    manager.start();

    startBtn.setEnabled(false);
    pauseAndCheckBtn.setEnabled(true);
    resumeBtn.setEnabled(false);
    stopBtn.setEnabled(true);

    monitorTimer.start(); // Start auto-monitoring

    output.setText("Simulation started with %d immortals (health=%d, damage=%d, fight=%s)%n"
        .formatted(n, health, damage, fight));
  }

  private void checkSimulationStatus() {
    if (manager == null)
      return;

    if (manager.aliveCount() <= 1) {
      monitorTimer.stop();

      // Compute final status for the automatic result
      List<Immortal> pop = manager.populationSnapshot();
      java.util.List<Immortal> ranking = new java.util.ArrayList<>(pop);
      ranking.sort((a, b) -> Integer.compare(b.getHealth(), a.getHealth()));

      long sum = 0;
      StringBuilder sb = new StringBuilder();
      sb.append("--- SIMULATION FINISHED ---\n");
      for (int i = 0; i < ranking.size(); i++) {
        Immortal im = ranking.get(i);
        sum += im.getHealth();
        if (i < 10)
          sb.append(String.format("#%2d %-14s : %5d%n", i + 1, im.name(), im.getHealth()));
      }
      sb.append("--------------------------------\n");
      sb.append("Total Health: ").append(sum).append("\n");

      if (ranking.size() > 0 && ranking.get(0).getHealth() > 0) {
        sb.append("\n🏆 WINNER: ").append(ranking.get(0).name()).append("\n");
      }

      output.setText(sb.toString());

      safeStop();
      startBtn.setEnabled(true);
      pauseAndCheckBtn.setEnabled(false);
      resumeBtn.setEnabled(false);
      stopBtn.setEnabled(false);
    }
  }

  private void onPauseAndCheck(ActionEvent e) {
    if (manager == null)
      return;
    manager.pause();

    pauseAndCheckBtn.setEnabled(false);
    resumeBtn.setEnabled(true);

    List<Immortal> pop = manager.populationSnapshot();
    java.util.List<Immortal> ranking = new java.util.ArrayList<>(pop);
    ranking.sort((a, b) -> Integer.compare(b.getHealth(), a.getHealth()));

    long sum = 0;
    StringBuilder sb = new StringBuilder();
    sb.append("--- PAUSE & STATUS ---\n");
    for (int i = 0; i < ranking.size(); i++) {
      Immortal im = ranking.get(i);
      sum += im.getHealth();
      if (i < 10)
        sb.append(String.format("#%2d %-14s : %5d%n", i + 1, im.name(), im.getHealth()));
    }

    sb.append("--------------------------------\n");
    sb.append("Alive Count : ").append(manager.aliveCount()).append(" / ").append(pop.size()).append('\n');
    sb.append("Total Health: ").append(sum).append('\n');
    sb.append("Score (fights): ").append(manager.scoreBoard().totalFights()).append('\n');

    output.setText(sb.toString());
  }

  private void onResume(ActionEvent e) {
    if (manager == null)
      return;
    manager.resume();
    pauseAndCheckBtn.setEnabled(true);
    resumeBtn.setEnabled(false);
    output.append("\nSimulation resumed.\n");
  }

  private void onStop(ActionEvent e) {
    if (monitorTimer.isRunning())
      monitorTimer.stop();
    safeStop();
    startBtn.setEnabled(true);
    pauseAndCheckBtn.setEnabled(false);
    resumeBtn.setEnabled(false);
    stopBtn.setEnabled(false);
    output.append("\nSimulation stopped.\n");
  }

  private void safeStop() {
    if (manager != null) {
      manager.stop();
      manager = null;
    }
  }

  public static void main(String[] args) {
    int count = Integer.getInteger("count", 8);
    String fight = System.getProperty("fight", "ordered");
    SwingUtilities.invokeLater(() -> new ControlFrame(count, fight));
  }
}
