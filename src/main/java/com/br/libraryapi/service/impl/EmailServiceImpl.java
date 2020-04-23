package com.br.libraryapi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.br.libraryapi.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	@Value("${application.mail.default.remetent}")
	private String remetent;
	
	private final JavaMailSender javaMailSend;
	
	@Override
	public void sendMails(String mesage, List<String> mailList) {
		String[] mails = mailList.toArray(new String[mailList.size()]);
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(remetent);
		mailMessage.setSubject("Livro com empréstimo atrasado");
		mailMessage.setText(mesage);
		mailMessage.setTo(mails);
		
		javaMailSend.send(mailMessage);
	}

}
