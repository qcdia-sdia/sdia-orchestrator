/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.qcdis.sdia.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import nl.uva.qcdis.sdia.api.NotFoundException;
import nl.uva.qcdis.sdia.dao.ToscaTemplateDAO;
import nl.uva.qcdis.sdia.model.Exceptions.TypeExeption;
import nl.uva.qcdis.sdia.model.NodeTemplate;
import nl.uva.qcdis.sdia.model.tosca.ToscaTemplate;
import nl.uva.qcdis.sdia.sure.tosca.client.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author S. Koulouzis
 */
@Service
public class ToscaTemplateService {

    private final ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Autowired
    public ToscaTemplateService() {
        this.objectMapper = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Autowired
    private ToscaTemplateDAO dao;

    public String save(ToscaTemplate tt) {
        dao.save(tt);
        return tt.getId();
    }

    public String saveFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String name = System.currentTimeMillis() + "_" + originalFileName;
        byte[] bytes = file.getBytes();
        String ymlStr = new String(bytes, "UTF-8");
        ToscaTemplate tt = objectMapper.readValue(ymlStr, ToscaTemplate.class);
        save(tt);
        return tt.getId();
    }

    public String updateToscaTemplateByID(String id, MultipartFile file) throws IOException {
        ToscaTemplate tt = dao.findById(id).get();
        if (tt == null) {
            throw new IOException("Tosca Template with id :" + id + " not found");
        }
        byte[] bytes = file.getBytes();
        String ymlStr = new String(bytes, "UTF-8");
        tt = objectMapper.readValue(ymlStr, ToscaTemplate.class);
        tt.setId(id);
        return save(tt);
    }

    public ToscaTemplate findToscaTemplateByID(String id) throws JsonProcessingException, NotFoundException {
        ToscaTemplate tt = dao.findById(id).get();
        if (tt == null) {
            java.util.logging.Logger.getLogger(ToscaTemplateService.class.getName()).log(Level.SEVERE, "ToscaTemplate with id: " + id + " not found");
            throw new NotFoundException(404, "ToscaTemplate with id: " + id + " not found");
        }
        return tt;
    }
    
        
        
    public String findByID(String id) throws JsonProcessingException, NotFoundException {
        String ymlStr = objectMapper.writeValueAsString(findToscaTemplateByID(id));
        return ymlStr;
    }
    


    public void deleteByID(String id) throws JsonProcessingException, NotFoundException, IOException, ApiException, TypeExeption {
        ToscaTemplate toscaTemplate = getYaml2ToscaTemplate(findByID(id));
        dao.deleteById(id);
    }

    public List<String> getAllIds() {
        List<String> allIds = new ArrayList<>();
        List<ToscaTemplate> all = dao.findAll();
        for (ToscaTemplate tt : all) {
            allIds.add(tt.getId());
        }
        return allIds;
    }

    void deleteAll() {
        dao.deleteAll();
    }

    public ToscaTemplate getYaml2ToscaTemplate(String ymlStr) throws IOException {
        return objectMapper.readValue(ymlStr, ToscaTemplate.class);
    }

    public ArrayList<String> findNodeIDs(Map<String, String> filters) throws JsonProcessingException {
        List<ToscaTemplate> all = dao.findAll();
        HashSet<String> matchedNodes = new HashSet<>();
        for (ToscaTemplate toscaTemplate : all) {
            Map<String, NodeTemplate> nodeTemplates = toscaTemplate.getTopologyTemplate().getNodeTemplates();
            Set<String> nodeNames = nodeTemplates.keySet();
            for (String nodeName : nodeNames) {
                NodeTemplate node = nodeTemplates.get(nodeName);
                Set<Map.Entry<String, String>> set = filters.entrySet();
                boolean allFiltersMatched = false;
                int filtersMatcheCount = 0;
                for (Map.Entry<String, String> entry : set) {
                    if (matches(node,nodeName,entry)) {
                        filtersMatcheCount++;
                    }
                }
                if (filtersMatcheCount == set.size()){
                    if (!matchedNodes.contains(toscaTemplate.getId())){
                        matchedNodes.add(toscaTemplate.getId());   
                    }
                }
            }
        }
        return new ArrayList<>(matchedNodes);
    }
    
    public List<String> findNodeIDs(String  query) throws JsonProcessingException {
        List<ToscaTemplate> all = dao.findAll();
        List<String> matches = new ArrayList<>();
        for (ToscaTemplate toscaTemplate : all) {
            matches.add(toscaTemplate.getId());
        }
        return matches;
    }
    

    private boolean matches(NodeTemplate node, String nodeName,Map.Entry<String, String> entry) {
        switch(entry.getKey()){
            case "nodeType":
             if(node.getType().equals(entry.getValue())){
                 return true;
             }
             return false;
             case "currentState":
                 if(node.getAttributes() !=null && 
                         node.getAttributes().containsKey("current_state") &&
                         node.getAttributes().get("current_state") !=null &&
                         node.getAttributes().get("current_state").equals(entry.getValue())){
                     return true;
                 }
                 return false;
             case "name":
                 if(nodeName.equals(entry.getValue())){
                     return true;
                 }
                 return false;
             default:
                 return false;      
        }
    }

}
