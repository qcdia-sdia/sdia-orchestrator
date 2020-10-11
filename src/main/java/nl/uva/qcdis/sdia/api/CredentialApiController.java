package nl.uva.qcdis.sdia.api;

import nl.uva.qcdis.sdia.model.tosca.Credential;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import nl.uva.qcdis.sdia.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-10-10T17:15:46.465Z")

@CrossOrigin(origins = "*")
@Controller
public class CredentialApiController implements CredentialApi {

    private static final Logger log = LoggerFactory.getLogger(CredentialApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private CredentialService credentialService;

    @org.springframework.beans.factory.annotation.Autowired
    public CredentialApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<String> createCredentials(@ApiParam(
            value = "Created user object", required = true)
            @Valid @RequestBody Credential body) {
//        String accept = request.getHeader("Accept");
//        if (accept != null && accept.contains("application/json")) {
            try {
                String id = credentialService.save(body);
                return new ResponseEntity<>(id, HttpStatus.OK);
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
//        }

    }

    @Override
    public ResponseEntity<List<String>> getCredentialIDs() {
//        String accept = request.getHeader("Accept");
//        if (accept != null && accept.contains("application/json")) {
            List<String> ids = credentialService.getAllIds();
            return new ResponseEntity<>(ids, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
