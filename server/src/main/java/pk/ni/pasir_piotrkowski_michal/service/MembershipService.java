package pk.ni.pasir_piotrkowski_michal.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pk.ni.pasir_piotrkowski_michal.dto.MembershipDto;
import pk.ni.pasir_piotrkowski_michal.model.Group;
import pk.ni.pasir_piotrkowski_michal.model.Membership;
import pk.ni.pasir_piotrkowski_michal.model.User;
import pk.ni.pasir_piotrkowski_michal.repository.GroupRepository;
import pk.ni.pasir_piotrkowski_michal.repository.MembershipRepository;
import pk.ni.pasir_piotrkowski_michal.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public void assertCurrentUserIsGroupMember(Long groupId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        User currentUser = currentUserService.getCurrentUser();
        assertUserIsGroupMember(groupId, currentUser.getId());
    }

    public void assertCurrentUserIsGroupOwner(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        User currentUser = currentUserService.getCurrentUser();
        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the group owner can perform this operation.");
        }
    }

    public void assertUserIsGroupMember(Long groupId, Long userId) {
        if (!membershipRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new AccessDeniedException("User is not a member of this group.");
        }
    }

    public List<Membership> getGroupMembers(Long groupId) {
        assertCurrentUserIsGroupMember(groupId);
        return membershipRepository.findByGroupId(groupId);
    }

    public boolean isUserMemberOfGroup(Long groupId, Long userId) {
        return membershipRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    public Membership addMember(MembershipDto membershipDto) {
        Group group = groupRepository.findById(membershipDto.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Group with id " + membershipDto.getGroupId() + " does not exist."));

        User currentUser = currentUserService.getCurrentUser();
        if (group.getOwner() == null || !group.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the group owner can add members.");
        }

        User userToAdd = userRepository.findByEmail(membershipDto.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("User with email " + membershipDto.getUserEmail() + " does not exist."));

        if (membershipRepository.existsByGroupIdAndUserId(group.getId(), userToAdd.getId())) {
            throw new IllegalStateException("User is already a member of this group.");
        }

        Membership membership = new Membership();
        membership.setGroup(group);
        membership.setUser(userToAdd);
        return membershipRepository.save(membership);
    }

    public void removeMember(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new EntityNotFoundException("Membership does not exist"));

        User currentUser = currentUserService.getCurrentUser();
        User groupOwner = membership.getGroup().getOwner();

        if (!currentUser.getId().equals(groupOwner.getId())) {
            throw new AccessDeniedException("Only the group owner can remove members.");
        }

        if (membership.getUser().getId().equals(groupOwner.getId())) {
            throw new IllegalStateException("Cannot remove the owner from their own group.");
        }

        membershipRepository.delete(membership);
    }
}
