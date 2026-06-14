package br.edu.ifg.med_clinica_api.domain.bo;

import br.edu.ifg.med_clinica_api.domain.dao.PatientRepository;
import br.edu.ifg.med_clinica_api.domain.dao.UserRepository;
import br.edu.ifg.med_clinica_api.domain.dto.patient.GooglePatientRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.user.GoogleSignupPendingDTO;
import br.edu.ifg.med_clinica_api.domain.entity.Address;
import br.edu.ifg.med_clinica_api.domain.entity.Patient;
import br.edu.ifg.med_clinica_api.domain.entity.User;
import br.edu.ifg.med_clinica_api.domain.enums.AuthProvider;
import br.edu.ifg.med_clinica_api.domain.enums.UserRole;
import br.edu.ifg.med_clinica_api.infra.security.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final TokenService tokenService;

    public GoogleAuthService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
    }

    public GoogleSignupPendingDTO getPendingSignup(String signupToken) {
        var decodedToken = tokenService.validateGoogleSignupToken(signupToken);

        return new GoogleSignupPendingDTO(
                decodedToken.getSubject(),
                decodedToken.getClaim("name").asString()
        );
    }

    @Transactional
    public String completePatientSignup(String signupToken, GooglePatientRegisterDTO data) {
        var decodedToken = tokenService.validateGoogleSignupToken(signupToken);

        String email = decodedToken.getSubject();
        String name = decodedToken.getClaim("name").asString();
        String providerId = decodedToken.getClaim("providerId").asString();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Usuário já cadastrado.");
        }

        if (patientRepository.existsByCpf(data.cpf())) {
            throw new RuntimeException("CPF já cadastrado.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword("");
        user.setRole(UserRole.ROLE_PATIENT);
        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(providerId);

        userRepository.save(user);

        Patient patient = new Patient();
        patient.setActive(true);
        patient.setName(name);
        patient.setEmail(email);
        patient.setCpf(data.cpf());
        patient.setPhone(data.phone());
        patient.setAddress(new Address(data.address()));
        patient.setBirthDate(data.birthDate());
        patient.setGender(data.gender());
        patient.setUser(user);

        patientRepository.save(patient);

        return tokenService.generateToken(user);
    }
}
