package dev.sumuks.simplefileconverter.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "text-service", url = "${text-service.url}")
public interface TextServiceClient {

    @PostMapping("/url-to-html")
    Response convertUrlToHtml(@RequestParam("url") String url);
}
