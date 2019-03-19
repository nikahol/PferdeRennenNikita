package at.ac.tuwien.sepm.assignment.individual.util.mapper;

import at.ac.tuwien.sepm.assignment.individual.rest.dto.HorseDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class HorseMapper {

    public HorseDto entityToDto(Horse horse) {
        return new HorseDto(horse.getId(), horse.getName(), horse.getBreed(), horse.getMinSpeed(), horse.getMaxSpeed(), horse.getCreated(), horse.getUpdated(), horse.isDeleted());
    }
    public Horse dtoToEntity(HorseDto horse) {
        return new Horse(horse.getId(), horse.getName(), horse.getBreed(), horse.getMinSpeed(), horse.getMaxSpeed(), horse.getCreated(), horse.getUpdated(), horse.isDeleted());
    }
    public LinkedList<Horse> horseDtoListToHorseList(LinkedList<HorseDto> horseDtos){
        LinkedList<Horse> res = new LinkedList<>();
        for (HorseDto x: horseDtos
        ) {
            res.add(dtoToEntity(x));
        }
        return res;
    }

    public LinkedList<HorseDto> horseListToHorseDtoList(LinkedList<Horse> horses) {
        LinkedList<HorseDto> res = new LinkedList<>();
        for (Horse x : horses){
            res.add(entityToDto(x));
        }
        return res;
    }
}
