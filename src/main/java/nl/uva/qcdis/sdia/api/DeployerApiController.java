package nl.uva.qcdis.sdia.api;

import com.fasterxml.jackson.core.JsonProcessingException;
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

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-10-10T17:15:46.465Z")

@Controller
@CrossOrigin(origins = "*")
public class DeployerApiController implements DeployerApi {

    private static final Logger log = LoggerFactory.getLogger(DeployerApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private SDIAService dripService;

    @org.springframework.beans.factory.annotation.Autowired
    public DeployerApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<String> deployProvisionToscaTemplateByID(
            @ApiParam(value = "ID of topolog template to deploy", required = true)
            @PathVariable("id") String id) {

//        String accept = request.getHeader("Accept");
//        if (accept != null && accept.contains("")) {
        try {
            String planedYemplateId = dripService.deploy(id, null);
            java.util.logging.Logger.getLogger(DeployerApiController.class.getName()).log(Level.INFO, "Returning ID : {0}", new Object[]{planedYemplateId});
            return new ResponseEntity<>(planedYemplateId, HttpStatus.OK);

//            } catch (Exception ex) {
//                java.util.logging.Logger.getLogger(DeployerApiController.class.getName()).log(Level.SEVERE, null, ex);
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (java.util.NoSuchElementException | NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ApiException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TimeoutException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (SIDIAExeption ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        } 
      
    }

//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
//        }
}
