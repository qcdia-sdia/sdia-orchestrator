
package nl.uva.qcdis.sdia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.qcdis.sdia.api.NotFoundException;
import nl.uva.qcdis.sdia.commons.utils.ToscaHelper;
import nl.uva.qcdis.sdia.commons.utils.Constants.NODE_STATES;
import nl.uva.qcdis.sdia.commons.utils.Converter;
import nl.uva.qcdis.sdia.model.Exceptions.MissingCredentialsException;
import nl.uva.qcdis.sdia.model.Exceptions.MissingVMTopologyException;
import nl.uva.qcdis.sdia.model.Exceptions.TypeExeption;
import nl.uva.qcdis.sdia.model.Message;
import nl.uva.qcdis.sdia.model.NodeTemplateMap;
import nl.uva.qcdis.sdia.model.tosca.Credential;
import nl.uva.qcdis.sdia.model.tosca.ToscaTemplate;
import nl.uva.qcdis.sdia.rpc.SDIACaller;
import nl.uva.qcdis.sdia.sure.tosca.client.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author S. Koulouzis
 */
@Service
public class SDIAService {

    @Autowired
    private ToscaTemplateService toscaTemplateService;

    @Autowired
    SDIACaller caller;

    @Autowired
    CredentialService credentialService;

    @Autowired
    private ToscaHelper helper;

    @Autowired
    ProvisionerService provisionerService;

    @Value("${message.broker.queue.provisioner}")
    private String provisionerQueueName;

    @Value("${message.broker.queue.planner}")
    private String plannerQueueName;

    @Value("${message.broker.queue.deployer}")
    private String deployerQueueName;
    public static final String[] OS_PROVIDERS = new String[]{"INFN", 
        "CESGA","EGI","Azure"};

