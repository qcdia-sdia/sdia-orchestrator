/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.qcdis.sdia.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import nl.uva.qcdis.sdia.commons.utils.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import nl.uva.qcdis.sdia.dao.CredentialDAO;
import nl.uva.qcdis.sdia.model.tosca.Credential;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author S. Koulouzis
 */
@Service
public class CredentialService {

    @Value("${credential.secret}")
    private String credentialSecret;

    @Autowired
    private CredentialDAO dao;

    public String save(Credential document) throws UnsupportedEncodingException, 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        dao.save(Converter.encryptCredential(document,credentialSecret));
        return document.getId();
    }

    public Credential findByID(String id) {
        Credential credential = dao.findById(id).get();
        return credential;
    }

    public void deleteByID(String id) {
        dao.deleteById(id);
    }

    public List<String> getAllIds() {
        List<String> allIds = new ArrayList<>();
        List<Credential> all = dao.findAll();
        for (Credential tt : all) {
            allIds.add(tt.getId());
        }
        return allIds;
    }

    void deleteAll() {
        dao.deleteAll();
    }

    List<Credential> findByProvider(String provider) {
        return dao.findBycloudProviderName(provider);
    }



}
