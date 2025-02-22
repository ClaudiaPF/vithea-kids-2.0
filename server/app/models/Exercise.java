package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.*;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import play.Logger;

@Entity
public class Exercise extends Model {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @Column(nullable = true)
    private Topic topic;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @Column(nullable = true)
    private Level level;

    @OneToOne(mappedBy="exercise", cascade = CascadeType.ALL)
    private Question question;

    @OneToOne(mappedBy="exercise", cascade = CascadeType.ALL)
    private Answer rightAnswer;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Answer> answersList;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Caregiver author;
    
    @Column(nullable = false)
    private ExerciseType type;
    
    private Boolean defaultExercise;

    public Boolean getDefaultExercise() {
        return defaultExercise;
    }

    public void setDefaultExercise(Boolean defaultExercise) {
        this.defaultExercise = defaultExercise;
    }

    @OneToMany(mappedBy = "exercise")
    @JsonManagedReference
    private List<SequenceExercise> sequencesExercise = new ArrayList<SequenceExercise>();
    
    public void addSequenceExercise(SequenceExercise se) {
        this.sequencesExercise.add(se);
    }

    public List<SequenceExercise> getSequenceExercise() {
        return this.sequencesExercise;
    }

    public void setSequenceExercise(List<SequenceExercise> sequencesExercise) {
        this.sequencesExercise = sequencesExercise;
    }
   
    public Exercise(Caregiver loggedCaregiver, long topic, long level, String question, long stimulusId, String answer, List<String> distractors, Boolean def) {
        this.author = loggedCaregiver;
        this.type = ExerciseType.TEXT;
        this.defaultExercise = def;
        
        if (topic != -1) {
            this.topic = Topic.findTopicById(topic);
        }
        
        if (level != -1) {
            this.level = Level.findLevelById(level);
        }
        
        if (stimulusId != -1) {
            Resource stimulus = Resource.findById(stimulusId);
            this.question = new Question(question, stimulus);
        } else {
            this.question = new Question(question);
        }
        this.name = question;
        
        List<Answer> answers = new ArrayList();
        
        this.rightAnswer = new Answer(answer);
        answers.add(this.rightAnswer);
        
        distractors.forEach((s) -> {
            answers.add(new Answer(s));
        }); 
        
        this.answersList = answers;
    }
    
    public Exercise(Caregiver loggedCaregiver, long topic, long level, String question, String stimulusText, long answerResourceId, List<Long> distractorsResourcesIds, Boolean def) {
        this.author = loggedCaregiver;
        this.type = ExerciseType.IMAGE;
        this.defaultExercise = def;
         
        if (topic != -1) {
            this.topic = Topic.findTopicById(topic);
        }
        
        if (level != -1) {
            this.level = Level.findLevelById(level);
        }
        
        if (stimulusText != null && !stimulusText.isEmpty()) {
            this.question = new Question(question, stimulusText);
        } else {
            this.question = new Question(question);
        }
        this.name = question;
        
        List<Answer> answers = new ArrayList();
        
        Resource rightAnswer = Resource.findById(answerResourceId);
        this.rightAnswer = new Answer(rightAnswer);
        
        answers.add(this.rightAnswer);
        
        for(Long d : distractorsResourcesIds) {
            System.out.println("distractor id: " + d);
            Resource distractor = Resource.findById(d);
            answers.add(new Answer(distractor));
        }   
        
        this.answersList = answers;
    }
    
    public String getExerciseName(){
        return name;
    }
    
    public void setExerciseName(String name){
        this.name = name;
    }
    
    public Long getExerciseId() {
        return id;
    }
    public Topic getTopic() {
        return topic;
    }
    public Level getLevel() {
        return level;
    }
    public Question getQuestion() {
        return question;
    }
    public Answer getRightAnswer() {
        return rightAnswer;
    }
    public List<Answer> getAnswers() {
       return answersList;
    }
    public void resetAnswers(){
        answersList.clear();
    }
    public void removeAnswer(Answer ans){
        answersList.remove(ans);
    }
    public void removeQuestion(){
        question.delete();
    }
    public Caregiver getAuthor() {
        return author;
    }
    public ExerciseType getType() {
        return type;
    }

    public void setExerciseId(Long exerciseId) {
        this.id = exerciseId;
    }
    public void setTopic(Topic topic) {
        this.topic = topic;
    }
    public void setTopic(Long topicId) {
        Topic topic = Topic.findTopicById(topicId);
        if (topic == null) throw new NullPointerException("Topic does not exist");
        Logger.debug("New exercise :: setTopic: " + topic.getTopicDescription());
        this.topic = topic;
    }
    public void setLevel(Level level) {
        this.level = level;
    }
    public void setLevel(Long levelId) {
        Level level = Level.findLevelById(levelId);
        if (level == null) throw new NullPointerException("Level does not exist");
        Logger.debug("New exercise :: setLevel: " + level.getLevelDescription());
        this.level = level;
    }
    public void setQuestion(Question question) {
            this.question = question;
    }
    public void setQuestion(String questionDescription, Long stimulus) {
        Question question = new Question(questionDescription);
        if(stimulus != 0) question.setStimulus(stimulus);
        question.save();
        Logger.debug("New exercise :: setQuestion: " + question.getQuestionDescription() + " (" + question.getQuestionId() + ")");
        this.question = question;
    }    
    public void setRightAnswer(Answer rightAnswer) {
        this.rightAnswer = rightAnswer;
    }
    public void setRightAnswer(String rightAnswerDescription, Long resource) {
        Answer rightAnswer = new Answer(rightAnswerDescription);
        rightAnswer.setStimulus(resource);
        rightAnswer.save();
        Logger.debug("New exercise :: setRightAnswer: " + rightAnswer.getAnswerDescription() +" (" + rightAnswer.getAnswerId() + ")");
        this.rightAnswer = rightAnswer;
        this.answersList.add(rightAnswer);
    }
    public void setAnswers(List<String> answerDescriptions, List<Long> answerStimulus) {
        Iterator<String> i = answerDescriptions.iterator(); 
        Iterator<Long> j = answerStimulus.iterator();		
        while(i.hasNext() || j.hasNext()) {
            String description = i.next();
            Long stimulus = j.next();
            Answer answer = new Answer(description);
            answer.setStimulus(stimulus);
            answer.save();
            Logger.debug("New exercise :: addDistractor: " + answer.getAnswerDescription() +" (" + answer.getAnswerId() + ")");
            this.answersList.add(answer);
        }		
    }
    public void setAnswers(List<Answer> answers){
        this.answersList = answers;
    }
    
    public void setAuthor(Caregiver author) {
            this.author = author;
    }
    public void setType(ExerciseType type) {
        this.type = type;
    }

    public static List<Exercise> getAll() {
        return find.all();
    }
    
    public static final Finder<Long, Exercise> find = new Finder<>(Exercise.class);
    public static List<Exercise> findByAuthor(Caregiver author) {
            Logger.debug("Looking for exercises from: " + author.getCaregiverLogin().getUsername());
            return find.where().eq("author_id", author.getCaregiverId()).findList();
    }
    public static Exercise findExerciseById(Long id) {
            Logger.debug("Looking for exercise " + id);
            return find.where().eq("id", id).findUnique();
    }
}