package ru.practicum.shareit;

import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@NoArgsConstructor
public class GeneratorId {
    private int id;

    public int generate(){
        id++;
        return id;
    }
}
