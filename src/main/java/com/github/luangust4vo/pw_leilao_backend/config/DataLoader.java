package com.github.luangust4vo.pw_leilao_backend.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.luangust4vo.pw_leilao_backend.models.Category;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.PersonProfile;
import com.github.luangust4vo.pw_leilao_backend.models.Profile;
import com.github.luangust4vo.pw_leilao_backend.models.enums.ProfileType;
import com.github.luangust4vo.pw_leilao_backend.repositories.CategoryRepository;
import com.github.luangust4vo.pw_leilao_backend.repositories.PersonRepository;
import com.github.luangust4vo.pw_leilao_backend.repositories.ProfileRepository;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired 
    private PasswordEncoder passwordEncoder;
    
    // Configurações do usuário ADMIN padrão
    @Value("${app.admin.name}")
    private String adminName;
    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        initializeProfiles();
        initializeCategories();
        initializeAdminUser();
    }

    private void initializeProfiles() {
        for (ProfileType profileType : ProfileType.values()) {
            if (profileRepository.findByType(profileType).isEmpty()) {
                Profile profile = new Profile();
                profile.setType(profileType);
                profileRepository.save(profile);
            }
        }
    }

    private void initializeCategories() {
        String[][] defaultCategories = {
            {"Eletrônicos", "Dispositivos eletrônicos, smartphones, computadores, etc."},
            {"Veículos", "Carros, motos, bicicletas e outros veículos"},
            {"Casa e Jardim", "Móveis, decoração, eletrodomésticos e itens para jardim"},
            {"Moda e Beleza", "Roupas, calçados, acessórios e produtos de beleza"},
            {"Livros e Mídia", "Livros, filmes, jogos e outros tipos de mídia"},
            {"Esportes e Lazer", "Equipamentos esportivos, instrumentos musicais e hobbies"},
            {"Arte e Antiguidades", "Obras de arte, peças antigas e colecionáveis"},
            {"Joias e Relógios", "Joias, relógios e acessórios de luxo"},
            {"Ferramentas", "Ferramentas para trabalho e bricolagem"},
            {"Outros", "Itens que não se encaixam nas outras categorias"}
        };

        for (String[] categoryData : defaultCategories) {
            String name = categoryData[0];
            String observation = categoryData[1];
            
            if (categoryRepository.findByName(name).isEmpty()) {
                Category category = new Category();
                category.setName(name);
                category.setObservation(observation);
                categoryRepository.save(category);
            }
        }
    }

    private void initializeAdminUser() {
        if (personRepository.findByEmail(this.adminEmail).isEmpty()) {
            Profile adminProfile = profileRepository.findByType(ProfileType.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Perfil ADMIN não encontrado"));
            
            Person admin = new Person();
            admin.setName("ADMIN");
            admin.setEmail(this.adminEmail);
            admin.setPassword(passwordEncoder.encode(this.adminPassword));
            admin.setActive(true);
            
            PersonProfile personProfile = new PersonProfile();
            personProfile.setProfile(adminProfile);
            personProfile.setPerson(admin);
            
            List<PersonProfile> personProfiles = new ArrayList<>();
            personProfiles.add(personProfile);
            admin.setPersonProfiles(personProfiles);
            
            personRepository.save(admin);
        }
    }
}
