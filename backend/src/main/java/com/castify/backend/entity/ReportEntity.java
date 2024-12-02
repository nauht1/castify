package com.castify.backend.entity;

import com.castify.backend.enums.ReportType;
import org.springframework.data.annotation.Id; // Sửa dòng này
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "report")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportEntity {
    @Id
    private String id;
    private String title;
    private String detail;
    private ReportType type;
    private String target;
    private LocalDateTime createdDay;

    @DBRef
    private User user;

}
