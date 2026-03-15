package com.main.tutorly.config;

import com.main.tutorly.entity.Subject;
import com.main.tutorly.entity.TutorProfile;
import com.main.tutorly.entity.User;
import com.main.tutorly.repository.SubjectRepository;
import com.main.tutorly.repository.TutorProfileRepository;
import com.main.tutorly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TutorProfileRepository tutorProfileRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        if (subjectRepository.count() == 0) {
            initializeSubjects();
        }
        
        if (userRepository.count() == 0) {
            initializeUsers();
        }
    }
    
    private void initializeSubjects() {
        List<Subject> subjects = Arrays.asList(
            createSubject("Programming", "Learn coding and software development", "💻"),
            createSubject("Mathematics", "Master math from basics to advanced", "📐"),
            createSubject("Physics", "Understand the laws of nature", "⚛️"),
            createSubject("English", "Improve your English language skills", "📚"),
            createSubject("Business", "Learn business and entrepreneurship", "💼"),
            createSubject("Design", "Master graphic and UI/UX design", "🎨"),
            createSubject("Cybersecurity", "Learn to protect digital systems", "🔒")
        );
        
        subjectRepository.saveAll(subjects);
        System.out.println("✅ Initialized " + subjects.size() + " subjects");
    }
    
    private Subject createSubject(String name, String description, String icon) {
        Subject subject = new Subject();
        subject.setName(name);
        subject.setDescription(description);
        subject.setIcon(icon);
        return subject;
    }
    
    private void initializeUsers() {
        // Create admin user
        User admin = new User();
        admin.setEmail("admin@tutorly.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(User.Role.ADMIN);
        admin.setIsActive(true);
        userRepository.save(admin);
        
        // Create student users
        User student1 = createStudent("student1@example.com", "Alice", "Johnson");
        User student2 = createStudent("student2@example.com", "Bob", "Williams");
        
        // Create tutor users with profiles
        createTutorWithProfile("john.smith@tutorly.com", "John", "Smith",
            "Expert programmer with 10 years of experience in full-stack development",
            BigDecimal.valueOf(50), 10, "BS Computer Science, MIT",
            "English, Spanish", Arrays.asList("Programming"));
        
        createTutorWithProfile("sarah.davis@tutorly.com", "Sarah", "Davis",
            "Mathematics professor specializing in calculus and algebra",
            BigDecimal.valueOf(45), 8, "PhD Mathematics, Stanford",
            "English", Arrays.asList("Mathematics"));
        
        createTutorWithProfile("michael.chen@tutorly.com", "Michael", "Chen",
            "Physics researcher and educator with passion for teaching",
            BigDecimal.valueOf(40), 6, "PhD Physics, Caltech",
            "English, Mandarin", Arrays.asList("Physics", "Mathematics"));
        
        createTutorWithProfile("emma.wilson@tutorly.com", "Emma", "Wilson",
            "Professional English teacher with TEFL certification",
            BigDecimal.valueOf(35), 5, "MA English Literature, Oxford",
            "English, French", Arrays.asList("English"));
        
        createTutorWithProfile("david.brown@tutorly.com", "David", "Brown",
            "MBA graduate and business consultant",
            BigDecimal.valueOf(60), 12, "MBA, Harvard Business School",
            "English", Arrays.asList("Business"));
        
        createTutorWithProfile("lisa.garcia@tutorly.com", "Lisa", "Garcia",
            "Senior UI/UX designer at top tech company",
            BigDecimal.valueOf(55), 7, "BFA Design, RISD",
            "English, Portuguese", Arrays.asList("Design"));
        
        createTutorWithProfile("james.taylor@tutorly.com", "James", "Taylor",
            "Cybersecurity expert and ethical hacker",
            BigDecimal.valueOf(70), 9, "MS Cybersecurity, Carnegie Mellon",
            "English", Arrays.asList("Cybersecurity", "Programming"));
        
        createTutorWithProfile("sophia.martinez@tutorly.com", "Sophia", "Martinez",
            "Full-stack developer and coding bootcamp instructor",
            BigDecimal.valueOf(48), 6, "BS Software Engineering",
            "English, Spanish", Arrays.asList("Programming"));
        
        System.out.println("✅ Initialized demo users (1 admin, 2 students, 8 tutors)");
    }
    
    private User createStudent(String email, String firstName, String lastName) {
        User student = new User();
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode("password123"));
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setRole(User.Role.STUDENT);
        student.setIsActive(true);
        return userRepository.save(student);
    }
    
    private void createTutorWithProfile(String email, String firstName, String lastName,
                                       String description, BigDecimal hourlyRate, int experienceYears,
                                       String education, String languages, List<String> subjectNames) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(User.Role.TUTOR);
        user.setIsActive(true);
        user = userRepository.save(user);
        
        TutorProfile profile = new TutorProfile();
        profile.setUser(user);
        profile.setDescription(description);
        profile.setHourlyRate(hourlyRate);
        profile.setExperienceYears(experienceYears);
        profile.setEducation(education);
        profile.setLanguages(languages);
        profile.setIsVerified(true);
        profile.setTotalLessons(0);
        profile.setRating(BigDecimal.valueOf(4.5));
        profile.setTotalReviews(0);
        
        // Add subjects
        profile.setSubjects(new HashSet<>());
        for (String subjectName : subjectNames) {
            subjectRepository.findByName(subjectName).ifPresent(subject -> 
                profile.getSubjects().add(subject)
            );
        }
        
        tutorProfileRepository.save(profile);
    }
}