    private String execute(ToscaTemplate toscaTemplate, String requestQeueName) throws IOException, TimeoutException, InterruptedException{
        try {
            caller.init();
//            Logger.getLogger(SDIAService.class.getName()).log(Level.INFO, "toscaTemplate:\n{0}", toscaTemplate);
            Message message = new Message();
            message.setOwner("user");
            message.setCreationDate(System.currentTimeMillis());
            message.setToscaTemplate(toscaTemplate);
            
            caller.setRequestQeueName(requestQeueName);
            Message response = caller.call(message);
            ToscaTemplate updatedToscaTemplate = response.getToscaTemplate();
            return toscaTemplateService.save(updatedToscaTemplate);
        } catch (IOException | TimeoutException | InterruptedException ex) {
            throw ex;
        }finally{
            try {
                caller.close();
            } catch (IOException | TimeoutException ex) {
                Logger.getLogger(SDIAService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private Credential getBestCredential(List<Credential> credentials) {
        return credentials.get(0);
    }

    protected ToscaTemplate addCredentials(ToscaTemplate toscaTemplate) throws MissingCredentialsException, ApiException, TypeExeption, MissingVMTopologyException, JsonProcessingException {
        List<NodeTemplateMap> vmTopologies = helper.getVMTopologyTemplates();
        if (vmTopologies == null) {
            throw new MissingVMTopologyException("ToscaTemplate: " + toscaTemplate + " has no VM topology");
        }
        List<Credential> credentials;
        for (NodeTemplateMap vmTopologyMap : vmTopologies) {
            String provider = helper.getTopologyProvider(vmTopologyMap);
            if (needsCredentials(provider)) {
                credentials = credentialService.findByProvider(provider);
                if (credentials == null || credentials.size() <= 0) {
                    throw new MissingCredentialsException("Provider: " + provider + " needs credentials but non clould be found");
                } else {
                    Credential credential = getBestCredential(credentials);
                    vmTopologyMap = helper.setCredentialsInVMTopology(vmTopologyMap, credential);
                    toscaTemplate = helper.setNodeInToscaTemplate(toscaTemplate, vmTopologyMap);
                }
            }

        }
        Logger.getLogger(SDIAService.class.getName()).log(Level.INFO, "Added credetials to ToscaTemplate");
        Logger.getLogger(SDIAService.class.getName()).log(Level.INFO, Converter.toYAML(toscaTemplate));
        
        return toscaTemplate;
    }

    public String plan(String id) throws ApiException, NotFoundException, IOException, JsonProcessingException, TimeoutException, InterruptedException {
        ToscaTemplate toscaTemplate = initExecution(id);
        return execute(toscaTemplate, plannerQueueName);
    }

    public String provision(String id) throws MissingCredentialsException, ApiException, TypeExeption, IOException, JsonProcessingException, TimeoutException, InterruptedException, NotFoundException, MissingVMTopologyException {
        ToscaTemplate toscaTemplate = initExecution(id);
        toscaTemplate = addCredentials(toscaTemplate);
        //Update ToscaTemplate so we can include the credentials
        helper.uploadToscaTemplate(toscaTemplate);
        List<NodeTemplateMap> vmTopologies = helper.getVMTopologyTemplates();
        if (vmTopologies == null || vmTopologies.isEmpty()) {
            throw new MissingVMTopologyException("ToscaTemplate: " + toscaTemplate + " has no VM Topologies");
        }
        for (NodeTemplateMap vmTopology : vmTopologies) {
            toscaTemplate = setDesieredSate(toscaTemplate, vmTopology, NODE_STATES.RUNNING);
        }
        String queueName = getQueueName(toscaTemplate);
        return execute(toscaTemplate, queueName);
    }

    protected ToscaTemplate setDesieredSate(ToscaTemplate toscaTemplate,
            NodeTemplateMap node, NODE_STATES nodeState) throws IOException, JsonProcessingException, ApiException {
        NODE_STATES currentState = helper.getNodeCurrentState(node);
        NODE_STATES desiredState = helper.getNodeDesiredState(node);
        node = helper.setNodeDesiredState(node, nodeState);
        toscaTemplate = helper.setNodeInToscaTemplate(toscaTemplate, node);
        return toscaTemplate;
    }

    private boolean needsCredentials(String provider) {
        switch (provider) {
            case "local":
                return false;
            default:
                return true;
        }
    }

    public String deploy(String id, List<String> nodeNames) throws JsonProcessingException, NotFoundException, IOException, ApiException, Exception {
        ToscaTemplate toscaTemplate = initExecution(id);
        //If no nodes are specified deploy all applications
        if (nodeNames == null || nodeNames.isEmpty()) {
            List<NodeTemplateMap> applicationTemplates = helper.getApplicationTemplates();
            for (NodeTemplateMap applicationTemplate : applicationTemplates) {
                toscaTemplate = setDesieredSate(toscaTemplate, applicationTemplate, NODE_STATES.RUNNING);
            }
        }
        return execute(toscaTemplate, deployerQueueName);
    }

    protected ToscaTemplate initExecution(String id) throws JsonProcessingException, NotFoundException, IOException, ApiException {
        String ymlToscaTemplate = toscaTemplateService.findByID(id);
        Logger.getLogger(SDIAService.class.getName()).log(Level.FINE, "Found ToscaTemplate with id: {0}", id);
        ToscaTemplate toscaTemplate = toscaTemplateService.getYaml2ToscaTemplate(ymlToscaTemplate);
        helper.uploadToscaTemplate(toscaTemplate);
        return toscaTemplate;
    }

    public String delete(String id, List<String> nodeNames) throws NotFoundException, IOException, JsonProcessingException, ApiException, TypeExeption, TimeoutException, InterruptedException {
        ToscaTemplate toscaTemplate = initExecution(id);
        boolean nothingToDelete = true;
        //If no nodes are specified delete all the infrastructure
        if (nodeNames == null || nodeNames.isEmpty()) {
            List<NodeTemplateMap> vmTopologies = helper.getVMTopologyTemplates();
            if (vmTopologies != null) {
                for (NodeTemplateMap vmTopology : vmTopologies) {
                    NODE_STATES currentState = helper.getNodeCurrentState(vmTopology);
                    if (currentState != null && currentState != NODE_STATES.DELETED) {
                        nothingToDelete = false;
                        toscaTemplate = setDesieredSate(toscaTemplate, vmTopology, NODE_STATES.DELETED);
                    }
                }
                if (!nothingToDelete) {
                    this.toscaTemplateService.deleteByID(id);
                    return execute(toscaTemplate, provisionerQueueName);
                }
            }
            return id;
        } else {

        }

        return null;
    }

    private String getQueueName(ToscaTemplate toscaTemplate) throws ApiException, MissingVMTopologyException, TypeExeption {
                List<NodeTemplateMap> vmTopologies = helper.getVMTopologyTemplates();
        if (vmTopologies == null) {
            throw new MissingVMTopologyException("ToscaTemplate: " + toscaTemplate + " has no VM topology");
        }
        for (NodeTemplateMap vmTopologyMap : vmTopologies) {
            String provider = helper.getTopologyProvider(vmTopologyMap);
            for(String osProvider: OS_PROVIDERS){
                if(osProvider.toUpperCase().equals(provider)){
                    return deployerQueueName;
                }
            }
            return provisionerQueueName;
    }
    return provisionerQueueName;
    }
}
