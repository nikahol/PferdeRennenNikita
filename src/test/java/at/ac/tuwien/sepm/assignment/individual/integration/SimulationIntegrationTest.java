package at.ac.tuwien.sepm.assignment.individual.integration;


import at.ac.tuwien.sepm.assignment.individual.integration.dto.*;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public class SimulationIntegrationTest {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String BASE_URL = "http://localhost:";
    private static final String SIMULATION_URL = "/api/v1/simulations";
    private static final String HORSE_URL = "/api/v1/horses";
    private static final String JOCKEY_URL = "/api/v1/jockeys";
    private static final JockeyTestDto JOCKEY_1 = new JockeyTestDto("Jockey1", 40.0);
    private static final JockeyTestDto JOCKEY_2 = new JockeyTestDto("Jockey2", -1000.0);
    private static final JockeyTestDto JOCKEY_3 = new JockeyTestDto("Jockey3", 0.0);
    private static final HorseTestDto HORSE_1 = new HorseTestDto("Horse1", 40.0, 60.0);
    private static final HorseTestDto HORSE_2 = new HorseTestDto("Horse2", 49.99, 50.0);
    private static final HorseTestDto HORSE_3 = new HorseTestDto("Horse3", 45.0, 55.0);
    private static final SimulationParticipantTestDto SIMULATION_PARTICIPANT_1 = new SimulationParticipantTestDto(1, 1, 1.0);
    private static final SimulationParticipantTestDto SIMULATION_PARTICIPANT_2 = new SimulationParticipantTestDto(2, 2, 0.95);
    private static final SimulationParticipantTestDto SIMULATION_PARTICIPANT_3 = new SimulationParticipantTestDto(3, 3, 1.05);
    private static final List<SimulationParticipantTestDto> SIMULATION_PARTICIPANTS = new ArrayList<>();
    private static final SimulationInputTestDto SIMULATION_INPUT = new SimulationInputTestDto("Simulation1", SIMULATION_PARTICIPANTS);
    private static final HorseJockeyCombinationTestDto HORSE_JOCKEY_COMBINATION_1 =
        new HorseJockeyCombinationTestDto(2, "Horse1", "Jockey1", 53.455, 50.0, 1.0691, 1.0);
    private static final HorseJockeyCombinationTestDto HORSE_JOCKEY_COMBINATION_2 =
        new HorseJockeyCombinationTestDto(3, "Horse2", "Jockey2", 43.9382, 49.99, 0.9252, 0.95);
    private static final HorseJockeyCombinationTestDto HORSE_JOCKEY_COMBINATION_3 =
        new HorseJockeyCombinationTestDto(1, "Horse3", "Jockey3", 57.75, 55.0, 1.0, 1.05);
    private static final List<HorseJockeyCombinationTestDto> HORSE_JOCKEY_COMBINATIONS_1 = new ArrayList<>();
    private static final SimulationResultTestDto SIMULATION_1 = new SimulationResultTestDto("Simulation1", HORSE_JOCKEY_COMBINATIONS_1);

    private static final SimulationResultTestDto SIMULATION_2 = new SimulationResultTestDto("Simulation2", HORSE_JOCKEY_COMBINATIONS_1);
    private static final SimulationInputTestDto SIMULATION_INPUT2 = new SimulationInputTestDto("Simulation2", SIMULATION_PARTICIPANTS);

    @LocalServerPort
    private int port;
    @Autowired
    private DBConnectionManager dbConnectionManager;

    /**
     * It is important to close the database connection after each test in order to clean the in-memory database
     */
    @After
    public void afterEachTest() throws PersistenceException {
        SIMULATION_PARTICIPANTS.remove(SIMULATION_PARTICIPANT_1);
        SIMULATION_PARTICIPANTS.remove(SIMULATION_PARTICIPANT_2);
        SIMULATION_PARTICIPANTS.remove(SIMULATION_PARTICIPANT_3);


        HORSE_JOCKEY_COMBINATIONS_1.remove(HORSE_JOCKEY_COMBINATION_1);
        HORSE_JOCKEY_COMBINATIONS_1.remove(HORSE_JOCKEY_COMBINATION_2);
        HORSE_JOCKEY_COMBINATIONS_1.remove(HORSE_JOCKEY_COMBINATION_3);
        dbConnectionManager.closeConnection();

    }

    @Before
    public void beforeEach() {
        postHorse(HORSE_1);
        postHorse(HORSE_2);
        postHorse(HORSE_3);

        postJockey(JOCKEY_1);
        postJockey(JOCKEY_2);
        postJockey(JOCKEY_3);


        SIMULATION_PARTICIPANTS.add(SIMULATION_PARTICIPANT_1);
        SIMULATION_PARTICIPANTS.add(SIMULATION_PARTICIPANT_2);
        SIMULATION_PARTICIPANTS.add(SIMULATION_PARTICIPANT_3);

        HORSE_JOCKEY_COMBINATIONS_1.add(HORSE_JOCKEY_COMBINATION_1);
        HORSE_JOCKEY_COMBINATIONS_1.add(HORSE_JOCKEY_COMBINATION_2);
        HORSE_JOCKEY_COMBINATIONS_1.add(HORSE_JOCKEY_COMBINATION_3);
        HORSE_JOCKEY_COMBINATIONS_1.sort(Comparator.comparingInt(HorseJockeyCombinationTestDto::getRank));
    }

    @Test
    public void whenSaveOneSimulation_thenStatus201AndGetGeneratedId() {
        HttpEntity<SimulationInputTestDto> request = new HttpEntity<>(SIMULATION_INPUT);
        ResponseEntity<SimulationResultTestDto> response = REST_TEMPLATE
            .exchange(BASE_URL + port + SIMULATION_URL, HttpMethod.POST, request, SimulationResultTestDto.class);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        SimulationResultTestDto simulationResponse = response.getBody();
        assertNotNull(simulationResponse);
        assertNotNull(simulationResponse.getId());
        System.out.println("Id of the simulation " + simulationResponse.getHorseJockeyCombinations().get(1).getId());
        //set created and id to null to enable equals comparison
        simulationResponse.setCreated(null);
        simulationResponse.setId(null);
        assertEquals(SIMULATION_1, simulationResponse);
    }
    @Test
    public void givenOneSimulation_whenFindThisSimulationByID_thenStatus200AndGetThisSimulation(){
        postSimulation1();
        HttpEntity<SimulationInputTestDto> request = new HttpEntity<>(SIMULATION_INPUT);
        ResponseEntity<SimulationResultTestDto> response = REST_TEMPLATE.exchange(BASE_URL + port + SIMULATION_URL + "/1", HttpMethod.GET, request, SimulationResultTestDto.class);
        SimulationResultTestDto simulations = response.getBody();
        assertEquals(SIMULATION_1.getName(), simulations.getName());
        assertEquals(SIMULATION_1.getHorseJockeyCombinations(), simulations.getHorseJockeyCombinations());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }



    @Test
    public void givenTwoSimulations_WhenFindAll_thenStatus200andGetList(){
        postSimulation1();
        postSimulation2();
        ResponseEntity<List<SimulationResultTestDto>> response = REST_TEMPLATE.exchange(BASE_URL + port + SIMULATION_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<SimulationResultTestDto>>() {
        });
        List<SimulationResultTestDto> simulations = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2,simulations.size() );
    }

    private void postHorse(HorseTestDto horse) {
        REST_TEMPLATE.postForObject(BASE_URL + port + HORSE_URL, new HttpEntity<>(horse), HorseTestDto.class);
    }

    private void postJockey(JockeyTestDto jockey) {
        REST_TEMPLATE.postForObject(BASE_URL + port + JOCKEY_URL, new HttpEntity<>(jockey), JockeyTestDto.class);
    }

    private void postSimulation1(){
        REST_TEMPLATE.postForObject(BASE_URL + port+ SIMULATION_URL, new HttpEntity<>(SIMULATION_INPUT), SimulationInputTestDto.class);
    }
    private void postSimulation2(){
        REST_TEMPLATE.postForObject(BASE_URL + port+ SIMULATION_URL, new HttpEntity<>(SIMULATION_INPUT2), SimulationInputTestDto.class);
    }

}
