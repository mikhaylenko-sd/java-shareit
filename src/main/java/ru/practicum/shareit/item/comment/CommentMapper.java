package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentDto commentDto, UserDto user, ItemDto item) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(UserMapper.toUser(user))
                .item(ItemMapper.toItem(item))
                .created(commentDto.getCreated())
                .build();
    }
}
