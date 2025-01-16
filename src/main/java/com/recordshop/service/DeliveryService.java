package com.recordshop.service;

import com.recordshop.entity.Delivery;
import com.recordshop.entity.Order;
import com.recordshop.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Delivery createDelivery(String nickName, String phoneNumber, String address, Order order) {
        Delivery delivery = new Delivery();
        delivery.setNickName(nickName);
        delivery.setPhoneNumber(phoneNumber);
        delivery.setAddress(address);
        delivery.setOrder(order);
        return deliveryRepository.save(delivery);
    }


}
