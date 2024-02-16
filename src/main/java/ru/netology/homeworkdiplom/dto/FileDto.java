package ru.netology.homeworkdiplom.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDto {

    @JsonProperty("filename")
    private String fileName;
    private String fileType;
    private byte[] fileData;
    private String hash;
    private Long size;

}