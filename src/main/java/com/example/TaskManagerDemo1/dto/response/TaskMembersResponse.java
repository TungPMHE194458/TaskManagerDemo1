package com.example.TaskManagerDemo1.dto.response;


import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskMembersResponse {

    private MemberResponse owner;
    private List<MemberResponse> members;
}
