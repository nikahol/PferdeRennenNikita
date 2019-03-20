package at.ac.tuwien.sepm.assignment.individual.exceptions;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class BadRequestException extends Exception {
    private static final Logger LOGGER = LoggerFactory.getLogger(BadRequestException.class);
    public BadRequestException(String msg){
        super(msg);
        LOGGER.error("Error: " + msg);
    }
}
