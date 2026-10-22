package com.campus.express.controller;

import com.campus.express.service.CompanyService;
import com.campus.express.service.ShelfService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 页面路由控制器
 */
@Controller
public class PageController {

    @Resource
    private CompanyService companyService;

    @Resource
    private ShelfService shelfService;

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/package/in")
    public String packageIn(Model model) {
        model.addAttribute("companies", companyService.listActive());
        model.addAttribute("shelves", shelfService.listActive());
        return "package/in";
    }

    @GetMapping("/package/list")
    public String packageList(Model model) {
        model.addAttribute("companies", companyService.listActive());
        return "package/list";
    }

    @GetMapping("/package/pickup")
    public String packagePickup() {
        return "package/pickup";
    }

    @GetMapping("/package/detail/{id}")
    public String packageDetail(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "package/detail";
    }

    @GetMapping("/problem/list")
    public String problemList() {
        return "problem/list";
    }

    @GetMapping("/basic/company")
    public String company() {
        return "basic/company";
    }

    @GetMapping("/basic/shelf")
    public String shelf() {
        return "basic/shelf";
    }

    @GetMapping("/system/user")
    public String user() {
        return "system/user";
    }

    @GetMapping("/system/operlog")
    public String operlog() {
        return "system/operlog";
    }

    @GetMapping("/system/loginlog")
    public String loginlog() {
        return "system/loginlog";
    }

    @GetMapping("/stat")
    public String stat() {
        return "stat/index";
    }

    @GetMapping("/screen")
    public String screen() {
        return "screen/index";
    }

    @GetMapping("/order/send")
    public String sendOrder() {
        return "order/send";
    }

    @GetMapping("/order/deposit")
    public String depositOrder() {
        return "order/deposit";
    }

    // ===================== 用户端（学生）页面 =====================

    @GetMapping("/student")
    public String student() {
        return "student/index";
    }

    @GetMapping("/student/query")
    public String studentQuery() {
        return "student/query";
    }

    @GetMapping("/student/login")
    public String studentLogin() {
        return "student/login";
    }

    @GetMapping("/student/register")
    public String studentRegister() {
        return "student/register";
    }

    @GetMapping("/student/center")
    public String studentCenter() {
        return "student/center";
    }
}
