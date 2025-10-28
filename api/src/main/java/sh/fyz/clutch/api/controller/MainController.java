package sh.fyz.clutch.api.controller;

import sh.fyz.clutch.api.utils.VersionHelper;
import sh.fyz.fiber.annotations.request.Controller;
import sh.fyz.fiber.annotations.request.RequestMapping;
import sh.fyz.fiber.annotations.security.NoCors;
import sh.fyz.fiber.core.ResponseEntity;

@Controller()
public class MainController {

    @NoCors
    @RequestMapping("/ping")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("freshperf.fr - " + VersionHelper.getVersion()+" made with so much <3");
    }

}
