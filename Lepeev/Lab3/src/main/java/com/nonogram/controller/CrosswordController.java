package com.nonogram.controller;

import com.nonogram.entity.Crossword;
import com.nonogram.repository.CrosswordRepository;
import com.nonogram.service.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class CrosswordController {

    private final ImageService imageService;
    private final CrosswordRepository repository;

    public CrosswordController(ImageService imageService, CrosswordRepository repository) {
        this.imageService = imageService;
        this.repository = repository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("crosswords", repository.findAll());
        return "create";
    }

    @PostMapping("/create")
    public String create(@RequestParam String imageUrl,
                         @RequestParam(defaultValue = "15") int size,
                         @RequestParam(defaultValue = "3") int numColors,
                         Model model) {
        try {
            Crossword cw = imageService.generateCrossword(imageUrl, size, numColors);
            repository.save(cw);
            return "redirect:/solve/" + cw.getId();
        } catch (Throwable e) {
            e.printStackTrace();
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("crosswords", repository.findAll());
            return "create";
        }
    }

    @PostMapping("/clear")
    public String clearHistory() {
        repository.deleteAll();
        return "redirect:/";
    }

    @GetMapping("/solve/{id}")
    public String solve(@PathVariable Long id, Model model) {
        Crossword cw = repository.findById(id).orElseThrow();
        model.addAttribute("cw", cw);
        model.addAttribute("palette", Arrays.asList(cw.getPalette().split(",")));
        model.addAttribute("rowClues", parseClues(cw.getRowClues().split("\\|")));
        model.addAttribute("colClues", parseClues(cw.getColClues().split("\\|")));
        return "solve";
    }

    private List<List<String[]>> parseClues(String[] raw) {
        return Arrays.stream(raw).map(line -> {
            if (line.equals("empty")) return List.<String[]>of();
            return Arrays.stream(line.split(";")).map(block -> block.split(":")).collect(java.util.stream.Collectors.toList());
        }).collect(java.util.stream.Collectors.toList());
    }
}