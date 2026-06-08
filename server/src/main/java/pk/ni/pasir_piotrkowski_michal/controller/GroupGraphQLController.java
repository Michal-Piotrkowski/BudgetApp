package pk.ni.pasir_piotrkowski_michal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pk.ni.pasir_piotrkowski_michal.dto.GroupDto;
import pk.ni.pasir_piotrkowski_michal.model.Group;
import pk.ni.pasir_piotrkowski_michal.service.GroupService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class GroupGraphQLController {
    private final GroupService groupService;

    @QueryMapping
    public List<Group> groups() {
        return groupService.getAllGroups();
    }

    @MutationMapping
    public Group createGroup(@Valid @Argument GroupDto groupDTO) {
        return groupService.createGroup(groupDTO);
    }

    @MutationMapping
    public Boolean deleteGroup(@Argument Long id) {
        groupService.deleteGroup(id);
        return true;
    }
}
