package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ParameterPaginationService;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemValidationService;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class})
class ItemControllerTest {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    @MockBean
    private ItemService itemService;
    @MockBean
    private ItemValidationService itemValidationService;
    @MockBean
    private ParameterPaginationService parameterPaginationService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private UserDto userOwnerDto = UserDto
            .builder()
            .id(1L)
            .email("userOwner@ya.ru")
            .name("userOwnerName")
            .build();
    private ItemDto itemDto = ItemDto
            .builder()
            .id(1L)
            .name("ItemName")
            .description("description")
            .ownerId(null)
            .available(true)
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();
    private ItemDto itemDto2 = ItemDto
            .builder()
            .id(2L)
            .name("ItemName")
            .description("description")
            .ownerId(null)
            .available(true)
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    private final CommentDto commentDto = CommentDto
            .builder()
            .id(1L)
            .text("text")
            .authorName(userOwnerDto.getName())
            .itemId(itemDto.getId())
            .created(LocalDateTime.of(2023, 11, 11, 11, 11))
            .build();

    private final List<ItemDto> listItemDto = new ArrayList<>();

    @Test
    public void testCreateItem() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(REQUEST_HEADER, 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void testPatchItem() throws Exception {
        when(itemService.update(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void testFindAllItems() throws Exception {
        when(itemService.getAllByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(mapper.writeValueAsString(listItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.[0].ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void testFindItemByItemId() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void testSearchItems() throws Exception {
        when(itemService.searchItemsByText(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(listItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.[0].ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void testAddComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testRemoveItemById() throws Exception {
        mvc.perform(delete("/items/1")
                        .header(REQUEST_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteItem() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(REQUEST_HEADER, 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

    }

    @Test
    void test() throws Exception {
        mvc.perform(delete("/items")
                        .header(REQUEST_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }
}