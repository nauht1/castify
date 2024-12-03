package com.castify.backend.service.userActivity;

import com.castify.backend.entity.CommentEntity;
import com.castify.backend.entity.PodcastEntity;
import com.castify.backend.entity.UserActivityEntity;
import com.castify.backend.entity.UserEntity;
import com.castify.backend.enums.ActivityType;
import com.castify.backend.models.PageDTO;
import com.castify.backend.models.podcast.PodcastModel;
import com.castify.backend.models.userActivity.AddActivityRequestDTO;
import com.castify.backend.models.userActivity.UserActivityModel;
import com.castify.backend.repository.CommentRepository;
import com.castify.backend.repository.PodcastRepository;
import com.castify.backend.repository.UserActivityRepository;
import com.castify.backend.service.user.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserActivityServiceImpl implements IUserActivityService{
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PodcastRepository podcastRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void addActivity(AddActivityRequestDTO requestDTO) throws Exception {
        UserEntity user = userService.getUserByAuthentication();

        PodcastEntity podcastEntity = null;
        if (requestDTO.getPodcastId() != null) {
            podcastEntity = podcastRepository.findById(requestDTO.getPodcastId())
                    .orElseThrow(() -> new RuntimeException("Podcast not found"));
        }

        CommentEntity commentEntity = null;
        if (requestDTO.getCommentId() != null) {
            commentEntity = commentRepository.findById(requestDTO.getCommentId())
                    .orElseThrow(() -> new RuntimeException("Comment not found"));
        }

        // Kiểm tra nếu hoạt động đã tồn tại
        UserActivityEntity existingActivity = userActivityRepository.findByUserAndTypeAndPodcast(
                user, requestDTO.getType(), podcastEntity);

        if (existingActivity != null) {
            // Nếu đã tồn tại, cập nhật timestamp
            existingActivity.setTimestamp(LocalDateTime.now());
            userActivityRepository.save(existingActivity);
        } else {
            // Nếu chưa tồn tại, tạo mới
            UserActivityEntity activity = new UserActivityEntity();
            activity.setUser(user);
            activity.setType(requestDTO.getType());
            activity.setPodcast(podcastEntity);
            activity.setComment(commentEntity);
            activity.setTimestamp(LocalDateTime.now());
            userActivityRepository.save(activity);
        }
    }

    @Override
    public PageDTO<UserActivityModel> getViewPodcastActivitiesByDate(int page) throws Exception {
        UserEntity user = userService.getUserByAuthentication();

        // Lấy toàn bộ các hoạt động VIEW_PODCAST của user, sắp xếp giảm dần theo timestamp
        List<UserActivityEntity> activities = userActivityRepository.findAllByUserIdAndType(
                user.getId(),
                ActivityType.VIEW_PODCAST,
                Sort.by(Sort.Direction.DESC, "timestamp")
        );

        // Nếu không có hoạt động nào, trả về PageDTO với nội dung rỗng
        if (activities.isEmpty()) {
            PageDTO<UserActivityModel> emptyPageDTO = new PageDTO<>();
            emptyPageDTO.setContent(Collections.emptyList());
            emptyPageDTO.setCurrentPage(page);
            emptyPageDTO.setTotalPages(0);
            emptyPageDTO.setTotalElements(0);
            return emptyPageDTO;
        }

        // Nhóm hoạt động theo ngày
        Map<LocalDate, List<UserActivityEntity>> groupedByDate = activities.stream()
                .collect(Collectors.groupingBy(activity -> activity.getTimestamp().toLocalDate()));

        // Chuyển đổi Map sang danh sách được phân trang
        List<LocalDate> dates = new ArrayList<>(groupedByDate.keySet());
        dates.sort(Comparator.reverseOrder()); // Sắp xếp theo ngày giảm dần

        if (page >= dates.size()) {
            throw new RuntimeException("Page out of range");
        }

        // Lấy danh sách hoạt động theo ngày của trang hiện tại
        LocalDate selectedDate = dates.get(page);
        List<UserActivityEntity> activitiesForSelectedDate = groupedByDate.get(selectedDate);

        // Chuyển đổi sang UserActivityModel
        List<UserActivityModel> activityModels = activitiesForSelectedDate.stream()
                .map(entity -> {
                    UserActivityModel model = new UserActivityModel();
                    model.setId(entity.getId());
                    model.setType(entity.getType());

                    // Mapping PodcastEntity sang PodcastModel (nếu có)
                    if (entity.getPodcast() != null) {
                        PodcastModel podcastModel = modelMapper.map(entity.getPodcast(), PodcastModel.class);
                        model.setPodcast(podcastModel);
                    }

                    model.setTimestamp(entity.getTimestamp());
                    return model;
                })
                .toList();

        // Tạo đối tượng PageDTO
        PageDTO<UserActivityModel> pageDTO = new PageDTO<>();
        pageDTO.setContent(activityModels);
        pageDTO.setCurrentPage(page);
        pageDTO.setTotalPages(dates.size());
        pageDTO.setTotalElements(activities.size());

        return pageDTO;
    }

    @Override
    public void removeViewPodcastActivity(String actId) {
        UserActivityEntity userActivity = userActivityRepository.findByIdAndType(actId, ActivityType.VIEW_PODCAST);
        if (userActivity == null) return;

        userActivityRepository.delete(userActivity);
    }

    @Override
    public void removeAllViewPodcastActivities() throws Exception {
        UserEntity user = userService.getUserByAuthentication();
        // Lấy danh sách các activity của user có type VIEW_PODCAST
        List<UserActivityEntity> activities = userActivityRepository.findAllByUserIdAndType(user.getId(), ActivityType.VIEW_PODCAST);

        if (activities.isEmpty()) {
            return;
        }

        userActivityRepository.deleteAll(activities);
    }
}
