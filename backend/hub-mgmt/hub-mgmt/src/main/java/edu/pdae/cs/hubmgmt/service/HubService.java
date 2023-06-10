package edu.pdae.cs.hubmgmt.service;

import edu.pdae.cs.common.util.UserWrapper;
import edu.pdae.cs.hubmgmt.model.Role;
import edu.pdae.cs.hubmgmt.model.dto.*;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface HubService {

    HubCreationResponseDTO create(HubCreationDTO hubCreationDTO, UserWrapper userWrapper);

    HubDetailsDTO getById(ObjectId id, String asUser);

    List<HubDTO> getAll(String asUser, Optional<Role> role);

    HubCreationResponseDTO update(ObjectId id, HubUpdateDTO hubUpdateDTO, String asUser);

    void deleteById(ObjectId id, String asUser);

    List<MemberDTO> getMembers(ObjectId hubId, String asUser);

    MemberDTO getMember(ObjectId hubId, String email, String asUser);

    void addMember(ObjectId hubId, MemberDTO memberDTO, UserWrapper asUser);

    void deleteMember(ObjectId hubId, String email, UserWrapper asUser);

    List<MemberDTO> getWaiters(ObjectId hubId, String asUser);

    void addWaiter(ObjectId hubId, MemberDTO memberDTO);

    void deleteWaiter(ObjectId hubId, String email, String asUser);

}
