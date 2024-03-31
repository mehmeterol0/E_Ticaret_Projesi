package com.project.ecommercep.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

//Veritabanındaki "cart" tablosunu temsil eden Cart sınıfı.
@Entity
@Table(name = "cart")
@Data
public class Cart extends BaseEntity {

	//Cart sınıfının bir müşteriye ait olduğunu belirten ilişki.
	@ManyToOne
	@JoinColumn(name = "customer_id")
	Customer customer;

	//Bir sepetin birden çok siparişi olabilir.
	@OneToMany(mappedBy = "cart")
	@JsonIgnore
	List<Order> orders;

	//Bir sepetin birden çok ürünü olabilir.
	@OneToMany(mappedBy = "cart")
	@JsonIgnore
	List<Product> products;

	//Toplam fiyat bilgisini saklayan sütun.
	@Column(name = "total_price", columnDefinition = "DOUBLE DEFAULT 0.0")
	private Double totalPrice;

	//Sepetteki ürün isimlerini saklayan sütun.
	@Column(name = "product_names", columnDefinition = "VARCHAR(255) DEFAULT ' '")
	private String productNames;

	//Sepetteki ürün fiyatlarını saklayan sütun.
	@Column(name = "product_prices", columnDefinition = "VARCHAR(255) DEFAULT ' '")
	private String productPrices;

	//toStringi ezmemdeki amaç bazı sorgularda JSON çok uzun geliyordu gerekli olanları aldım.
	@Override
	public String toString() {
		return "Cart{" +
				"id=" + getId() +
				", customer=" + customer +
				", totalPrice=" + totalPrice +
				", productNames=" + productNames +
				'}';
	}

	//Getter ve Setterlar..
	public String getProductIds() {
		return productNames;
	}

	public void setProductIds(String productNames) {
		this.productNames = productNames;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}


	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getProductPrices() {
		return productPrices;
	}

	public void setProductPrices(String productPrices) {
		this.productPrices = productPrices;
	}

	public String getProductNames() {
		return productNames;
	}

	public void setProductNames(String productNames) {
		this.productNames = productNames;
	}
}