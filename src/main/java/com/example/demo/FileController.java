package com.example.demo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import javax.activation.MimetypesFileTypeMap;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class FileController {

    private MimetypesFileTypeMap mimetypesFileTypeMap; // requires activation maven dependency

    private final FileService fileService;

    @PostMapping("uploadFile")
    public String fileUpload(@RequestParam("file") MultipartFile file) throws Exception {
        System.out.println(file.getContentType());
        System.out.println(file.getName()); // file
        System.out.println(file.getOriginalFilename()); // support.log
        Path path = Paths
                .get("D:\\Karthik\\Code\\Hands-on\\FileExperiment\\" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return "File Upload Successfully";
    }

    @PostMapping("uploadFiles")
    public String multipleFileUpload(@RequestParam("files") MultipartFile[] files)
            throws Exception {
        for (MultipartFile file : files) {
            System.out.println(file.getContentType());
            System.out.println(file.getName()); // file
            System.out.println(file.getOriginalFilename()); // support.log
            Path path = Paths.get(
                    "D:\\Karthik\\Code\\Hands-on\\FileExperiment\\" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }
        return "File Upload Successfully";
    }

    @GetMapping("downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName)
            throws Exception {
        Path path = Paths.get("D:\\Karthik\\Code\\Hands-on\\FileExperiment\\" + fileName);
        //InputStream fileInputStream = Files.newInputStream(path, StandardOpenOption.READ);
        //byte[] bytes = fileInputStream.readAllBytes();
        //System.out.println(fileInputStream.readAllBytes().length);
        //mimetypesFileTypeMap = new MimetypesFileTypeMap(fileInputStream);
        //String mimeType = mimetypesFileTypeMap.getContentType(fileName);
        //System.out.println(mimeType);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                //.contentLength(bytes.length)
                .body(new FileSystemResource(path));
    }

    @PostMapping("uploadFileToDB")
    public String fileUploadToDB(@RequestParam("file") MultipartFile file) throws Exception {
        fileService.saveFileToDB(file);
        return "File saved successfully to the Database";
    }

    @PostMapping("uploadFilesToDB")
    public String multipleFilesUploadToDB(@RequestParam("files") MultipartFile[] files)
            throws Exception {
        for (MultipartFile file : files) {
            fileService.saveFileToDB(file);
        }
        return "Files saved successfully to the Database";
    }

    @GetMapping("downloadFileFromDB")
    public ResponseEntity<Resource> downloadFileFromDB(@RequestParam("id") int id)
            throws Exception {
        return fileService.downloadFileFromDB(id);
    }

    @GetMapping("getAllFiles")
    public List<FileDTO> getFileList() {
        return fileService.getFileList();
    }
}
