package edu.deakin.sit738.controller;

import edu.deakin.sit738.entity.User;
import edu.deakin.sit738.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/fix")
public class FixController {

    @Autowired private UserRepository userRepository;

    // XSS fix (escaped output in template)
    @GetMapping("/xss")
    public String xss(@RequestParam(required=false) String msg, Model model) {
        model.addAttribute("msg", msg);
        return "fix/xss";
    }

    // SQLi mitigation: using repository methods (example)
    @GetMapping("/sql")
    public String sqlSearch(@RequestParam(required=false) String q, Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "fix/sql";
    }

    // File upload fix: validate and store with UUID prefix
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if(file==null || file.isEmpty()){
            model.addAttribute("msg","no file");
            return "fix/upload";
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        if(original.contains("..") || !original.contains(".")){
            model.addAttribute("msg","Invalid filename");
            return "fix/upload";
        }
        String safeName = UUID.randomUUID().toString() + "-" + original.replaceAll("[^A-Za-z0-9_.-]", "_");
        Path target = Paths.get("src/main/resources/static/uploads").resolve(safeName);
        Files.createDirectories(target.getParent());
        Files.write(target, file.getBytes());
        model.addAttribute("msg","Saved as " + safeName);
        return "fix/upload";
    }
    @GetMapping("/upload") public String uploadForm(){ return "fix/upload"; }

    // CSRF fix: rely on Spring Security CSRF tokens in the template
    @GetMapping("/csrf")
    public String csrfForm(){ return "fix/csrf"; }

    @PostMapping("/csrf")
    public String csrfSubmit(@RequestParam String email, Model model){
        model.addAttribute("msg","Email updated to: " + email);
        return "fix/csrf";
    }

    // SSRF fix: whitelist hosts (basic demo)
    @GetMapping("/ssrf")
    public String ssrfForm(){ return "fix/ssrf"; }

    @PostMapping("/ssrf")
    public String ssrfFetch(@RequestParam String url, Model model){
        try {
            URI u = new URI(url);
            String host = u.getHost();
            if(host==null || !(host.endsWith("example.com") || host.endsWith("httpbin.org"))){
                model.addAttribute("result","Blocked host: " + host);
                return "fix/ssrf";
            }
            RestTemplate rt = new RestTemplate();
            String body = rt.getForObject(url, String.class);
            model.addAttribute("result", body != null ? body.substring(0, Math.min(800, body.length())) : "no body");
        } catch(URISyntaxException e){
            model.addAttribute("result","Invalid URL");
        } catch(Exception e){
            model.addAttribute("result","Error: "+e.getMessage());
        }
        return "fix/ssrf";
    }
}
