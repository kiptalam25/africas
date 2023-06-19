package com.integration.africas;

import com.africastalking.AfricasTalking;
import com.africastalking.PaymentService;
import com.africastalking.SmsService;
//import com.africastalking.sms.Recipient;
import com.africastalking.payment.response.BankTransferResponse;
import com.africastalking.payment.response.CheckoutResponse;
import com.africastalking.payment.response.WalletBalanceResponse;
import com.africastalking.sms.Recipient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MySmsService {
	
	@Value("${africastalking.username}")
	private String username;
	
	@Value("${africastalking.apiKey}")
	private String apiKey;
	SmsService sms;
	
	
	public List<Recipient> sendBulkSms(List<String> recipients, String message) {
		// use your sandbox app API key for development in the test environment
	try {
		String[] phoneNumbers = recipients.toArray(new String[0]);
		AfricasTalking.initialize(username, apiKey);
		
		// Initialize a service e.g. SMS
		 sms = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);
		
		// Use the service
		List<Recipient> response = sms.send(message, phoneNumbers, true);
		return response;
	}catch (Exception e){
		System.out.println(e.getMessage());
	}
		return null;
	}
	
	public void sendBulkSms2(List<Map<String, String>> recipientMessages) {
		for (Map<String, String> recipientMessage : recipientMessages) {
			String phoneNumber = "+"+recipientMessage.get("phoneNumber");
			String message = recipientMessage.get("message");
			
			// Code to send SMS to the specific recipient with their message
			sendSms(phoneNumber, message);
		}
	}
	
	private void sendSms(String phoneNumber, String message) {
		List<String> recipients = List.of(phoneNumber);
		try {
			sendBulkSms(recipients,message);
		} catch (Exception e) {
			System.out.println("Failed to send SMS to: " + phoneNumber);
			e.printStackTrace();
		}
	}
	
	public String getAccountBalance() {
		AfricasTalking.initialize(username, apiKey);
		PaymentService paymentService = AfricasTalking.getService(AfricasTalking.SERVICE_PAYMENT);
		
		try {
			WalletBalanceResponse balanceResponse = paymentService.fetchWalletBalance();
			return balanceResponse.toString();
//					getData().getBalance().getAmount();
		} catch (Exception e) {
			// Handle any exceptions
			e.printStackTrace();
		}
		
		return "0.0"; // Default value if retrieval fails
	}
}
