package ru.relicarium.auction.application.service;

import ru.relicarium.auction.application.dto.CompleteLotCommand;
import ru.relicarium.auction.domain.model.Lot;

public interface LotCompletionService {

    Lot complete(CompleteLotCommand command);
}
