package com.example.caching.product.controller;

import com.example.caching.product.dto.CreateProductRequest;
import com.example.caching.product.dto.ProductResponse;
import com.example.caching.product.model.PurchaseStatus;
import com.example.caching.product.service.AlertService;
import com.example.caching.product.service.ProductHistoryService;
import com.example.caching.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui/products")
public class ProductViewController {

    private final ProductService productService;
    private final ProductHistoryService productHistoryService;
    private final AlertService alertService;

    @GetMapping
    public String listProducts(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String stockStatus,
                               Model model) {
        model.addAttribute("products", productService.getAll(name, stockStatus));
        model.addAttribute("statistics", productService.getStatistics());
        model.addAttribute("nameFilter", name);
        model.addAttribute("stockStatusFilter", stockStatus);
        return "products/list";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.get(id));
        model.addAttribute("history", productHistoryService.getHistory(id));
        return "products/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new CreateProductRequest("", null, null));
        return "products/create";
    }

    @PostMapping("/new")
    public String createProduct(@RequestParam String name,
                                @RequestParam Double price,
                                @RequestParam Integer quantity,
                                RedirectAttributes redirectAttributes) {
        try {
            ProductResponse created = productService.create(new CreateProductRequest(name, price, quantity));
            redirectAttributes.addFlashAttribute("success", "Product '" + created.name() + "' created successfully!");
            return "redirect:/ui/products/" + created.id();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/ui/products/new";
        }
    }

    @PostMapping("/{id}/name")
    public String updateName(@PathVariable Long id,
                             @RequestParam String name,
                             RedirectAttributes redirectAttributes) {
        try {
            productService.updateName(id, name);
            redirectAttributes.addFlashAttribute("success", "Name updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/products/" + id;
    }

    @PostMapping("/{id}/price")
    public String updatePrice(@PathVariable Long id,
                              @RequestParam Double price,
                              RedirectAttributes redirectAttributes) {
        try {
            productService.updatePrice(id, price);
            redirectAttributes.addFlashAttribute("success", "Price updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/products/" + id;
    }

    @PostMapping("/{id}/purchase")
    public String purchase(@PathVariable Long id,
                           @RequestParam int amount,
                           RedirectAttributes redirectAttributes) {
        try {
            productService.reduceQuantity(id, amount);
            redirectAttributes.addFlashAttribute("success", "Purchased " + amount + " units!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/products/" + id;
    }

    @PostMapping("/{id}/restock")
    public String restock(@PathVariable Long id,
                          @RequestParam int amount,
                          RedirectAttributes redirectAttributes) {
        try {
            productService.extendQuantity(id, amount);
            redirectAttributes.addFlashAttribute("success", "Restocked " + amount + " units!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/products/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
        return "redirect:/ui/products";
    }

    @GetMapping("/history")
    public String history(@RequestParam(required = false) String status, Model model) {
        PurchaseStatus purchaseStatus = null;
        if (status != null && !status.isBlank()) {
            purchaseStatus = PurchaseStatus.valueOf(status);
        }
        model.addAttribute("historyList", productHistoryService.getAll(purchaseStatus));
        model.addAttribute("statuses", PurchaseStatus.values());
        model.addAttribute("selectedStatus", status);
        return "products/history";
    }

    @GetMapping("/alerts")
    public String alerts(Model model) {
        model.addAttribute("alerts", alertService.getAll());
        return "products/alerts";
    }
}
