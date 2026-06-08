package pk.ni.pasir_piotrkowski_michal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import java.util.Map;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public String test(){
        return "Hello world!";
    }

    @GetMapping(value = "/api/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> info(){
        return Map.of(
                "appName", "Aplikacja Budżetowa",
                "version", "1.0",
                "message", "Witaj w aplikacji budżetowej stworzonej w Spring Boot!"
        );
    }
}
