package com.example.easyloan.service.serviceImpl;

import com.example.easyloan.api.getBank.Bankaccountverification;
import com.example.easyloan.api.getBank.Datum;
import com.example.easyloan.model.Bank;
import com.example.easyloan.repository.BankRepository;
import com.example.easyloan.service.DatabaseSeedService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class DatabaseSeedServiceImpl implements DatabaseSeedService {

    private final BankRepository bankRepository;


    public DatabaseSeedServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    private static final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.paystack.co")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer sk_test_29d0c02151f8e2f3c0b99976ca2e78cc8cb0f03c")
            .build();


    public ResponseEntity<?> seedBanks() {
        List<Bank> allBanks =bankRepository.findAll();
        if(allBanks.size() == 0){
            try{
                Bankaccountverification verify = webClient.get()
                        .uri("/bank")
                        .retrieve()
                        .bodyToMono(Bankaccountverification.class)
                        .block();
                if (verify != null) {
                    List<Datum> bankList = verify.getData();
                    for (Datum data : bankList) {
                        Bank bank = Bank.builder()
                                .country(data.getCountry())
                                .active(data.getActive())
                                .isDeleted(data.getIsDeleted())
                                .name(data.getName())
                                .code(data.getCode())
                                .currency(data.getCurrency())
                                .build();
                        bankRepository.save(bank);

                    }
                }

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.ok(allBanks);
    }
}
