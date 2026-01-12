package com.example.TaskManagerDemo1.service;

import com.example.TaskManagerDemo1.dto.request.TaskAddRequest;
import com.example.TaskManagerDemo1.dto.request.TaskUpdateRequest;
import com.example.TaskManagerDemo1.dto.response.*;
import com.example.TaskManagerDemo1.entity.Tasks;
import com.example.TaskManagerDemo1.entity.UserTask;
import com.example.TaskManagerDemo1.entity.Users;
import com.example.TaskManagerDemo1.enums.MemberStatus;
import com.example.TaskManagerDemo1.enums.Role;
import com.example.TaskManagerDemo1.enums.TaskRole;
import com.example.TaskManagerDemo1.exception.AppException;
import com.example.TaskManagerDemo1.exception.ErrorCode;
import com.example.TaskManagerDemo1.repository.TaskRepository;
import com.example.TaskManagerDemo1.repository.UserRepository;
import com.example.TaskManagerDemo1.repository.UserTaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserTaskRepository userTaskRepository;
    /*
    * POST GET done
    * PUT DELETE error
    * subtask not work
    * */
    /* ===================== COMMON ===================== */

    private Users getCurrentUser() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private UserTask getUserTaskOrThrow(Tasks task, Users user) {
        return userTaskRepository.findByUserAndTask(user, task)
                .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN));
    }

    private UserTask checkAcceptedMember(Tasks task, Users user) {

        UserTask ut = getUserTaskOrThrow(task, user);

        if (ut.getStatus() != MemberStatus.ACCEPTED)
            throw new AppException(ErrorCode.FORBIDDEN);
        return ut;
    }


    /* ===================== MAPPING ===================== */

    private UserBrief toUserBrief(Users user) {
        return UserBrief.builder()
                .id(user.getID())
                .username(user.getUsername())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .build();
    }

    private MemberResponse toMemberResponse(Users user) {
        return MemberResponse.builder()
                .id(user.getID())
                .username(user.getUsername())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .build();
    }

    private TaskResponse mapToResponse(Tasks task) {

        // OWNER
        UserBrief owner = userTaskRepository
                .findFirstByTaskAndRole(task, TaskRole.OWNER)
                .map(ut -> toUserBrief(ut.getUser()))
                .orElse(null);

        // ===== MEMBERS =====
        List<UserBrief> members = userTaskRepository
                .findByTaskAndRole(task, TaskRole.MEMBER)
                .stream()
                .map(ut -> toUserBrief(ut.getUser()))
                .toList();



        return TaskResponse.builder()
                .ID(task.getID())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .deadline(task.getDeadline())

                .owner(owner)
                .members(members)
                .parentTaskID(
                        task.getParentTask() != null
                                ? task.getParentTask().getID()
                                : null
                )

                .subTasks(
                        task.getSubTasks() == null
                                ? List.of()
                                : task.getSubTasks()
                                .stream()
                                .map(this::mapToResponse) // ĐỆ QUY
                                .toList()
                )
                .build();
    }


    /* ===================== CREATE ===================== */

    /**
     * Người tạo = OWNER
     */
    public ApiResponse<TaskResponse> createTask(TaskAddRequest request) {

        Users user = getCurrentUser();

        Tasks task = new Tasks();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());

        // parent task (optional)
        if (request.getParentTaskId() != null) {
            Tasks parent = taskRepository.findById(request.getParentTaskId())
                    .orElseThrow(() -> new AppException(ErrorCode.PARENT_TASK_NOT_FOUND));
            task.setParentTask(parent);
        }

        taskRepository.save(task);

        // create OWNER relation
        UserTask owner = UserTask.builder()
                .user(user)
                .task(task)
                .role(TaskRole.OWNER)
                .status(MemberStatus.ACCEPTED)
                .build();

        userTaskRepository.save(owner);

        return ApiResponse.success(mapToResponse(task));
    }

    @Transactional
    public ApiResponse<String> acceptTask(int taskId) {

        Users currentUser = getCurrentUser();

        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        UserTask ut = userTaskRepository
                .findByTaskAndUserAndStatus(
                        task,
                        currentUser,
                        MemberStatus.PENDING
                )
                .orElseThrow(() -> new AppException(ErrorCode.INVITATION_NOT_FOUND));

        ut.setStatus(MemberStatus.ACCEPTED);

        return ApiResponse.success("Task accepted");
    }
    @Transactional
    public ApiResponse<String> rejectTask(int taskId) {

        Users currentUser = getCurrentUser();

        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        UserTask ut = userTaskRepository
                .findByTaskAndUserAndStatus(
                        task,
                        currentUser,
                        MemberStatus.PENDING
                )
                .orElseThrow(() -> new AppException(ErrorCode.INVITATION_NOT_FOUND));

        ut.setStatus(MemberStatus.REJECTED);

        return ApiResponse.success("Task rejected");
    }

    /* ===================== READ ===================== */

    /**
     * GET TASK BY ID
     * Chỉ OWNER / MEMBER / ADMIN
     */
    @PreAuthorize("@taskSecurity.isTaskMember(#id, authentication) or hasRole('ADMIN')")
    public ApiResponse<TaskResponse> getTaskById(int id) {

        Tasks task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        return ApiResponse.success(mapToResponse(task));
    }

    /**
     * GET ALL TASKS (ADMIN)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<TaskResponse>> getAllTasks() {

        return ApiResponse.success(
                taskRepository.findAll()
                        .stream()
                        .map(this::mapToResponse)
                        .toList()
        );
    }

    /**
     * GET MY ROOT TASKS
     */

    public ApiResponse<List<TaskResponse>> getMyRootTasks() {

        Users user = getCurrentUser();

        return ApiResponse.success(
                userTaskRepository
                        .findByUser(user)
                        .stream()
                        .filter(ut ->
                                ut.getRole() == TaskRole.OWNER ||
                                        ut.getStatus() == MemberStatus.ACCEPTED
                        )
                        .map(UserTask::getTask)
                        .filter(task -> task.getParentTask() == null)
                        .map(this::mapToResponse)
                        .toList()
        );
    }

    /**
     * GET SUB TASKS
     */
    public ApiResponse<List<TaskResponse>> getSubTasks(int parentId) {

        Tasks parent = taskRepository.findById(parentId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        Users user = getCurrentUser();
        checkAcceptedMember(parent, user); // check access

        return ApiResponse.success(
                parent.getSubTasks()
                        .stream()
                        .map(this::mapToResponse)
                        .toList()
        );
    }
    /*
    * GET INTITATION
     */
    public ApiResponse<List<TaskResponse>> getMyInvitations() {

        Users currentUser = getCurrentUser();

        List<TaskResponse> invitations = userTaskRepository
                .findByUserAndStatus(currentUser, MemberStatus.PENDING)
                .stream()
                .map(UserTask::getTask)
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success(invitations);
    }

    /* ===================== UPDATE ===================== */

    /**
     * UPDATE TASK
     * Chỉ OWNER
     */
    @Transactional
    public ApiResponse<TaskResponse> updateTask(
            int taskId,
            TaskUpdateRequest request) {

        Users user = getCurrentUser();

        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        UserTask ut = checkAcceptedMember(task, user);
        if (ut.getRole() != TaskRole.OWNER)
            throw new AppException(ErrorCode.FORBIDDEN);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());

        taskRepository.save(task);

        return ApiResponse.success(mapToResponse(task));
    }



    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> addMember(int taskId, int userId) {

        Users owner = getCurrentUser();
        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        UserTask ownerUT = getUserTaskOrThrow(task, owner);
        if (ownerUT.getRole() != TaskRole.OWNER)
            throw new AppException(ErrorCode.FORBIDDEN);

        Users member = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserTask ut = new UserTask();
        ut.setUser(member);
        ut.setTask(task);
        ut.setRole(TaskRole.MEMBER);

        if (userTaskRepository.findByUserAndTask(member, task).isPresent()) {
            throw new AppException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }
        return ApiResponse.success("Member added");
    }
    @Transactional
    public ApiResponse<String> inviteMember(int taskId, int userId) {

        Users inviter = getCurrentUser();

        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        // check quyền
        if (!inviter.getRoles().contains(Role.ADMIN.name())) {
            UserTask ut = userTaskRepository
                    .findByTaskAndUserAndStatus(
                            task,
                            inviter,
                            MemberStatus.ACCEPTED
                    )
                    .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN));

            if (ut.getRole() != TaskRole.OWNER)
                throw new AppException(ErrorCode.FORBIDDEN);
        }

        Users invitedUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // kiểm tra đã từng tồn tại UserTask chưa
        Optional<UserTask> existingUT =
                userTaskRepository.findByTaskAndUser(task, invitedUser);

        if (existingUT.isPresent()) {
            UserTask ut = existingUT.get();

            switch (ut.getStatus()) {
                case PENDING:
                    throw new AppException(ErrorCode.USER_ALREADY_INVITED);

                case ACCEPTED:
                    throw new AppException(ErrorCode.USER_ALREADY_IN_TASK);

                case REJECTED:
                    // mời lại → reset status
                    ut.setStatus(MemberStatus.PENDING);
                    ut.setRole(TaskRole.MEMBER);
                    userTaskRepository.save(ut);
                    return ApiResponse.success("Invitation re-sent");
            }
        }


        UserTask newUT = UserTask.builder()
                .task(task)
                .user(invitedUser)
                .role(TaskRole.MEMBER)
                .status(MemberStatus.PENDING)
                .build();

        userTaskRepository.save(newUT);

        return ApiResponse.success("Invitation sent");
    }




    public ApiResponse<TaskMembersResponse> getMembers(int taskId) {

        Users currentUser = getCurrentUser();

        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        if (!currentUser.getRoles().contains(Role.ADMIN.name())) {
            // check quyền: phải ACCEPTED
            userTaskRepository
                    .findByTaskAndUserAndStatus(
                            task,
                            currentUser,
                            MemberStatus.ACCEPTED
                    )
                    .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN));
        }

        // OWNER
        MemberResponse owner =
                userTaskRepository
                        .findByTaskAndRoleAndStatus(
                                task,
                                TaskRole.OWNER,
                                MemberStatus.ACCEPTED
                        )
                        .map(UserTask::getUser)
                        .map(this::toMemberResponse)
                        .orElseThrow(() ->
                                new AppException(ErrorCode.OWNER_NOT_FOUND)
                        );

        // MEMBERS
        List<MemberResponse> members =
                userTaskRepository
                        .findByTaskAndRoleAndStatus(
                                task,
                                TaskRole.MEMBER,
                                MemberStatus.ACCEPTED
                        )
                        .stream()
                        .map(UserTask::getUser)
                        .map(this::toMemberResponse)
                        .toList();

        return ApiResponse.success(
                TaskMembersResponse.builder()
                        .owner(owner)
                        .members(members)
                        .build()
        );
    }

    /* ===================== DELETE ===================== */

    /**
     * DELETE TASK
     * Chỉ OWNER mới được xóa
     */
    @Transactional
    public ApiResponse<String> deleteTask(int taskId) {

        Users currentUser = getCurrentUser();

        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        // ADMIN được xóa luôn
        if (!currentUser.getRoles().contains(Role.ADMIN.name())) {

            UserTask ut = userTaskRepository
                    .findByTaskAndUserAndStatus(
                            task,
                            currentUser,
                            MemberStatus.ACCEPTED
                    )
                    .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN));

            if (ut.getRole() != TaskRole.OWNER)
                throw new AppException(ErrorCode.FORBIDDEN);
        }

        taskRepository.delete(task);
        return ApiResponse.success("Task deleted successfully");
    }






    @Transactional
    public ApiResponse<String> removeMember(int taskId, int userId) {

        Users currentUser = getCurrentUser();

        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        // check quyền OWNER hoặc ADMIN
        if (!currentUser.getRoles().contains(Role.ADMIN.name())) {

            UserTask ownerUT = userTaskRepository
                    .findByTaskAndUserAndStatus(
                            task,
                            currentUser,
                            MemberStatus.ACCEPTED
                    )
                    .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN));

            if (ownerUT.getRole() != TaskRole.OWNER)
                throw new AppException(ErrorCode.FORBIDDEN);
        }

        Users member = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserTask ut = userTaskRepository
                .findByTaskAndUserAndStatus(
                        task,
                        member,
                        MemberStatus.ACCEPTED
                )
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        // KICK → đổi status
        ut.setStatus(MemberStatus.LEFT);

        return ApiResponse.success("Member removed from task");
    }
    @Transactional
    public ApiResponse<String> leaveTask(int taskId) {

        Users currentUser = getCurrentUser();

        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        UserTask ut = userTaskRepository
                .findByTaskAndUserAndStatus(
                        task,
                        currentUser,
                        MemberStatus.ACCEPTED
                )
                .orElseThrow(() -> new AppException(ErrorCode.NOT_A_TASK_MEMBER));

        // OWNER không được leave trực tiếp
        if (ut.getRole() == TaskRole.OWNER)
            throw new AppException(ErrorCode.OWNER_CANNOT_LEAVE);

        ut.setStatus(MemberStatus.LEFT);

        return ApiResponse.success("Left task successfully");
    }

}
