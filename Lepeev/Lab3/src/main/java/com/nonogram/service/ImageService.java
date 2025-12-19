package com.nonogram.service;

import com.nonogram.entity.Crossword;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;

@Service
public class ImageService {

    public Crossword generateCrossword(String imageUrl, int size, int k) throws Exception {
        if (size > 100) size = 100;
        if (size < 5) size = 5;

        BufferedImage original = downloadImage(imageUrl);
        BufferedImage resized = resizeImage(original, size, size);

        List<Color> dominantColors = extractDominantColors(resized, k);

        int[][] grid = new int[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int rgb = resized.getRGB(x, y);
                Color pixelColor = new Color(rgb);

                if ((rgb >> 24) == 0x00 || isWhite(pixelColor)) {
                    grid[y][x] = -1;
                } else {
                    grid[y][x] = findNearestColorIndex(pixelColor, dominantColors);
                }
            }
        }

        grid = applySymmetryFix(grid);
        grid = forceHorizontalSymmetry(grid);

        Crossword crossword = new Crossword();
        crossword.setOriginalImageUrl(imageUrl);
        crossword.setWidth(size);
        crossword.setHeight(size);
        crossword.setSolutionGrid(serializeGrid(grid));

        List<String> hexPalette = new ArrayList<>();
        hexPalette.add("#FFFFFF");
        for (Color c : dominantColors) {
            hexPalette.add(String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
        }
        crossword.setPalette(String.join(",", hexPalette));

        crossword.setRowClues(generateClues(grid, true));
        crossword.setColClues(generateClues(grid, false));

        return crossword;
    }

    private List<Color> extractDominantColors(BufferedImage image, int k) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                Color c = new Color(rgb);
                if (!isWhite(c) && (rgb >> 24) != 0x00) {
                    counts.put(rgb, counts.getOrDefault(rgb, 0) + 1);
                }
            }
        }

        List<Map.Entry<Integer, Integer>> sorted = new ArrayList<>(counts.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<Color> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : sorted) {
            if (result.size() >= k) break;
            Color newColor = new Color(entry.getKey());
            boolean ok = true;
            for (Color existing : result) {
                if (getColorDistance(newColor, existing) < 2000) {
                    ok = false;
                    break;
                }
            }
            if (ok) result.add(newColor);
        }
        return result;
    }

    private boolean isWhite(Color c) {
        return c.getRed() > 235 && c.getGreen() > 235 && c.getBlue() > 235;
    }

    private double getColorDistance(Color c1, Color c2) {
        return Math.pow(c1.getRed() - c2.getRed(), 2) +
                Math.pow(c1.getGreen() - c2.getGreen(), 2) +
                Math.pow(c1.getBlue() - c2.getBlue(), 2);
    }

    private int findNearestColorIndex(Color target, List<Color> palette) {
        int bestIdx = 0;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < palette.size(); i++) {
            double dist = getColorDistance(target, palette.get(i));
            if (dist < minDist) {
                minDist = dist;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    private int[][] applySymmetryFix(int[][] grid) {
        int size = grid.length;
        int[][] res = new int[size][size];
        for (int y = 0; y < size; y++) System.arraycopy(grid[y], 0, res[y], 0, size);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int mx = size - 1 - x;
                int my = size - 1 - y;
                Map<Integer, Integer> v = new HashMap<>();
                v.put(grid[y][x], v.getOrDefault(grid[y][x], 0) + 1);
                v.put(grid[y][mx], v.getOrDefault(grid[y][mx], 0) + 1);
                v.put(grid[my][x], v.getOrDefault(grid[my][x], 0) + 1);
                v.put(grid[my][mx], v.getOrDefault(grid[my][mx], 0) + 1);
                int best = -1; int max = 0;
                for (Map.Entry<Integer, Integer> e : v.entrySet()) {
                    if (e.getValue() > max) { max = e.getValue(); best = e.getKey(); }
                }
                if (max >= 3) res[y][mx] = res[my][x] = res[my][mx] = res[y][x] = best;
            }
        }
        return res;
    }

    private int[][] forceHorizontalSymmetry(int[][] grid) {
        int size = grid.length;
        int[][] res = new int[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int mx = size - 1 - x;
                if (x < size / 2) {
                    int val = (grid[y][x] != -1) ? grid[y][x] : grid[y][mx];
                    res[y][x] = res[y][mx] = val;
                }
            }
        }
        if (size % 2 == 1) for (int y = 0; y < size; y++) res[y][size/2] = grid[y][size/2];
        return res;
    }

    private BufferedImage downloadImage(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        return ImageIO.read(conn.getInputStream());
    }

    private BufferedImage resizeImage(BufferedImage original, int w, int h) {
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, w, h, null);
        g.dispose();
        return scaled;
    }

    private String generateClues(int[][] grid, boolean isRow) {
        StringBuilder sb = new StringBuilder();
        int outer = isRow ? grid.length : grid[0].length;
        int inner = isRow ? grid[0].length : grid.length;
        for (int i = 0; i < outer; i++) {
            List<String> b = new ArrayList<>();
            int cur = -1; int count = 0;
            for (int j = 0; j < inner; j++) {
                int v = isRow ? grid[i][j] : grid[j][i];
                if (v != -1) {
                    if (v == cur) count++;
                    else { if (count > 0) b.add(count + ":" + (cur + 1)); cur = v; count = 1; }
                } else { if (count > 0) b.add(count + ":" + (cur + 1)); cur = -1; count = 0; }
            }
            if (count > 0) b.add(count + ":" + (cur + 1));
            sb.append(b.isEmpty() ? "empty" : String.join(";", b)).append("|");
        }
        return sb.toString();
    }

    private String serializeGrid(int[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                sb.append(grid[y][x]).append(x < grid[y].length - 1 ? "," : "");
            }
            sb.append(y < grid.length - 1 ? ";" : "");
        }
        return sb.toString();
    }
}