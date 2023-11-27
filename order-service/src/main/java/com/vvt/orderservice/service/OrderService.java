package com.vvt.orderservice.service;

import com.vvt.orderservice.dto.InventoryResponse;
import com.vvt.orderservice.dto.OrderItemsDto;
import com.vvt.orderservice.dto.OrderRequest;
import com.vvt.orderservice.model.Order;
import com.vvt.orderservice.model.OrderItems;
import com.vvt.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;
    public Boolean placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderItems> orderItemsList = orderRequest.getOrderItemsDtoList()
                .stream()
                .map(orderItemsDto -> mapToOrderItems(orderItemsDto))
                .collect(Collectors.toList());
        order.setOrderItemsList(orderItemsList);

        List<String> skuCodes = order.getOrderItemsList().stream().map(OrderItems::getSkuCode)
                .collect(Collectors.toList());

        // call Inventory service to check product is in stock;
        InventoryResponse[] inventoryResponses = webClient.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("SkuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        orderRepository.save(order);
        // check
        Boolean check = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::getIsInStocks);
        if(check){
            orderRepository.save(order);
            return true;
        }else{
            return false;
        }
    }

    private OrderItems mapToOrderItems(OrderItemsDto orderItemsDto) {
        return OrderItems.builder()
                .price(orderItemsDto.getPrice())
                .quantity(orderItemsDto.getQuantity())
                .skuCode(orderItemsDto.getSkuCode()).build();
    }
}