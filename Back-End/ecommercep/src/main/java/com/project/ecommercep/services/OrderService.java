package com.project.ecommercep.services;

import java.util.*;
import java.util.stream.Collectors;

import com.project.ecommercep.entities.Cart;
import com.project.ecommercep.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.ecommercep.entities.Order;
import com.project.ecommercep.entities.PriceHistory;
import com.project.ecommercep.repos.OrderRepository;
import com.project.ecommercep.repos.PriceHistoryRepository;

// Servis sınıfı olarak işaretlenmiş OrderService sınıfı.
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final CartService cartService;
    private final ProductService productService;

    // Bağımlılıkları enjekte eden yapıcı yöntem.
    @Autowired
    public OrderService(OrderRepository orderRepository, PriceHistoryRepository priceHistoryRepository, CartService cartService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.cartService = cartService;
        this.productService = productService;
    }

    // Sipariş oluşturan metod.
    public Order placeOrder(Order order) {
        order.setOrderDate(new Date());
        order = orderRepository.save(order);
        savePriceHistory(order); // Geçmiş fiyat bilgilerini kaydet
        return order;
    }

    // Sepetten sipariş oluşturan metod.
    public Order placeOrderFromCart(Long cartId) {
        Cart cart = cartService.getCartById(cartId);

        Order order = new Order();
        order.setCustomer(cart.getCustomer());
        order.setCart(cart);
        order.setOrderDate(new Date());

        List<String> productNameList = Arrays.stream(cart.getProductNames().split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        order.setQuantity(productNameList.size()-1);

        List<PriceHistory> priceHistories = new ArrayList<>();
        for (String productName : productNameList) {
            if (!productName.equals("null") && !productName.equals("")) {
                Product product = productService.getProductByName(productName);
                System.out.println("");
                if (product.getStock() == 0) {
                    throw new IllegalArgumentException(product.getName() + "'e ait Stoklar Tükendi. Sipariş Oluşturulamadı: ");
                }
                //product.setStock(product.getStock() - 1);

                // Her ürün için bir PriceHistory oluştur
                PriceHistory priceHistory = new PriceHistory();
                priceHistory.setProduct(product);
                priceHistory.setOrder(order);
                priceHistory.setPrice(product.getPrice());
                priceHistories.add(priceHistory);
            }
        }

        // Oluşturulan fiyat geçmişlerini siparişe ekle
        order.setPriceHistory(priceHistories);

        order.setTotalPrice(cart.getTotalPrice());

        // Sepeti temizle
        cart.setProductNames("");
        cart.setProductPrices("");
        cart.setTotalPrice(0.0);

        return orderRepository.save(order);
    }

    // Geçmiş fiyat bilgilerini kaydeden metod.
    private void savePriceHistory(Order order) {
        List<PriceHistory> priceHistoryList = new ArrayList<>();
        for (PriceHistory priceHistory : order.getPriceHistory()) {
            priceHistory.setOrder(order);
            priceHistoryList.add(priceHistory);
        }
        priceHistoryRepository.saveAll(priceHistoryList);
    }

    // Belirli bir siparişi ID'ye göre getiren metod.
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // Tüm siparişleri getiren metod.
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Belirli bir müşteriye ait siparişleri getiren metod.
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomer_Id(customerId);
    }
}