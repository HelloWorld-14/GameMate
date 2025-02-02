package com.example.gamemate.domain.pay.service;

import com.example.gamemate.domain.pay.dto.PaymentRequest;
import com.example.gamemate.domain.pay.dto.PaymentResponse;
import com.example.gamemate.domain.pay.dto.WebhookRequest;
import com.example.gamemate.domain.pay.entity.Payment;
import com.example.gamemate.domain.pay.enums.PaymentStatus;
import com.example.gamemate.domain.pay.repository.PaymentRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.Prepare;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private  PaymentRepository paymentRepository;
    private  IamportClient client;

    @Value("${portone.imp-key}")
    private String impKey;

    @Value("${portone.imp-secret}")
    private String impSecret;

    @Transactional
    public PaymentResponse requestPayment(PaymentRequest request) {
        String merchantUid = "order_" + UUID.randomUUID().toString();

        try {
            BigDecimal amount = request.getAmount();
            PrepareData prepareData = new PrepareData(merchantUid, amount);

            IamportResponse<Prepare> prepareResponse = client.postPrepare(prepareData);

            Payment payment = Payment.builder()
                    .merchantUid(merchantUid)
                    .amount(request.getAmount())
                    .buyerEmail(request.getBuyerEmail())
                    .status(PaymentStatus.READY)
                    .build();

            return paymentRepository.save(payment).toResponse();
        } catch (IamportResponseException | IOException e) {
            log.error("Error preparing payment", e);
            //  throw new PaymentCreationException("Failed to prepare payment", e);
            throw new ApiException(ErrorCode.GAME_NOT_FOUND);
        }
    }

    @Transactional
    public void verifyPayment(String impUid) throws IamportResponseException, IOException {
        IamportResponse<com.siot.IamportRestClient.response.Payment> portonePayment = client.paymentByImpUid(impUid);

        Payment payment = paymentRepository.findByMerchantUid(portonePayment.getResponse().getMerchantUid())
                .orElseThrow(() ->
                        //new PaymentNotFoundException("Payment not found: " + portonePayment.getResponse().getMerchantUid()));
                new ApiException(ErrorCode.GAME_NOT_FOUND));
        if (!portonePayment.getResponse().getAmount().equals(payment.getAmount())) {
            //throw new AmountMismatchException("Amount mismatch for payment: " + impUid);
            throw new ApiException(ErrorCode.GAME_NOT_FOUND);
        }

        Payment updatedPayment = Payment.builder()
                .id(payment.getId())
                .merchantUid(payment.getMerchantUid())
                .impUid(impUid)
                .amount(payment.getAmount())
                .buyerEmail(payment.getBuyerEmail())
                .status(PaymentStatus.PAID)
                .build();

        paymentRepository.save(updatedPayment);
    }

    @Transactional
    public void processWebhook(WebhookRequest webhookRequest) throws IamportResponseException, IOException {
        String impUid = webhookRequest.getImp_uid();
        String merchantUid = webhookRequest.getMerchant_uid();
        String status = webhookRequest.getStatus();

        IamportResponse<com.siot.IamportRestClient.response.Payment> portonePayment = client.paymentByImpUid(impUid);

        Payment payment = paymentRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() ->
                        //new PaymentNotFoundException("Payment not found: " + merchantUid));
                        new ApiException(ErrorCode.GAME_NOT_FOUND));
        PaymentStatus newStatus;
        switch(status) {
            case "paid":
                if (!portonePayment.getResponse().getAmount().equals(payment.getAmount())) {
                    //throw new AmountMismatchException("Amount mismatch for payment: " + merchantUid);
                    new ApiException(ErrorCode.GAME_NOT_FOUND);
                }
                newStatus = PaymentStatus.PAID;
                break;
            case "cancelled":
                newStatus = PaymentStatus.CANCELLED;
                break;
            case "failed":
                newStatus = PaymentStatus.FAILED;
                break;
            default:
                log.warn("Unhandled payment status: {}", status);
                return;
        }

        Payment updatedPayment = Payment.builder()
                .id(payment.getId())
                .merchantUid(payment.getMerchantUid())
                .impUid(impUid)
                .amount(payment.getAmount())
                .buyerEmail(payment.getBuyerEmail())
                .status(newStatus)
                .build();

        paymentRepository.save(updatedPayment);
    }

    public List<PaymentResponse> getUserPayments(String email) {
        return paymentRepository.findByBuyerEmail(email).stream()
                .map(Payment::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponse cancelPayment(String impUid) throws IamportResponseException, IOException {
        Payment payment = paymentRepository.findByImpUid(impUid)
                .orElseThrow(() ->
                        //new PaymentNotFoundException("Payment not found: " + impUid));
                        new ApiException(ErrorCode.GAME_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("Payment is not in PAID status");
        }

        CancelData cancelData = new CancelData(impUid, true); // 전체 취소
        IamportResponse<com.siot.IamportRestClient.response.Payment> response = client.cancelPaymentByImpUid(cancelData);

        Payment cancelledPayment = Payment.builder()
                .id(payment.getId())
                .merchantUid(payment.getMerchantUid())
                .impUid(payment.getImpUid())
                .amount(payment.getAmount())
                .buyerEmail(payment.getBuyerEmail())
                .status(PaymentStatus.CANCELLED)
                .build();

        return paymentRepository.save(cancelledPayment).toResponse();
    }
}
