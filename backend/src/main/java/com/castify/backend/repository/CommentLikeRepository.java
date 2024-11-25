package com.castify.backend.repository;

import com.castify.backend.entity.CommentLikeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends MongoRepository<CommentLikeEntity, String> {
    long countByCommentEntityId(String commentId);
    Optional<CommentLikeEntity> findByUserEntityIdAndCommentEntityId(String userId, String commentId);
}
