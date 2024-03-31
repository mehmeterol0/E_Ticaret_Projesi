package com.project.ecommercep.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.ecommercep.entities.Cart;
import com.project.ecommercep.services.CartService;

//CORS izinleri tanımladım (Securityi kaldırdım spring başlarken bunu da yazmam gerekli). CartController sınıfı.
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    //Dependenciyleri enjekte ettiğim constructor metodum
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    //Belirli bir müşteri için yeni bir sepet oluşturan endpoint.
    @PostMapping("/create/{customerId}")
    public ResponseEntity<Cart> createCartForCustomer(@PathVariable Long customerId) {
        Cart createdCart = cartService.createCartForCustomer(customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCart);
    }

    //Belirli bir sepeti ID'ye göre getiren endpoint.
    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        Cart cart = cartService.getCartById(id);
        return ResponseEntity.ok().body(cart);
    }

    //Sepeti güncelleyen endpoint.
    @PutMapping("/update")
    public ResponseEntity<Cart> updateCart(@RequestBody Cart cart) {
        Cart updatedCart = cartService.updateCart(cart);
        return ResponseEntity.ok().body(updatedCart);
    }

    //Sepete ürün ekleyen endpoint.
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/{cartId}/products/{productId}")
    public ResponseEntity<Cart> addProductToCart(@PathVariable Long cartId, @PathVariable Long productId) {
        Cart updatedCart = cartService.addProductToCart(cartId, productId);
        if (updatedCart != null) {
            return ResponseEntity.ok().body(updatedCart);
        } else {
            return ResponseEntity.notFound().build();
            //Sepet bulunamadı veya ürün eklenemediği için 404 dönülebilir.
        }
    }

    //Sepetten ürün çıkaran endpoint.
    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/{cartId}/products/{productId}")
    public ResponseEntity<Cart> removeProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        Cart updatedCart = cartService.removeProductFromCart(cartId, productId);
        return ResponseEntity.ok().body(updatedCart);
    }

    //Sepeti boşaltan endpoint.
    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/{id}/empty")
    public ResponseEntity<Void> emptyCart(@PathVariable Long id) {
        cartService.emptyCart(id);
        return ResponseEntity.noContent().build();
    }
}
