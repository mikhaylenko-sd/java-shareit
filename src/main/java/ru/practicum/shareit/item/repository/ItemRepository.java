package ru.practicum.shareit.item.repository;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    List<Item> findAllByOwnerId(long ownerId, PageRequest pageRequest);

    List<Item> findAllByOwnerId(long ownerId);

    List<Item> findAllByRequestId(long requestId);
}
