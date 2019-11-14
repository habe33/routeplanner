package com.routeplanner.controller;

import com.routeplanner.dto.ShortestPathResponse;
import com.routeplanner.exceptions.AppStatusException;
import com.routeplanner.exceptions.ResultNotFoundException;
import com.routeplanner.service.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RouteController.class)
public class RouteControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RouteService routeService;

    @Test
    public void getShortestPathSuccess() throws Exception {
        ShortestPathResponse resp = getResponse();
        given(routeService.getShortestPath("TLL", "LSS", 4)).willReturn(resp);

        mvc.perform(get("/shortest-path?startCode=TLL&endCode=LSS&stops=4").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{ airports: [ \"TLL\", \"HAM\", \"BRE\", \"CRL\", \"CDG\", \"LIS\" ], " +
                        "distances: [ 1110.76, 102.78, 414.33, 211.14, 1469.96 ], totalDistance: 3308.97 }"));
    }

    @Test
    public void getShortestPathAppCalculating() throws Exception {
        given(routeService.getShortestPath("TLL", "LSS", 4)).willThrow(new AppStatusException("Application is calculating new graph"));

        mvc.perform(get("/shortest-path?startCode=TLL&endCode=LSS&stops=4").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().json("{ appStatus: \"STOPPED\", httpStatus: \"SERVICE_UNAVAILABLE\", " +
                        "message: \"Application is calculating new graph\" }"));
    }

    @Test
    public void getShortestPathPathNotFound() throws Exception {
        given(routeService.getShortestPath("TLL", "LSS", 4)).willThrow(new ResultNotFoundException("Path not found for TLL->LSS"));

        mvc.perform(get("/shortest-path?startCode=TLL&endCode=LSS&stops=4").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{ appStatus: \"STOPPED\", httpStatus: \"BAD_REQUEST\", " +
                        "message: \"Path not found for TLL->LSS\" }"));
    }

    private ShortestPathResponse getResponse() {
        ShortestPathResponse resp = new ShortestPathResponse();
        resp.setAirports(Arrays.asList("TLL", "HAM", "BRE", "CRL", "CDG", "LIS"));
        resp.setDistances(Arrays.asList(1110.76, 102.78, 414.33, 211.14, 1469.96));
        resp.setTotalDistance(3308.97);
        return resp;
    }

}
