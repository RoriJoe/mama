package com.ganesha.minimarket.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.ganesha.model.Inactivable;
import com.ganesha.model.LogableEntity;

@Entity
@Table(name = "ITEMS")
public class Item extends Inactivable implements LogableEntity {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	@Column(name = "CODE", nullable = false, unique = true)
	private String code;

	@Column(name = "BARCODE")
	private String barcode;

	@Column(name = "MINIMUM_STOCK", nullable = false)
	private Integer minimumStock;

	@Column(name = "UNIT", nullable = false)
	private String unit;

	@Column(name = "SELL_PRICE", nullable = false)
	private BigDecimal sellPrice;

	@OneToMany(mappedBy = "item")
	private List<ItemStock> itemStocks;

	public String getBarcode() {
		return barcode;
	}

	public String getCode() {
		return code;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public List<ItemStock> getItemStocks() {
		return itemStocks;
	}

	public Integer getMinimumStock() {
		return minimumStock;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public String getUnit() {
		return unit;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setItemStocks(List<ItemStock> itemStocks) {
		this.itemStocks = itemStocks;
	}

	public void setMinimumStock(Integer minimumStock) {
		this.minimumStock = minimumStock;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
