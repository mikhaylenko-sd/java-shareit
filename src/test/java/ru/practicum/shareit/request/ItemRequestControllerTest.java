package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ParameterPaginationService;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestValidationService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemRequestController.class})
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestValidationService itemRequestValidationService;
    @MockBean
    private ParameterPaginationService parameterPaginationService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .requestorId(1L)
            .created(LocalDateTime.now())
            .description("Item request DTO description")
            .build();

    private static final String X_HEADER = "X-Sharer-User-Id";


    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.create(1L, itemRequestDto))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestorId", is(itemRequestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void getAllItemRequests() throws Exception {
        when(itemRequestService.getAllRequests(1L))
                .thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(X_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.[0].requestorId", is(itemRequestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.[0].created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(1L, 1))
                .thenReturn(itemRequestDto);
        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(X_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestorId", is(itemRequestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void getAllItemRequestsByOtherRequestors() throws Exception {
        when(itemRequestService.getAllItemRequestsByOtherRequestors(1L, 0, 10))
                .thenReturn(List.of(itemRequestDto, itemRequestDto));
        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(X_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.[0].requestorId", is(itemRequestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.[0].created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[1].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.[1].requestorId", is(itemRequestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.[1].created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testHandleItemRequestValidationExceptionWhenCreate() throws Exception {
        doThrow(new ValidationException("Ошибка валидации. Описание запроса вещи не может быть пустым."))
                .when(itemRequestValidationService).validateItemRequestCreate(itemRequestDto);
        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка при выполнении программы"), String.class))
                .andExpect(jsonPath("$.description", is("Ошибка валидации. Описание запроса вещи не может быть пустым."), String.class));
    }

    @Test
    void testHandleItemRequestItemRequestNotFoundExceptionWhenCreate() throws Exception {
        doThrow(new ItemRequestNotFoundException(1L))
                .when(itemRequestService).getItemRequestById(1L, 1L);
        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(X_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Ресурс не найден"), String.class))
                .andExpect(jsonPath("$.description", is("Запрос вещи с id = 1 не найден"), String.class));
    }


    @Test
    void testHandlePaginationExceptionWhileGetAllItemRequestsByOtherRequestors() throws Exception {
        doThrow(new ValidationException("Значения параметра запроса from не могут быть отрицательными, а size - неположительными."))
                .when(parameterPaginationService).validateRequestParameters(-2, 12);
        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(X_HEADER, 1L)
                        .param("from", "-2")
                        .param("size", "12")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка при выполнении программы"), String.class))
                .andExpect(jsonPath("$.description", is("Значения параметра запроса from не могут быть отрицательными, а size - неположительными."), String.class));
    }
}
