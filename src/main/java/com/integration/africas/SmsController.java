package com.integration.africas;

import com.africastalking.sms.Recipient;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SmsController {
	
	private final MySmsService smsService;
	
	@Autowired
	public SmsController(MySmsService smsService) {
		this.smsService = smsService;
	}
	
	@PostMapping("/send-sms")
	public List<Recipient> sendSms(@RequestBody SmsRequest smsRequest) {
		List<String> recipients = smsRequest.getRecipients();
		String message = smsRequest.getMessage();
		
		return smsService.sendBulkSms(recipients, message);
		
	}
	
	
	@PostMapping("/uploadContacts")
	public String uploadContacts(@RequestParam("contactsFile") MultipartFile file,
			@RequestParam("message") String message) {
		try {
			// Read the uploaded file and process the contacts
			List<String> recipients = readContactsFromFile(file);
			
			// Modify the message with the recipient's name
			 message = "Dear %s, "+message;
			
			// Send the bulk SMS
			smsService.sendBulkSms(recipients, message);
			
			return "Contacts uploaded and SMS sent successfully.";
		} catch (Exception e) {
			return "Failed to upload contacts: " + e.getMessage();
		}
	}
	
	private List<String> readContactsFromFile(MultipartFile file) throws IOException {
		// Read the file using Apache POI or any other library
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0); // Assuming contacts are in the first sheet
		
		List<String> recipients = new ArrayList<>();
		
		for (Row row : sheet) {
			Cell nameCell = row.getCell(0); // Assuming name is in the first column
			Cell phoneCell = row.getCell(1); //Assuming phone number is in the second column
			
			String name = nameCell.getStringCellValue();
			String phoneNumber = "+"+phoneCell.getStringCellValue();
			
			recipients.add(phoneNumber); // Add phone number to the recipients list
		}
		
		workbook.close();
		
		return recipients;
	}
	
	
	@PostMapping("/sendDifferentMessages")
	public String uploadContacts(@RequestParam("contactsFile") MultipartFile file) {
		try {
			List<Map<String, String>> recipientMessages = readRecipientMessagesFromFile(file);
			smsService.sendBulkSms2(recipientMessages);
			return "Contacts uploaded and SMS sent successfully.";
		} catch (Exception e) {
			return "Failed to upload contacts: " + e.getMessage();
		}
	}
	
	private List<Map<String, String>> readRecipientMessagesFromFile(MultipartFile file) throws IOException {
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0); // Assuming contacts are in the first sheet
		
		List<Map<String, String>> recipientMessages = new ArrayList<>();
		
		for (Row row : sheet) {
			Cell nameCell = row.getCell(0); // Assuming name is in the first column
			Cell phoneCell = row.getCell(1); // Assuming phone number is in the second column
			Cell messageCell = row.getCell(2); // Assuming message is in the third column
			
			String name = nameCell.getStringCellValue();
			String phoneNumber = phoneCell.getStringCellValue();
			String message = String.format("Dear %s, %s", name, messageCell.getStringCellValue());
			
			Map<String, String> recipientMessage = new HashMap<>();
			recipientMessage.put("phoneNumber", phoneNumber);
			recipientMessage.put("message", message);
			
			recipientMessages.add(recipientMessage);
		}
		
		workbook.close();
		
		return recipientMessages;
	}
	
	@GetMapping("/getAccountBalance")
	public String getAccountBalance(){
		return  smsService.getAccountBalance();
	}
	
}
