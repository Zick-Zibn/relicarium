package ru.relicarium.pledge.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.relicarium.pledge.application.dto.AcceptPledgeCommand;
import ru.relicarium.pledge.application.service.PledgeAcceptanceService;
import ru.relicarium.pledge.domain.enums.PledgeStatus;
import ru.relicarium.pledge.domain.model.Client;
import ru.relicarium.pledge.domain.model.Item;
import ru.relicarium.pledge.domain.model.Pledge;
import ru.relicarium.pledge.persistence.repository.ClientRepository;
import ru.relicarium.pledge.persistence.repository.ItemRepository;
import ru.relicarium.pledge.persistence.repository.PledgeRepository;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class PledgeAcceptanceServiceImpl implements PledgeAcceptanceService {
    private final ClientRepository clientRepository;
    private final ItemRepository   itemRepository;
    private final PledgeRepository pledgeRepository;

    @Override
    @Transactional
    public Pledge acceptPledge(AcceptPledgeCommand acceptPledgeCommand) {

        Client client = clientRepository.findByPhone(acceptPledgeCommand.phone())
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setFullName(acceptPledgeCommand.fullName());
                    newClient.setPhone(acceptPledgeCommand.phone());
                    newClient.setPassportSeries(acceptPledgeCommand.passportSeries());
                    newClient.setPassportNumber(acceptPledgeCommand.passportNumber());
                    return clientRepository.save(newClient);
                });

        Item item = new Item();
        item.setClient(client);
        item.setName(acceptPledgeCommand.itemName());
        item.setDescription(acceptPledgeCommand.description());
        item.setCategory(acceptPledgeCommand.category());
        item.setEstimatedValue(acceptPledgeCommand.estimatedValue());
        itemRepository.save(item);

        Pledge pledge = new Pledge();
        pledge.setClient(client);
        pledge.setItem(item);
        pledge.setLoanAmount(acceptPledgeCommand.loanAmount());
        pledge.setInterestRate(acceptPledgeCommand.interestRate());
        pledge.setTermDays(acceptPledgeCommand.termDays());
        pledge.setStatus(PledgeStatus.ACCEPTED);
        pledge.setAcceptedAt(OffsetDateTime.now());
        OffsetDateTime dueDate = pledge.getAcceptedAt().plusDays(pledge.getTermDays());
        pledge.setDueDate(dueDate.toLocalDate());

        return pledgeRepository.save(pledge);
    }
}
