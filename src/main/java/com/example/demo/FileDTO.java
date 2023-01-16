package com.example.demo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FileDTO {

    private int id;
    private String fileName;
    private String fileCreatedBy;
    private LocalDateTime fileCreatedTs;

}
