package com.maksudsharif.camelpoc.web;

import com.maksudsharif.camelpoc.service.EcmFileService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Log4j2
@Controller
public class FileAPIController
{
    private EcmFileService ecmFileService;

    @RequestMapping(value = "/api/latest/file", method = RequestMethod.GET)
    public ResponseEntity downloadById(@RequestParam(value = "id") String nodeId)
    {
        ContentStream contentStream = ecmFileService.downloadById(nodeId);
        String fileName = contentStream.getFileName().split("\\^")[1];
        ContentDisposition attachement = ContentDisposition.builder("attachment").creationDate(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())).filename(fileName, Charset.defaultCharset()).size(contentStream.getLength()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(attachement);
        headers.setContentLength(contentStream.getLength());
        headers.setContentType(MediaType.parseMediaType(contentStream.getMimeType()));
        InputStreamResource streamResource = new InputStreamResource(contentStream.getStream());
        return new ResponseEntity(streamResource, headers, HttpStatus.OK);
    }
}
