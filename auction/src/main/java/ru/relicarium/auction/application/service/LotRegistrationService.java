package ru.relicarium.auction.application.service;

import ru.relicarium.auction.application.dto.CreateLotCommand;
import ru.relicarium.auction.domain.model.Lot;

public interface LotRegistrationService {

    Lot register(CreateLotCommand command);
}
