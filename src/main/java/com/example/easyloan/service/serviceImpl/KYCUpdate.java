package com.example.easyloan.service.serviceImpl;


import com.example.easyloan.dto.BorrowerKYCDto;
import com.example.easyloan.dto.KYCUpdateResponseDto;
import com.example.easyloan.dto.LenderKYCDto;
import com.example.easyloan.exception.UserNotFoundException;
import com.example.easyloan.model.*;
import com.example.easyloan.repository.*;
import com.example.easyloan.service.KYCUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class KYCUpdate implements KYCUpdateService {
    private final ContactInfoRepository contactInfoRepository;
    private final UserRepository userRepository;
    private final LenderRepository lenderRepository;
    private final GovernmentIDRepository governmentIDRepository;
    private  final BankAccountRepository bankAccountRepository;
    private final BorrowerRepository borrowerRepository;
    @Override
    public KYCUpdateResponseDto updateLenderKYC(LenderKYCDto lenderKYCDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user =  (User) authentication.getPrincipal();
        KYCUpdateResponseDto kycUpdateResponseDto =  new KYCUpdateResponseDto();
        switch (lenderKYCDto.getProgress()){
            case 0:
                //CHECK IF CONTACT INFO EXITS
                ContactInfo contactInfo;
                Optional<ContactInfo> contactExists =  contactInfoRepository.findByUser(user);
                if(contactExists.isPresent()){
                    contactInfo=contactExists.get();
                }else{
                    contactInfo = new ContactInfo();
                    contactInfo.setUser(user);
                }
                contactInfo.setPhoneNumber(lenderKYCDto.getPhoneNumber());
                updateUserKYCStatus(user,1);
                kycUpdateResponseDto.setProgress(1);
                contactInfoRepository.save(contactInfo);
                break;
            case 1:

                //CHECK IF LENDER EXISTS
                Lender lender;
                Optional<Lender> lenderExists = lenderRepository.findByUser(user);
                if(lenderExists.isPresent()){
                    lender=lenderExists.get();
                }else{
                    lender=new Lender();
                    lender.setUser(user);
                }
                lender.setLoanType(lenderKYCDto.getLoanType());
                lender.setDocumentRequired(lenderKYCDto.getDocumentRequired());
                lender.setLoanFee(lenderKYCDto.isLoanFee());
                lender.setLoanDecisionDuration(lenderKYCDto.getLoanDecisionDuration());
                lenderRepository.save(lender);
                updateUserKYCStatus(user,2);
                kycUpdateResponseDto.setProgress(2);
                break;
            case 2:
                //CHECK IF GOVERNMENT ID EXISTS
                GovernmentID governmentID;
                Optional<GovernmentID> governmentIDExists= governmentIDRepository.findByUser(user);
                if(governmentIDExists.isPresent()){
                    governmentID=governmentIDExists.get();
                }else{
                    governmentID = new GovernmentID();
                    governmentID.setUser(user);
                }
                governmentID.setDocType(lenderKYCDto.getDocType());
                governmentID.setDocNumber(lenderKYCDto.getDocNumber());
                governmentID.setFileName(lenderKYCDto.getFileNameGovId());
                governmentIDRepository.save(governmentID);
                updateUserKYCStatus(user,3);
                kycUpdateResponseDto.setProgress(3);
                break;
            case 3:
                Optional lender1 = lenderRepository.findByUser(user);
                if (lender1.isPresent()){
                    Lender lender2 = (Lender) lender1.get();
                    lender2.setLoanRiskStatus(lenderKYCDto.getLoanRiskStatus());
                    lenderRepository.save(lender2);
                    updateUserKYCStatus(user,4);
                    kycUpdateResponseDto.setProgress(4);
                    break;
                }else {
                    throw  new UserNotFoundException("Lender record not found", HttpStatus.NOT_FOUND);
                }
            case 4:
                Optional contactInfo1 = contactInfoRepository.findByUser(user);
                if(contactInfo1.isEmpty()){
                    throw  new UserNotFoundException("User contact information not found",HttpStatus.NOT_FOUND);
                }
                ContactInfo contactInfo2 = (ContactInfo) contactInfo1.get();
                contactInfo2.setFileName(lenderKYCDto.getFileNameAdd());
                contactInfoRepository.save(contactInfo2);
                updateUserKYCStatus(user,5);
                kycUpdateResponseDto.setProgress(5);
                break;
            case 5:
                BankAccount bankAccount = new BankAccount();
                bankAccount.setBank(lenderKYCDto.getBank());
                bankAccount.setAccountName(lenderKYCDto.getBank());
                bankAccount.setAccountNumber(lenderKYCDto.getAccountNumber());
                bankAccount.setUser(user);
                bankAccountRepository.save(bankAccount);
                updateUserKYCStatus(user,6);
                kycUpdateResponseDto.setProgress(6);
                break;
        }
        return kycUpdateResponseDto;
    }

    @Override
    public KYCUpdateResponseDto updateBorrowerKyc(BorrowerKYCDto borrowerKYCDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user =  (User) authentication.getPrincipal();
        KYCUpdateResponseDto kycUpdateResponseDto = new KYCUpdateResponseDto();
        switch (borrowerKYCDto.getProgress()) {
            case 0 -> {
                ContactInfo contactInfo = new ContactInfo();
                contactInfo.setPhoneNumber(borrowerKYCDto.getPhoneNumber());
                contactInfo.setUser(user);
                contactInfoRepository.save(contactInfo);
                user.setAccountStatus(1);
                userRepository.save(user);
                updateUserKYCStatus(user,1);
                kycUpdateResponseDto.setProgress(1);
            }
            case 1 -> {
                Borrower borrower = new Borrower();
                borrower.setUser(user);
                borrower.setEmployedBefore(borrowerKYCDto.getEmployedBefore());
                borrower.setEmploymentStatus(borrowerKYCDto.getEmploymentStatus());
                borrower.setEarning(borrowerKYCDto.getEarning());
                borrower.setWorkType(borrowerKYCDto.getWorkType());
                borrowerRepository.save(borrower);
                updateUserKYCStatus(user,2);
                kycUpdateResponseDto.setProgress(2);
            }
            case 2 -> {
                GovernmentID governmentID = new GovernmentID();
                governmentID.setUser(user);
                governmentID.setDocType(borrowerKYCDto.getDocType());
                governmentID.setDocNumber(borrowerKYCDto.getDocNumber());
                governmentID.setFileName(borrowerKYCDto.getFileNameGov());
                governmentIDRepository.save(governmentID);
                updateUserKYCStatus(user,3);
                kycUpdateResponseDto.setProgress(3);
            }
            case 3 -> {
                Optional<?> borrowerOp = borrowerRepository.findByUser(user);
                if (borrowerOp.isEmpty()){
                    throw new UserNotFoundException("Borrower record not found",HttpStatus.NOT_FOUND);
                } else {
                    Borrower borrower = (Borrower) borrowerOp.get();
                    borrower.setEmploymentStatus(borrowerKYCDto.getEmploymentStatus());
                    borrower.setOtherIncomeSource(borrowerKYCDto.getOtherIncomeSource());
                    borrower.setMonthlyPersonalIncome(borrowerKYCDto.getMonthlyPersonalIncome());
                    borrower.setExtraIncomeDescription(borrowerKYCDto.getExtraIncomeDescription());
                    borrowerRepository.save(borrower);
                    updateUserKYCStatus(user,4);
                    kycUpdateResponseDto.setProgress(4);
                }
            }
            case 4 -> {
                Optional<?> contactInfoOp = contactInfoRepository.findByUser(user);
                if(contactInfoOp.isEmpty()){
                    throw  new UserNotFoundException("User contact information not found",HttpStatus.NOT_FOUND);
                }
                ContactInfo contactInfo = (ContactInfo) contactInfoOp.get();
                contactInfo.setFileName(borrowerKYCDto.getFileNameAdd());
                contactInfoRepository.save(contactInfo);
                updateUserKYCStatus(user,5);
                kycUpdateResponseDto.setProgress(5);
            }
            case 5 -> {
                BankAccount bankAccount = new BankAccount();
                bankAccount.setBank(borrowerKYCDto.getBank());
                bankAccount.setAccountNumber(borrowerKYCDto.getAccountNumber());
                bankAccount.setAccountName(borrowerKYCDto.getAccountName());
                bankAccount.setUser(user);
                bankAccountRepository.save(bankAccount);
                updateUserKYCStatus(user,6);
                kycUpdateResponseDto.setProgress(6);

            }
        }
        return kycUpdateResponseDto;

    }

    private void updateUserKYCStatus(User user, Integer accountStatus ){
        user.setAccountStatus(accountStatus);
        userRepository.save(user);
    }
}
