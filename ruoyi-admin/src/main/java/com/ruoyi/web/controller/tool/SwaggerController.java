package com.ruoyi.web.controller.tool;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SwaggerController {

    @GetMapping("/v3/api-docs/swagger-config")
    public String configForward() {
        return "forward:/dev-api/v3/api-docs/swagger-config";
    }

    @GetMapping("/v3/api-docs/{group}")
    public String groupForward(@PathVariable String group) {
        return "forward:/dev-api/v3/api-docs/" + group;
    }

}
