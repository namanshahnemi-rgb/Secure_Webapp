package edu.deakin.sit738.controller;

import edu.deakin.sit738.service.SafeSqlService;
import edu.deakin.sit738.service.VulnerableSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/")
public class SqlDemoController {

    @Autowired
    private VulnerableSqlService vulnerableSqlService;

    @Autowired
    private SafeSqlService safeSqlService;

    /**
     * Vulnerable page (calls vulnerable service)
     * URL: /vuln/sql?q=...
     */
    @GetMapping("/vuln/sql")
    public String vulnSql(@RequestParam(required = false, name = "q") String q, Model model) {
        if (q == null) q = "";
        model.addAttribute("q", q);
        model.addAttribute("users", vulnerableSqlService.searchUsersVulnerable(q));
        return "vuln/sql"; // your template that lists ${users}
    }

    /**
     * Fixed page (calls safe service)
     * URL: /fix/sql?q=...
     */
    @GetMapping("/fix/sql")
    public String fixSql(@RequestParam(required = false, name = "q") String q, Model model) {
        if (q == null) q = "";
        model.addAttribute("q", q);
        model.addAttribute("users", safeSqlService.searchUsersSafe(q));
        return "fix/sql"; // your safe template that lists ${users}
    }
}
