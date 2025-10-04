package edu.deakin.sit738.controller;

import edu.deakin.sit738.entity.User;
import edu.deakin.sit738.repository.UserRepository;
import edu.deakin.sit738.service.VulnerableSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Controller
@RequestMapping("/vuln")
public class VulnController {

    private final UserRepository userRepository;
    private final VulnerableSqlService vulnerableSqlService;

    // Constructor injection (recommended)
    @Autowired
    public VulnController(UserRepository userRepository, VulnerableSqlService vulnerableSqlService) {
        this.userRepository = userRepository;
        this.vulnerableSqlService = vulnerableSqlService;
    }

    // XSS vulnerable: prints unescaped user input
    @GetMapping("/xss")
    public String xss(@RequestParam(required = false) String msg, Model model) {
        model.addAttribute("msg", msg);
        return "vuln/xss";
    }

    // SQL listing (demonstration) — now calls the vulnerable service
    @GetMapping("/sql")
    public String sqlSearch(@RequestParam(required = false) String q, Model model) {
        if (q == null) q = "";
        List<String> rows = vulnerableSqlService.searchUsersVulnerable(q);
        model.addAttribute("q", q);
        model.addAttribute("users", rows);
        return "vuln/sql";
    }

    // Unrestricted file upload: saves original filename (vulnerable)
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file == null || file.isEmpty()) {
            model.addAttribute("msg", "no file");
            return "vuln/upload";
        }
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        Path target = Paths.get("src/main/resources/static/uploads").resolve(filename);
        Files.createDirectories(target.getParent());
        Files.write(target, file.getBytes());
        model.addAttribute("msg", "Saved to " + target.toString());
        return "vuln/upload";
    }

    @GetMapping("/upload")
    public String uploadForm() {
        return "vuln/upload";
    }

    // CSRF vulnerable form (no server-side CSRF validation here)
    @GetMapping("/csrf")
    public String csrfForm() {
        return "vuln/csrf";
    }

    @PostMapping("/csrf")
    public String csrfSubmit(@RequestParam String email, Model model) {
        model.addAttribute("msg", "Email updated to: " + email);
        return "vuln/csrf";
    }

    // SSRF vulnerable: fetch arbitrary URL
    @GetMapping("/ssrf")
    public String ssrfForm() {
        return "vuln/ssrf";
    }

    @PostMapping("/ssrf")
    public String ssrfFetch(@RequestParam String url, Model model) {
        RestTemplate rt = new RestTemplate();
        try {
            String body = rt.getForObject(url, String.class);
            model.addAttribute("result", body != null ? body.substring(0, Math.min(800, body.length())) : "no body");
        } catch (Exception e) {
            model.addAttribute("result", "Error: " + e.getMessage());
        }
        return "vuln/ssrf";
    }
}
