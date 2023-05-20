package edu.pdae.cs.memomgmt.service;

import org.bson.types.ObjectId;

public interface HubService {

    void createHub(ObjectId hubId, String ownerEmail);

    void deleteHub(ObjectId hubId);

    void addMember(ObjectId hubId, String email);

    void removeMember(ObjectId hubId, String email);

    boolean isMember(ObjectId hubId, String email);

}
