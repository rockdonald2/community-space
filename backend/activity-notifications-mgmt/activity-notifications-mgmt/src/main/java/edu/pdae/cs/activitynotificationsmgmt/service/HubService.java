package edu.pdae.cs.activitynotificationsmgmt.service;

import edu.pdae.cs.activitynotificationsmgmt.model.Hub;
import org.bson.types.ObjectId;

public interface HubService {

    void createHub(ObjectId hubId, String hubName, String ownerEmail);

    void deleteHub(ObjectId hubId);

    void addMember(ObjectId hubId, String email);

    void removeMember(ObjectId hubId, String email);

    boolean isMember(ObjectId hubId, String email);

    Hub getHub(ObjectId hubId);

}
