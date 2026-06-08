package pk.ni.pasir_piotrkowski_michal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pk.ni.pasir_piotrkowski_michal.dto.GroupResponseDto;
import pk.ni.pasir_piotrkowski_michal.dto.MembershipDto;
import pk.ni.pasir_piotrkowski_michal.dto.MembershipResponseDto;
import pk.ni.pasir_piotrkowski_michal.model.Group;
import pk.ni.pasir_piotrkowski_michal.model.Membership;
import pk.ni.pasir_piotrkowski_michal.model.User;
import pk.ni.pasir_piotrkowski_michal.repository.GroupRepository;
import pk.ni.pasir_piotrkowski_michal.service.CurrentUserService;
import pk.ni.pasir_piotrkowski_michal.service.MembershipService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class MembershipGraphQLController {
    private final MembershipService membershipService;
    private final GroupRepository groupRepository;
    private final CurrentUserService currentUserService;

    @QueryMapping
    public List<MembershipResponseDto> groupMembers(@Argument Long groupId) {
        return membershipService.getGroupMembers(groupId).stream()
                .map(membership -> new MembershipResponseDto(
                        membership.getId(),
                        membership.getUser().getId(),
                        membership.getGroup().getId(),
                        membership.getUser().getEmail()
                ))
                .collect(Collectors.toList());
    }

    @MutationMapping
    public MembershipResponseDto addMember(@Valid @Argument MembershipDto membershipDTO) {
        Membership membership = membershipService.addMember(membershipDTO);
        return new MembershipResponseDto(
                membership.getId(),
                membership.getUser().getId(),
                membership.getGroup().getId(),
                membership.getUser().getEmail()
        );
    }

    @QueryMapping
    public List<GroupResponseDto> myGroups() {
        User currentUser = currentUserService.getCurrentUser();
        return groupRepository.findByMemberships_User(currentUser).stream()
                .map(group -> new GroupResponseDto(group.getId(), group.getName(), group.getOwner().getId()))
                .collect(Collectors.toList());
    }

    @MutationMapping
    public Boolean removeMember(@Argument Long membershipId) {
        membershipService.removeMember(membershipId);
        return true;
    }
}
