package com.example.webservice.Controller;


import com.amazonaws.util.EC2MetadataUtils;
import com.example.webservice.Dynamoconfig.DynamoService;
import com.example.webservice.Model.User;
import com.example.webservice.Model.UserDetail;
import com.example.webservice.Model.Message;
import com.example.webservice.SNSConfig.SNSUtil;

import com.example.webservice.Repo.UserRepo;
import com.google.gson.Gson;
import com.timgroup.statsd.StatsDClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.Date;


@RestController
public class UserController {

    private final static Logger logger= LoggerFactory.getLogger(UserController.class);


    @Autowired
    private StatsDClient statsDClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //instance
    @Autowired
    UserRepo userRepo;

    @Autowired
    private SNSUtil snsUtil;

    @Autowired
    DynamoService dynamoService;


    @GetMapping("/v1/user/self")
    public ResponseEntity<?> getUserInfo() {
        logger.info("This is info message");
        logger.warn("This is warn message");
        logger.error("This is error message");

        statsDClient.incrementCounter("endpoint.homepage.http.get");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Date Time", DateTime.now().toString() );

        try{
            jsonObject.put("PrivateIpAddress", EC2MetadataUtils.getPrivateIpAddress());
            jsonObject.put("InstanceId", EC2MetadataUtils.getInstanceId());
            jsonObject.put("InstanceType", EC2MetadataUtils.getInstanceType());
            jsonObject.put("AvailabilityZone", EC2MetadataUtils.getAvailabilityZone());
            jsonObject.put("EC2InstanceRegion", EC2MetadataUtils.getEC2InstanceRegion());
            jsonObject.put("AmiId", EC2MetadataUtils.getAmiId());
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

        String returnString=jsonObject.toString();
        logger.info(returnString);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userInfo = userRepo.findByUsername(auth.getName());

        return new ResponseEntity(new UserDetail(userInfo), HttpStatus.OK);
    }

    @GetMapping("/healthz")
    public String getEndPoint() {
        logger.info("This is info message");
        logger.warn("This is warn message");
        logger.error("This is error message");

        statsDClient.incrementCounter("endpoint.homepage.http.get");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Date Time", DateTime.now().toString() );

        try{
            jsonObject.put("PrivateIpAddress", EC2MetadataUtils.getPrivateIpAddress());
            jsonObject.put("InstanceId", EC2MetadataUtils.getInstanceId());
            jsonObject.put("InstanceType", EC2MetadataUtils.getInstanceType());
            jsonObject.put("AvailabilityZone", EC2MetadataUtils.getAvailabilityZone());
            jsonObject.put("EC2InstanceRegion", EC2MetadataUtils.getEC2InstanceRegion());
            jsonObject.put("AmiId", EC2MetadataUtils.getAmiId());
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

        String returnString=jsonObject.toString();
        logger.info(returnString);
        return String.format(" ");
    }

    @PostMapping("/v1/user")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        logger.info("This is info message");
        logger.warn("This is warn message");
        logger.error("This is error message");

        statsDClient.incrementCounter("endpoint.homepage.http.get");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Date Time", DateTime.now().toString() );

        try{
            jsonObject.put("PrivateIpAddress", EC2MetadataUtils.getPrivateIpAddress());
            jsonObject.put("InstanceId", EC2MetadataUtils.getInstanceId());
            jsonObject.put("InstanceType", EC2MetadataUtils.getInstanceType());
            jsonObject.put("AvailabilityZone", EC2MetadataUtils.getAvailabilityZone());
            jsonObject.put("EC2InstanceRegion", EC2MetadataUtils.getEC2InstanceRegion());
            jsonObject.put("AmiId", EC2MetadataUtils.getAmiId());
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

        String returnString=jsonObject.toString();
        logger.info(returnString);

        if (userRepo.existsByUsername(user.getUsername())){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepo.save(user);

        String token = RandomStringUtils.random(8,true,true);
        dynamoService.putItemInTable("Email",user.getUsername(),"Token", token);

        String link = "http://prod.spicyrice.me/v1/verifyUserEmail?email="
                + user.getUsername() + "token=" + token;

        Message message = new Message();
        message.setFirst_name(user.getFirst_name());
        message.setUsername(user.getUsername());
        message.setOne_time_token(token);
        message.setLink(link);
        message.setMessage_type("String");

        this.snsUtil.publishSNSMessage(new Gson().toJson(message));

        return new ResponseEntity(new UserDetail(user), HttpStatus.CREATED);
    }

    @PutMapping("/v1/user/self")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user) {
        logger.info("This is info message");
        logger.warn("This is warn message");
        logger.error("This is error message");

        statsDClient.incrementCounter("endpoint.homepage.http.get");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Date Time", DateTime.now().toString() );

        try{
            jsonObject.put("PrivateIpAddress", EC2MetadataUtils.getPrivateIpAddress());
            jsonObject.put("InstanceId", EC2MetadataUtils.getInstanceId());
            jsonObject.put("InstanceType", EC2MetadataUtils.getInstanceType());
            jsonObject.put("AvailabilityZone", EC2MetadataUtils.getAvailabilityZone());
            jsonObject.put("EC2InstanceRegion", EC2MetadataUtils.getEC2InstanceRegion());
            jsonObject.put("AmiId", EC2MetadataUtils.getAmiId());
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

        String returnString=jsonObject.toString();
        logger.info(returnString);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userInfo = userRepo.findByUsername(auth.getName());

        System.out.println(user.getUsername());

        if (!userInfo.getUsername().equals(user.getUsername())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        userInfo.setFirst_name(user.getFirst_name());
        userInfo.setLast_name(user.getLast_name());
        userInfo.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(userInfo);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    // Get a Single Note

    // Update a Note

    // Delete a Note
}