package at.ac.tuwien.sepm.assignment.individual.util.mapper;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.JockeyDto;
import org.springframework.stereotype.Component;

import java.util.LinkedList;


@Component
public class JockeyMapper {

    public Jockey jockeyDtoToJockey(JockeyDto jockeyDto){
        return new Jockey (jockeyDto.getId(), jockeyDto.getName(), jockeyDto.getSkill(), jockeyDto.getCreated(), jockeyDto.getUpdated());
    }

    public JockeyDto jockeyToJockeyDto(Jockey jockey){
        return new JockeyDto(jockey.getId(), jockey.getName(), jockey.getSkill(), jockey.getCreated(), jockey.getUpdated());
    }

    public LinkedList<JockeyDto> jockeyListToJockeyDtoList(LinkedList<Jockey> jockeys){
        LinkedList<JockeyDto> res = new LinkedList<>();
        for (Jockey x: jockeys
        ) {
            res.add(jockeyToJockeyDto(x));
        }
        return res;
    }

    public LinkedList<Jockey> jockeyDtoListToJockeyList(LinkedList<JockeyDto> jockeyDtos){
        LinkedList<Jockey> res = new LinkedList<>();
        for (JockeyDto x: jockeyDtos
        ) {
            res.add(jockeyDtoToJockey(x));
        }
        return res;
    }
}
