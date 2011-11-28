package com.all.client.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.RequestMethod;
import com.all.client.services.CrawlerClientService;
import com.all.core.actions.Actions;
import com.all.shared.command.LoginCommand;
import com.all.shared.external.email.EmailDomain;
import com.all.shared.messages.CrawlerRequest;
import com.all.shared.messages.CrawlerResponse;

@Controller
public class CrawlerController {

	@Autowired
	private CrawlerClientService crawlerService;

	@RequestMethod(Actions.Application.REQUEST_CRAWLER_EMAIL_CONTACTS_ID)
	public CrawlerResponse importContacts(Map<EmailDomain, List<LoginCommand>> emailAccounts) {
		CrawlerRequest crawlerRequest = new CrawlerRequest();
		crawlerRequest.setAccounts(emailAccounts);
		CrawlerResponse response = crawlerService.requestImportContacts(crawlerRequest);
		return response;
	}

}
