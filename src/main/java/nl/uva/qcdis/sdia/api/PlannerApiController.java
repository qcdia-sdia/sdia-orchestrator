package nl.uva.qcdis.sdia.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import javax.servlet.http.HttpServletRequest;
import nl.uva.qcdis.sdia.model.Exceptions.SIDIAExeption;
import nl.uva.qcdis.sdia.service.SDIAService;
import nl.uva.qcdis.sdia.sure.tosca.client.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin(origins = "*")
public class PlannerApiController implements PlannerApi {

    private static final Logger log = LoggerFactory.getLogger(PlannerApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private SDIAService dripService;

    @org.springframework.beans.factory.annotation.Autowired
    public PlannerApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<String> planToscaTemplateByID(@ApiParam(
            value = "ID of topolog template to plan", required = true)
            @PathVariable("id") String id) {
//        String accept = request.getHeader("Accept");
//        if (accept != null && accept.contains("text/plain")) {

        try {
            String planedYemplateId = dripService.plan(id);
            java.util.logging.Logger.getLogger(PlannerApiController.class.getName()).log(Level.INFO, "Returning ID: {0}", planedYemplateId);
            return new ResponseEntity<>(planedYemplateId, HttpStatus.OK);
        } catch (NotFoundException | java.util.NoSuchElementException ex) {
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.WARNING, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (TimeoutException ex) {
            java.util.logging.Logger.getLogger(PlannerApiController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT);
        } catch (ApiException | IOException | InterruptedException ex) {
            java.util.logging.Logger.getLogger(PlannerApiController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (SIDIAExeption ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
//        }
    }

}
