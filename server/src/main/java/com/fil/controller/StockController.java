package com.fil.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fil.dto.AddToInventory;
import com.fil.exceptions.InvalidFieldException;
import com.fil.market.StockMarket;
import com.fil.model.Ticker;
import com.fil.service.TickerService;

@RestController
@RequestMapping("/api/stock")
public class StockController {

	@Autowired
	private StockMarket marketAPI;

	@Autowired
	private TickerService tickerService;

	@GetMapping()
	public ResponseEntity<?> search(@RequestParam(name = "search", required = true) String search) {
		if (search.isEmpty() || search.length() == 0) {
			throw new InvalidFieldException();
		}
		List<Ticker> result = marketAPI.search(search);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		map.put("data", result);
		return ResponseEntity.status(HttpStatus.OK).body(map);

	}

	@PostMapping("/add-to-inventory")
	public ResponseEntity<?> addToInventory(@Valid @RequestBody AddToInventory data) {
		Ticker ticker = tickerService.addTicker(data.toTicker());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		map.put("data", ticker);
		return ResponseEntity.status(HttpStatus.CREATED).body(map);

	}

}