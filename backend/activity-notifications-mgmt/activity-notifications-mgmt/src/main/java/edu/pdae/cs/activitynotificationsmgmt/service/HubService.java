package edu.pdae.cs.activitynotificationsmgmt.service;

import edu.pdae.cs.activitynotificationsmgmt.model.Hub;
import edu.pdae.cs.common.model.dto.HubMutationDTO;
import org.bson.types.ObjectId;

public interface HubService {

    void createHub(HubMutationDTO hubMutationDTO);

    void deleteHub(ObjectId hubId);

    void addMember(ObjectId hubId, String email);

    void removeMember(ObjectId hubId, String email);

    boolean isMember(ObjectId hubId, String email);

    Hub getHub(ObjectId hubId);

}
