package nl.uva.qcdis.sdia.api;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.uva.qcdis.sdia.service.ToscaTemplateService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import nl.uva.qcdis.sdia.commons.utils.Constants;
import nl.uva.qcdis.sdia.model.Exceptions.MissingVMTopologyException;
import nl.uva.qcdis.sdia.model.Exceptions.SIDIAExeption;
import nl.uva.qcdis.sdia.model.Exceptions.TypeExeption;
import nl.uva.qcdis.sdia.service.SDIAService;
import nl.uva.qcdis.sdia.sure.tosca.client.ApiException;
import org.springframework.beans.factory.annotation.Autowired;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-10-10T17:15:46.465Z")

@Controller
@CrossOrigin(origins = "*")
public class ToscaTemplateApiController implements ToscaTemplateApi {

    private static final Logger log = LoggerFactory.getLogger(ToscaTemplateApiController.class);

    private final HttpServletRequest request;

    @Autowired
    private ToscaTemplateService toscaTemplateService;

    @Autowired
    private SDIAService sdiaService;

    @org.springframework.beans.factory.annotation.Autowired
    public ToscaTemplateApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public ResponseEntity<String> deleteToscaTemplateByID(
            @ApiParam(value = "ID of topology template to return", required = true)
            @PathVariable("id") String id, @ApiParam(value = "The node(s) to delete")
            @Valid @RequestParam(value = "node_names", required = false) List<String> nodeName) {
//        String accept = request.getHeader("Accept");
//        if (accept != null && accept.contains("text/plain")) {
        try {
            sdiaService.delete(id, nodeName);
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.INFO, "Returning delete id: " + id);
            return new ResponseEntity<>(id, HttpStatus.OK);
        } catch (IOException | ApiException | TypeExeption | TimeoutException | InterruptedException ex) {
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (MissingVMTopologyException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (SIDIAExeption ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
//        }
    }

    @Override
    public ResponseEntity<String> deleteAllToscaTemplates() {
        List<String> ids = toscaTemplateService.getAllIds();
        try {
            for (String id : ids) {
                sdiaService.delete(id, null);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException | ApiException | TypeExeption | TimeoutException | InterruptedException ex) {
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (MissingVMTopologyException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (SIDIAExeption ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> getToscaTemplateByID(@ApiParam(value = "ID of topolog template to return", required = true)
            @PathVariable("id") String id) {
        try {
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.INFO, "Requestsed ID: {0}", id);
            Constants.NODE_STATES staus = sdiaService.processQueue(id);
            String ymlStr = toscaTemplateService.findByID(id);
            switch (staus) {
                case CREATED:
                    return new ResponseEntity<>(ymlStr, HttpStatus.CREATED);
                case CREATING:
                    return new ResponseEntity<>(ymlStr, HttpStatus.ACCEPTED);
                case FAILED:
                    return new ResponseEntity<>(ymlStr, HttpStatus.INTERNAL_SERVER_ERROR);
                default:
                    return new ResponseEntity<>(ymlStr, HttpStatus.OK);
            }

        } catch (NotFoundException | java.util.NoSuchElementException ex) {
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.WARNING, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException ex) {
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException | TimeoutException | InterruptedException | ApiException | TypeExeption ex) {
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }catch(SIDIAExeption ex){
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> updateToscaTemplateByID(@ApiParam(
            value = "ID of topolog template to return", required = true)
            @PathVariable("id") String id, @ApiParam(value = "file detail")
            @Valid @RequestPart("file") MultipartFile file) {
        try {
            id = toscaTemplateService.updateToscaTemplateByID(id, file);
            return new ResponseEntity<>(id, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Couldn't serialize response for content type ", e);
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.SEVERE, null, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> uploadToscaTemplate(@ApiParam(value = "file detail") @Valid @RequestPart("file") MultipartFile file) {
//        String accept = request.getHeader("Accept");
//        if (accept != null && accept.contains("*/*")) {
        try {
            String id = toscaTemplateService.saveFile(file);
            return new ResponseEntity<>(id, HttpStatus.OK);
        } catch (com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException ex) {
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.SEVERE, null, ex);
            log.error("Couldn't serialize response for content type application/json", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.SEVERE, null, e);
            log.error("Couldn't serialize response for content type application/json", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
//        }

    }

    @Override
    public ResponseEntity<List<String>> getToscaTemplateIDs() {
//        String accept = request.getHeader("Accept");
//        if (accept != null && accept.contains("application/json")) {
        List<String> ids = toscaTemplateService.getAllIds();
        return new ResponseEntity<>(ids, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
//        }

    }

    @Override
    public ResponseEntity<List<String>> getToscaTemplateID(
            @RequestParam(required = false) String nodeType,
            @RequestParam(required = false) String currentState,
            @RequestParam(required = false) String name) {
        try {
            Map<String, String> filters = new HashMap<>();
            if (nodeType != null) {
                filters.put("nodeType", nodeType);
            }
            if (currentState != null) {
                filters.put("currentState", currentState);
            }
            if (name != null) {
                filters.put("name", name);
            }
            return new ResponseEntity<>(toscaTemplateService.findNodeIDs(filters), HttpStatus.OK);
        } catch (JsonProcessingException ex) {
            java.util.logging.Logger.getLogger(ToscaTemplateApiController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
