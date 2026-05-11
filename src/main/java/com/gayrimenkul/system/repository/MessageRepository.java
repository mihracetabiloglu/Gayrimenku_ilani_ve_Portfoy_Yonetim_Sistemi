package com.gayrimenkul.system.repository;

import com.gayrimenkul.system.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    List<Message> findBySenderIdOrderByCreatedAtDesc(Long senderId);

    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtAsc(
            Long senderId1, Long receiverId1, Long senderId2, Long receiverId2);

    List<Message> findByPropertyIdOrderByCreatedAtAsc(Long propertyId);

    Optional<Message> findByIdAndSenderIdOrIdAndReceiverId(Long id1, Long senderId, Long id2, Long receiverId);

    @Query("""
            select m from Message m
            where ((m.sender.id = :user1Id and m.receiver.id = :user2Id)
               or (m.sender.id = :user2Id and m.receiver.id = :user1Id))
              and (:propertyId is null or m.property.id = :propertyId)
            order by m.createdAt asc
            """)
    List<Message> findThread(@Param("user1Id") Long user1Id,
                             @Param("user2Id") Long user2Id,
                             @Param("propertyId") Long propertyId);
}
