package pk.ni.pasir_piotrkowski_michal.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;

import pk.ni.pasir_piotrkowski_michal.dto.GroupDto;
import pk.ni.pasir_piotrkowski_michal.model.Group;
import pk.ni.pasir_piotrkowski_michal.model.Membership;
import pk.ni.pasir_piotrkowski_michal.model.User;
import pk.ni.pasir_piotrkowski_michal.repository.GroupRepository;
import pk.ni.pasir_piotrkowski_michal.repository.MembershipRepository;
import pk.ni.pasir_piotrkowski_michal.repository.DebtRepository;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final DebtRepository debtRepository;
    private final CurrentUserService currentUserService;

    public List<Group> getAllGroups() {
        User currentUser = currentUserService.getCurrentUser();
        return groupRepository.findByMemberships_User(currentUser);
    }

    public Group createGroup(GroupDto groupDTO) {
        User owner = currentUserService.getCurrentUser(); //get currently logged user

        Group group = new Group();
        group.setName(groupDTO.getName());
        group.setOwner(owner);

        Group savedGroup = groupRepository.save(group);

        Membership membership = new Membership();
        membership.setUser(owner);
        membership.setGroup(savedGroup);

        membershipRepository.save(membership);

        return savedGroup;
    }

    @Transactional
    public void deleteGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "You cannot delete group with id " + id + " because it does not exist."
                ));

        User currentUser = currentUserService.getCurrentUser();
        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You cannot delete group with id " + id + " because you are not the owner.");
        }

        debtRepository.deleteByGroupId(id);
        membershipRepository.deleteByGroupId(id);
        groupRepository.delete(group);
    }
}
