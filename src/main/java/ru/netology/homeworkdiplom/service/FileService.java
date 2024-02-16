package ru.netology.homeworkdiplom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.netology.homeworkdiplom.dto.FileDto;
import ru.netology.homeworkdiplom.entity.FileEntity;
import ru.netology.homeworkdiplom.entity.UserEntity;
import ru.netology.homeworkdiplom.exception.FileNotFoundException;
import ru.netology.homeworkdiplom.exception.IncorrectDataEntry;
import ru.netology.homeworkdiplom.model.FileBody;
import ru.netology.homeworkdiplom.repository.FileRepository;
import ru.netology.homeworkdiplom.security.JWTToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final JWTToken jwtToken;

    @Transactional
    public void addFile(MultipartFile file, String fileName) {
        Long userID = jwtToken.getAuthenticatedUser().getId();

        log.info("Поиск файла в хранилище по имени файла {} и идентификатору {}", fileName, userID);
        fileRepository.findFileByUserEntityIdAndFileName(userID, fileName).ifPresent(s -> {
            log.info("Файл не найден в базе данных по имени вайла {} и Id {}", fileName, userID);
            throw new IncorrectDataEntry("Файл с таким именем: { " + fileName + " } уже существует", userID);
        });

        String hash = null;
        byte[] fileData = null;
        try {
            hash = checksum(file);
            fileData = file.getBytes();
            log.info("Получен массив байтов файла и сгенерирован хэш файла:{}", hash);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        log.info("Проверка наличия файла в базе данных по Id пользователя:{} и хэшу:{}", userID, hash);
        if (fileRepository.findFileByUserEntityIdAndHash(userID, hash).isPresent()) {
            log.info("Файл с таким хэшом: {} у пользователя с Id: {} существует", hash, userID);
            return;
        }
        log.info("Файл в базе данных по Id пользователя:{} и хэшу:{} не существует", userID, hash);

        FileEntity createdFile = FileEntity.builder()
                .fileName(fileName)
                .fileType(file.getContentType())
                .fileData(fileData)
                .hash(hash)
                .size(file.getSize())
                .userEntity(UserEntity.builder().id(userID).build())
                .build();

        log.info("Файл создан и сохранен в базе данных. Имя файла: {}, хэш: {}, Id пользователя: {}", fileName, hash, userID);
        fileRepository.save(createdFile);
    }

    private String checksum(MultipartFile file) throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        try (var fis = file.getInputStream();
             var bis = new BufferedInputStream(fis);
             var dis = new DigestInputStream(bis, md)) {

            while (dis.read() != -1) ;
            md = dis.getMessageDigest();
        }

        var result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        log.info("Контрольная сумма успешно сгенерирована для файла: {}", file.getName());
        return result.toString();
    }

    @Transactional
    public FileDto getFile(String fileName) {
        Long userID = jwtToken.getAuthenticatedUser().getId();

        log.info("Поиск файла в базе данных по имени файла: {} и Id пользователя: {}", fileName, userID);
        FileEntity file = fileRepository.findFileByUserEntityIdAndFileName(userID, fileName).orElseThrow(() -> new FileNotFoundException(
                "Файл с именем: { " + fileName + " } не найден", userID));

        log.info("Загрузка файла: {} из базы данных. Id пользователя: {}", fileName, userID);
        return FileDto.builder()
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .fileData(file.getFileData())
                .build();
    }

    public void renameFile(String fileName, FileBody fileBody) {
        Long userID = jwtToken.getAuthenticatedUser().getId();

        log.info("Поиск файла для переименования в базе данных по имени файла: {} и Id пользователя: {}", fileName, userID);
        FileEntity fileToRename = fileRepository.findFileByUserEntityIdAndFileName(userID, fileName)
                .orElseThrow(() -> new FileNotFoundException("Файл с именем: { " + fileName + " } не найден", userID));

        log.info("Проверка нового имени файла в базе данных по имени файла: {} и Id пользователя: {}", fileBody.getFileName(), userID);
        fileRepository.findFileByUserEntityIdAndFileName(userID, fileBody.getFileName()).ifPresent(s -> {
            log.info("Файл не найден в базе данных по имени вайла: {} и Id пользователя: {}", fileName, userID);
            throw new IncorrectDataEntry("Файл с таким именем: { " + fileName + " } уже существует", userID);
        });

        log.info("Переименование файла в базе данных. Старое имя файла: {}, новое имя файла: {}, Id пользователя: {}",
                fileName, fileToRename, userID);
        fileToRename.setFileName(fileBody.getFileName());
        fileRepository.save(fileToRename);
    }

    public void deleteFile(String fileName) {
        Long userID = jwtToken.getAuthenticatedUser().getId();

        log.info("Поиск файла для удаления в базе данных по имени файла: {} и Id пользователя: {}", fileName, userID);
        FileEntity fileFromStorage = fileRepository.findFileByUserEntityIdAndFileName(userID, fileName).orElseThrow(() -> new FileNotFoundException(
                "Файл с именем: { " + fileName + " } не найден", userID));

        log.info("Удаление файла из базы данных по имени файла: {} и Id пользователя: {}", fileFromStorage, userID);
        fileRepository.deleteById(fileFromStorage.getId());
    }

    public List<FileDto> getAllFiles(int limit) {
        Long userId = jwtToken.getAuthenticatedUser().getId();

        log.info("Поиск всех файлов в базе данных по Id пользователя: {} и лимиту вывода: {}", userId, limit);
        List<FileEntity> listFiles = fileRepository.findFilesByUserIdWithLimit(userId, limit);

        log.info("Все файлы в базе данных по Id пользователя: {} и лимиту вывода: {} найдены | Список файлов: {}", userId, limit, listFiles);
        return listFiles.stream()
                .map(file -> FileDto.builder()
                        .fileName(file.getFileName())
                        .size(file.getSize())
                        .build()).collect(Collectors.toList());
    }
}