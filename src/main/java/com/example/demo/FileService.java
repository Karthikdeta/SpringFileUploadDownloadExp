package com.example.demo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileRepository fileRepository;

    public void saveFileToDB(MultipartFile file) throws IOException {
        try {
            File fileEntity = File.builder()
                    .fileName(FilenameUtils.removeExtension(file.getOriginalFilename()))
                    .fileType(FilenameUtils.getExtension(file.getOriginalFilename()))
                    .fileCreatedBy("SYSTEM").fileContent(file.getBytes()).build();
            fileRepository.save(fileEntity);
        } catch (IOException e) {
            log.error("Error occured while saving the file into the database", e);
            throw new IOException("Error occured while saving the file into the database");
        }
    }

    public ResponseEntity<Resource> downloadFileFromDB(int id) throws Exception {
        Optional<File> file = fileRepository.findById(id);
        if (file.isPresent()) {
            File fileEntity = file.get();
            byte[] bytes = fileEntity.getFileContent();
            System.out.println(bytes.length);
            String fileNameWithExt = fileEntity.getFileName() + "." + fileEntity.getFileType();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileNameWithExt + "\"")
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .contentLength(bytes.length)
                    .body(new InputStreamResource(new ByteArrayInputStream(bytes)));
        } else {
            log.error("File not found for id - {}", id);
            throw new Exception("File not found");
        }
    }

    public List<FileDTO> getFileList() {
        return fileRepository.findAll().stream().map(file -> {
            return FileDTO.builder().id(file.getId())
                    .fileName(file.getFileName() + "." + file.getFileType())
                    .fileCreatedBy(file.getFileCreatedBy()).fileCreatedTs(file.getFileCreatedTs())
                    .build();
        }).collect(Collectors.toList());
    }

}
