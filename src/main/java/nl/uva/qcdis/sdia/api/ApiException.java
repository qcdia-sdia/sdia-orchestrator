package nl.uva.qcdis.sdia.api;

import org.springframework.http.ResponseEntity;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-12-06T13:31:49.386Z")

public class ApiException extends Exception{

    static ResponseEntity<String> handleExceptions(ApiException ex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private int code;
    public ApiException (int code, String msg) {
        super(msg);
        this.code = code;
    }
}
