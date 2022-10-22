package vadim.andreich.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/put")
    @ResponseBody
    public String putFile(@RequestParam("file") MultipartFile files) throws IOException {
        File file = new File("files", Objects.requireNonNull(files.getOriginalFilename()));
        if (!file.exists()){
            RandomAccessFile raf = new RandomAccessFile(file,"rw");
            raf.write(files.getBytes());
        }
        return "true";
    }

    @GetMapping(value = "/get", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody ResponseEntity<ByteArrayResource> getFile(@RequestBody String name) throws IOException {
        final ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(Paths.get(
                 name
        )));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .body(inputStream);
    }



}
