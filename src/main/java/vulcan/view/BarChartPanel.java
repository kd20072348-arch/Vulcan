package vulcan.view;

import vulcan.util.StringUtils;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;

/** Minimal bar chart drawn with Java2D (no chart library needed). */
public class BarChartPanel extends JPanel {
    private Map<String, Integer> data = new LinkedHashMap<>();
    private String title = "";

    public BarChartPanel() {
        setBackground(Color.WHITE);
    }

    public void setData(String title, Map<String, Integer> data) {
        this.title = title;
        this.data = new LinkedHashMap<>(data);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontMetrics fm = g2.getFontMetrics();

        if (data.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.drawString("Run the analysis to see the chart.", 20, 30);
            g2.dispose();
            return;
        }

        int left = 50, right = 20, top = 50, bottom = 50;
        int plotW = getWidth() - left - right;
        int plotH = getHeight() - top - bottom;
        int max = Math.max(1, data.values().stream().mapToInt(Integer::intValue).max().orElse(1));

        g2.setColor(Color.DARK_GRAY);
        g2.drawString(title, left, 28);
        g2.drawLine(left, top, left, top + plotH);
        g2.drawLine(left, top + plotH, left + plotW, top + plotH);

        int n = data.size();
        int slot = plotW / n;
        int barW = Math.max(8, slot * 2 / 3);
        int i = 0;
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            int h = (int) ((double) e.getValue() / max * plotH);
            int x = left + i * slot + (slot - barW) / 2;
            int y = top + plotH - h;
            g2.setColor(new Color(0x2E75B6));
            g2.fillRect(x, y, barW, h);
            g2.setColor(Color.DARK_GRAY);
            String val = String.valueOf(e.getValue());
            g2.drawString(val, x + (barW - fm.stringWidth(val)) / 2, y - 4);
            String label = StringUtils.truncate(e.getKey(), Math.max(4, slot / 7));
            g2.drawString(label, x + (barW - fm.stringWidth(label)) / 2, top + plotH + 18);
            i++;
        }
        g2.dispose();
    }
}
