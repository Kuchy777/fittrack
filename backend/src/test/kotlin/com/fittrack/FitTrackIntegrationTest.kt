package com.fittrack

import com.fasterxml.jackson.databind.ObjectMapper
import com.fittrack.dto.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class FitTrackIntegrationTest {

    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var mapper: ObjectMapper

    companion object { var token = ""; var workoutId = 0L }

    // ── Scenariusz 1: Rejestracja użytkownika ─────────────
    @Test @Order(1)
    fun `POST api_auth_register - returns 201 with tokens`() {
        val body = mapper.writeValueAsString(RegisterRequest("test@fittrack.pl", "Pass1234"))
        val result = mvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.accessToken").isNotEmpty)
            .andReturn()
        token = mapper.readTree(result.response.contentAsString)["accessToken"].asText()
    }

    // ── Scenariusz 2: Logowanie ───────────────────────────
    @Test @Order(2)
    fun `POST api_auth_login - returns 200 with tokens`() {
        val body = mapper.writeValueAsString(LoginRequest("test@fittrack.pl", "Pass1234"))
        mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").isNotEmpty)
    }

    // ── Scenariusz 3: Aktualizacja profilu i kalkulacja kcal
    @Test @Order(3)
    fun `PUT api_profile - calculates dailyKcalGoal via Harris-Benedict`() {
        val req = ProfileUpdateRequest(
            displayName   = "Jan Kowalski",
            gender        = "MALE",
            birthDate     = LocalDate.of(2000, 1, 1),
            weightKg      = BigDecimal("80"),
            heightCm      = BigDecimal("178"),
            activityLevel = "MODERATE",
            goal          = "LOSE"
        )
        mvc.perform(put("/api/profile")
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.dailyKcalGoal").isNumber)
    }

    // ── Scenariusz 4: Dodanie wpisu do dziennika ──────────
    @Test @Order(4)
    fun `POST api_diary - adds entry and returns 201`() {
        val req = DiaryEntryRequest(
            entryDate = LocalDate.now(),
            mealType  = "BREAKFAST",
            productId = null,
            recipeId  = null,
            customName = "Owsianka",
            quantityG  = BigDecimal("150"),
            photoPath  = null,
            note       = null
        )
        mvc.perform(post("/api/diary")
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(req)))
            .andExpect(status().isCreated)
    }

    // ── Scenariusz 5: Pobieranie dziennego podsumowania ───
    @Test @Order(5)
    fun `GET api_diary_summary - returns DailySummaryResponse`() {
        mvc.perform(get("/api/diary/summary")
            .header("Authorization", "Bearer $token")
            .param("date", LocalDate.now().toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.kcalGoal").isNumber)
            .andExpect(jsonPath("$.kcalConsumed").isNumber)
            .andExpect(jsonPath("$.kcalRemaining").isNumber)
    }
}
