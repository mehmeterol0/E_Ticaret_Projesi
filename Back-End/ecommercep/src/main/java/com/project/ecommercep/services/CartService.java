package com.project.ecommercep.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.ecommercep.entities.Cart;
import com.project.ecommercep.entities.Customer;
import com.project.ecommercep.entities.Product;
import com.project.ecommercep.repos.CartRepository;
import com.project.ecommercep.repos.CustomerRepository;
import com.project.ecommercep.repos.ProductRepository;

import java.util.List;
import java.util.regex.Pattern;

//Servis sınıfı anatosyonu olarak işaretledim.
@Service
public class CartService {
	private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    //Dependenciyleri enjekte ettiğim constructor metodum
    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository, CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    //Belirli bir müşteri için yeni bir sepet oluşturan metodum.
    public Cart createCartForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı: " + customerId));
        Cart cart = new Cart();
        cart.setCustomer(customer);
        return cartRepository.save(cart);
    }

    //Belirli bir id'ye göre sepeti getirir.
    public Cart getCartById(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + id));
    }

    //Sepeti güncelleyen metodum.
    public Cart updateCart(Cart cart) {
        return cartRepository.save(cart);
    }

    //sepete ürünleri ekleyen metodum.
    public Cart addProductToCart(Long cartId, Long productId) {
        Cart cart = getCartById(cartId);
        Product product;
        try {
            product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("İlgili ürün ID'si bulunamadı: " + productId));
        } catch (IllegalArgumentException ex) {
            throw ex;
        }

        if (product.getStock() == 0) {
            throw new IllegalArgumentException(product.getName() + "'e ait Stoklar Tükendi. Sepete Eklenemedi ");
        }

        try {
            cart.setProductNames(cart.getProductNames() + "," + product.getName());
            cart.setProductPrices(cart.getProductPrices() + "," + product.getPrice());
            cart.getProducts().add(product);
            updateCart(cart);
            double total = cart.getTotalPrice() + product.getPrice();
            cart.setTotalPrice(total); // Total price'i güncelledim.
        } catch (Exception ex) {
            throw ex;
        }

        return cartRepository.save(cart);
    }

    //Sepetten ürünleri kaldıran metodum.
    public Cart removeProductFromCart(Long cartId, Long productId) {
        Cart cart = getCartById(cartId);
        Product productToRemove = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("İlgili ürün idsi bulunamadı: " + productId));
        // Ürün listesinden sadece bir tane ürünü kaldır
        List<Product> products = cart.getProducts();
        boolean found = false;
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (product.getId().equals(productId) && !found) {
                products.remove(i);
                found = true;
            }
        }
        String productNames = cart.getProductNames();
        String productPrices = cart.getProductPrices();
        // Silinecek ürünün adını ve fiyatını al
        String productNameToRemove = productToRemove.getName();
        String productPriceToRemove = String.valueOf(productToRemove.getPrice());
        // Güncel ürün adı ve fiatını listelerden çıkar
        productNames = productNames.replaceFirst("\\b" + Pattern.quote(productNameToRemove) + "\\b", "").replaceAll("^,|,$", "").replaceAll(",{2,}", ",");
        productPrices = productPrices.replaceFirst("\\b" + Pattern.quote(productPriceToRemove) + "\\b", "").replaceAll("^,|,$", "").replaceAll(",{2,}", ",");
        // Güncellenmiş ürün adı ve fiyatını sepete set et
        cart.setProductNames(productNames);
        cart.setProductPrices(productPrices);
        double total = cart.getTotalPrice() - productToRemove.getPrice();
        cart.setTotalPrice(total);
        return cartRepository.save(cart);
    }

    //sepeti tamamen boşaltır yani sepetteki tüm ürünleri kaldırır.
    public void emptyCart(Long id) {
        Cart cart = getCartById(id);
        cart.getProducts().clear();
        cart.setTotalPrice(0.0);
        cart.setProductNames("");
        cart.setProductPrices("");
        cartRepository.save(cart);
    }
}
