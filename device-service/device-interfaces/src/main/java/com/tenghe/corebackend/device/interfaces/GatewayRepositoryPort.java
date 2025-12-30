package com.tenghe.corebackend.device.interfaces;

import com.tenghe.corebackend.device.model.Gateway;
import java.util.List;

public interface GatewayRepositoryPort {
    Gateway save(Gateway gateway);

    Gateway update(Gateway gateway);

    Gateway findById(Long id);

    Gateway findBySn(String sn);

    List<Gateway> listAll();

    List<Gateway> searchByNameOrSn(String keyword);
}
