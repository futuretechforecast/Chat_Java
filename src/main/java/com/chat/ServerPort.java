package com.chat;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;

public class ServerPort implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

	@Override
	public void customize(ConfigurableServletWebServerFactory factory) {

		String port = System.getenv("ServerPort");

		int listenport;

		if (port != null && !port.isEmpty()) {

			listenport = Integer.parseInt(System.getenv("ServerPort"));

		} else {
			listenport = 9000;
		}
		factory.setPort(listenport);
	}

}
