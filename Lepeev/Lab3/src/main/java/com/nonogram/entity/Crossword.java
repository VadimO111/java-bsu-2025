package com.nonogram.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "crosswords")
public class Crossword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalImageUrl;
    private int width;
    private int height;

    @Column(columnDefinition = "TEXT")
    private String solutionGrid;

    @Column(columnDefinition = "TEXT")
    private String rowClues;

    @Column(columnDefinition = "TEXT")
    private String colClues;

    @Column(columnDefinition = "TEXT")
    private String palette;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalImageUrl() { return originalImageUrl; }
    public void setOriginalImageUrl(String originalImageUrl) { this.originalImageUrl = originalImageUrl; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public String getSolutionGrid() { return solutionGrid; }
    public void setSolutionGrid(String solutionGrid) { this.solutionGrid = solutionGrid; }

    public String getRowClues() { return rowClues; }
    public void setRowClues(String rowClues) { this.rowClues = rowClues; }

    public String getColClues() { return colClues; }
    public void setColClues(String colClues) { this.colClues = colClues; }

    public String getPalette() { return palette; }
    public void setPalette(String palette) { this.palette = palette; }
}