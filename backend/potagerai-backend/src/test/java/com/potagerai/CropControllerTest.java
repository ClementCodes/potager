package com.potagerai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potagerai.domain.crop.Crop;
import com.potagerai.domain.crop.CropRepository;
import com.potagerai.domain.crop.NutritionalProfile;
import com.potagerai.domain.crop.NutritionalProfileRepository;
import com.potagerai.dto.auth.RegisterRequest;
import com.potagerai.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CropControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired CropRepository cropRepository;
    @Autowired NutritionalProfileRepository nutritionalProfileRepository;
    @Autowired JwtTokenProvider jwtTokenProvider;

    private String bearerToken;

    @BeforeEach
    void setUp() throws Exception {
        // Créer un utilisateur et obtenir un JWT
        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("crops-test@potagerai.com", "password123"))))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();
        bearerToken = "Bearer " + token;

        // Seed : une culture avec profil nutritionnel
        Crop tomate = cropRepository.save(Crop.builder()
                .commonName("Tomate Test")
                .scientificName("Solanum lycopersicum")
                .botanicalFamily("Solanaceae")
                .rootDepthCm(BigDecimal.valueOf(60))
                .growingDaysMin(70)
                .growingDaysMax(100)
                .build());

        nutritionalProfileRepository.save(NutritionalProfile.builder()
                .crop(tomate)
                .caloriesPer100g(BigDecimal.valueOf(18.0))
                .proteinsPer100g(BigDecimal.valueOf(0.9))
                .carbsPer100g(BigDecimal.valueOf(3.9))
                .fatsPer100g(BigDecimal.valueOf(0.2))
                .fiberPer100g(BigDecimal.valueOf(1.2))
                .andiScore(190)
                .build());
    }

    // -------------------------------------------------------------------------
    // GET /api/crops — sans token
    // -------------------------------------------------------------------------

    @Test
    void getCrops_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/crops"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // GET /api/crops — avec token valide
    // -------------------------------------------------------------------------

    @Test
    void getCrops_withToken_shouldReturn200AndList() throws Exception {
        mockMvc.perform(get("/api/crops")
                        .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].commonName").value("Tomate Test"))
                .andExpect(jsonPath("$[0].nutritionalProfile.caloriesPer100g").value(18.0));
    }

    // -------------------------------------------------------------------------
    // GET /api/crops/{id} — avec token valide
    // -------------------------------------------------------------------------

    @Test
    void getCropById_withToken_shouldReturn200() throws Exception {
        // Récupère l'id de la culture seedée
        Long id = cropRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/crops/" + id)
                        .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.botanicalFamily").value("Solanaceae"));
    }

    @Test
    void getCropById_unknown_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/crops/99999")
                        .header("Authorization", bearerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
