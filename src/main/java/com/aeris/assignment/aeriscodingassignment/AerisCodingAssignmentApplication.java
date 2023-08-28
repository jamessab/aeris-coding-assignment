package com.aeris.assignment.aeriscodingassignment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@OpenAPIDefinition(
        info = @Info(
                title = "Aeris Coding Assignment - James Sablatura",
                description = "Implementation of the netCDF Coding Assignment")
)
@SpringBootApplication
@RestController
public class AerisCodingAssignmentApplication {

    @Autowired NetCDFBean netCDFBean;

    public static void main(String[] args) {
        SpringApplication.run(AerisCodingAssignmentApplication.class, args);
    }

    @PostConstruct
    public void init() throws Exception {
        // Immediately after startup, initialize the bean that handles most of the business logic. Any exception thrown
        // from there will result in a startup exception and spring boot will abort.
        netCDFBean.openNetCDFFile();
    }

    @GetMapping(value = "/get-info", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getInfo() {
        return netCDFBean.getInfo();
    }

    @GetMapping(value="/get-data", produces=MediaType.APPLICATION_JSON_VALUE)
    public String getData(@RequestParam("time-index") int timeIndex, @RequestParam("z-index") int zIndex) throws Exception {
        return netCDFBean.getData(timeIndex, zIndex);
    }

    @GetMapping(value="/get-image", produces=MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage(@RequestParam("time-index") int timeIndex, @RequestParam("z-index") int zIndex) throws Exception {
        return netCDFBean.getImage(timeIndex, zIndex);
    }

    @ControllerAdvice
    public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
        // Catch exceptions here. In a production world we would not use the generic 'Exception' but for
        // this exercise I'll use it for ease
        @ExceptionHandler(value = { IllegalArgumentException.class, Exception.class })
        protected ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
            String bodyOfResponse = ex.getMessage();
            return handleExceptionInternal(ex, bodyOfResponse,
                    new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }
}

