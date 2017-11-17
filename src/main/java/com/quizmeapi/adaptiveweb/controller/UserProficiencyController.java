package com.quizmeapi.adaptiveweb.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.quizmeapi.adaptiveweb.model.Topic;
import com.quizmeapi.adaptiveweb.model.User;
import com.quizmeapi.adaptiveweb.model.UserProficiency;
import com.quizmeapi.adaptiveweb.repository.TopicRepository;
import com.quizmeapi.adaptiveweb.repository.UserProficiencyRepository;
import com.quizmeapi.adaptiveweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/userProficiency")
public class UserProficiencyController {

    private final UserProficiencyRepository userProficiencyRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    @Autowired
    public UserProficiencyController(UserProficiencyRepository userProficiencyRepository, UserRepository userRepository, TopicRepository topicRepository) {
        this.userProficiencyRepository = userProficiencyRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
    }

    @RequestMapping(value = "/{user_id}", method = RequestMethod.GET)
    @ResponseBody
    @CrossOrigin
    public List<UserProficiency> getProficiencyByUserId(@PathVariable("user_id") int userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return null;
        }
        List<UserProficiency> userProficiencies = userProficiencyRepository.findAllByUser(user);
        if (userProficiencies == null) {
            return null;
        }
        return userProficiencies;
    }

    @RequestMapping(value = "/{user_id}", method = RequestMethod.PUT)
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<?> updateUserProficiency(@PathVariable("user_id") int userId, @RequestBody List<JsonNode> rawInput) {
        try {
            User user = userRepository.findById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User id not found");
            }
            for (JsonNode jsonNode: rawInput){
                if (!jsonNode.has("skill_topic") && !jsonNode.has("proficiency")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("skill_topic and proficiency needed");
                }
                String skillTopic = jsonNode.get("skill_topic").asText();
                Topic topic = topicRepository.findTopicByTopicName(skillTopic);
                if (topic == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("skill_topic is invalid");
                }
                int prof = jsonNode.get("proficiency").asInt();
                UserProficiency userProficiency;
                UserProficiency oldProf = userProficiencyRepository.findBySkillTopicAndUser(skillTopic, user);
                if (oldProf == null) {
                    userProficiency = new UserProficiency();
                } else {
                    userProficiency = oldProf;
                }
                if (prof < 0 || prof > 5) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Proficiency value must be 0-5");
                }
                userProficiency.setTopic(topic);
                userProficiency.setUser(user);
                userProficiency.setSkillTopic(skillTopic);
                userProficiency.setProficiency(prof);
                userProficiencyRepository.save(userProficiency);
            }
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Success");
        }
        catch (Exception e) {
            System.out.println(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request cannot be parsed");
        }
    }
}
