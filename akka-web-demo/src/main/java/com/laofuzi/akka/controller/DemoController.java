package com.laofuzi.akka.controller;

import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laofuzi.akka.service.DemoService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/demo")
public class DemoController {
	private DemoService demoService;
	@Autowired
	public DemoController(DemoService demoService) {
		this.demoService = demoService;
	}
	
	@GetMapping(path = "/hello", params = { "who" })
	public Mono<String> hello( @RequestParam("who") String who) {
		CompletionStage<String> cs = demoService.hello(who);
		return Mono.fromCompletionStage(cs.thenApply(h->{
			System.out.println(h);
			return h;
			}));
	}

}
