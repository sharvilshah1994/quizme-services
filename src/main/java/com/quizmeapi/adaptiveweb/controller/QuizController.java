package com.quizmeapi.adaptiveweb.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.quizmeapi.adaptiveweb.model.Question;
import com.quizmeapi.adaptiveweb.model.Quiz;
import com.quizmeapi.adaptiveweb.model.User;
import com.quizmeapi.adaptiveweb.repository.QuestionRepository;
import com.quizmeapi.adaptiveweb.repository.QuizRepository;
import com.quizmeapi.adaptiveweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/quiz", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuizController {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public QuizController(QuizRepository quizRepository, UserRepository userRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<?> postUserResponse(JsonNode jsonNode) {
        try {
            Quiz quiz = new Quiz();
            if (!jsonNode.has("user_id") && !jsonNode.has("quiz_id") && !jsonNode.has("user_choice") && !jsonNode.has("question_id")) {
                return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST);
            }
            User user = userRepository.findById(jsonNode.get("user_id").asInt());
            Question question = questionRepository.findById(jsonNode.get("question_id").asInt());
            quiz.setQuizId(jsonNode.get("quiz_id").asInt());
            quiz.setUserChoice(jsonNode.get("user_choice").asText());
            quiz.setQuestion(question);
            quiz.setUser(user);
            quizRepository.save(quiz);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Success");
        } catch (Exception e) {
            System.out.println(e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot parse request");
        }
    }
}
