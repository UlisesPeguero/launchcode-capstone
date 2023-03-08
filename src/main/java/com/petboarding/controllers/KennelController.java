package com.petboarding.controllers;

import com.petboarding.models.*;
import com.petboarding.models.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("kennels")
public class KennelController extends AppBaseController{

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private KennelRepository kennelRepository;

    @Autowired
    private StayRepository stayRepository;


    @RequestMapping("")
    public String showKennelMap(Model model){
        Iterable<Kennel> kennels;
        kennels = kennelRepository.findAll();

        Iterable<Stay> stays;
        stays = stayRepository.findAll();

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        this.setActiveModule("kennels", model);
        model.addAttribute("kennels", kennels);
        model.addAttribute("stays", stays);
        model.addAttribute("TODAY", timestamp);
        return "kennels/kennelMap";
    }

    @RequestMapping("/grid")
    public String showKennelGrid(Model model){
        Iterable<Kennel> kennels;
        kennels = kennelRepository.findAll();

        Iterable<Stay> stays;
        stays = stayRepository.findAll();

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        this.setActiveModule("kennels", model);
        model.addAttribute("kennels", kennels);
        model.addAttribute("stays", stays);
        model.addAttribute("TODAY", timestamp);
        return "kennels/kennelGrid";
    }
}
