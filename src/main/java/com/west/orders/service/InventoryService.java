package com.west.orders.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.west.orders.dto.response.CupcakeResponseModel;
import com.west.orders.entity.Cupcake;
import com.west.orders.entity.Image;
import com.west.orders.repository.CupcakeRepository;
import com.west.orders.repository.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
@Service
public class InventoryService {
    private CupcakeRepository cupcakeRepository;
    private ImageRepository imageRepository;
    private AmazonS3 s3Client;

    public List<CupcakeResponseModel> getCupcakes() {

        Map<String, Image> imageMap = imageRepository.findAll().stream()
                .collect(Collectors.toMap(Image::getProductCode, image -> image));

        List<Cupcake> cupcakes = cupcakeRepository.findAll();

        return cupcakes.stream().map(cupcake -> {
            String productCode = cupcake.getProductCode();
            Image image = imageMap.get(productCode);

            try {
                return CupcakeResponseModel.builder()
                        .productCode(cupcake.getProductCode())
                        .flavour(cupcake.getFlavour())
                        .price(cupcake.getPrice())
                        .image(s3ToBytes(s3Client,
                                image.getBucketName(),
                                image.getObjectKey()))
                        .build();
            } catch (IOException e) {
                log.error("IOException occurred: {}", e.getMessage());
                return CupcakeResponseModel.builder()
                        .productCode(cupcake.getProductCode())
                        .flavour(cupcake.getFlavour())
                        .price(cupcake.getPrice())
                        .image("") // Return an empty string if unable to retrieve image
                        .build();
            }
        }).collect(Collectors.toList());
    }

    private String s3ToBytes(AmazonS3 s3Client, String bucketName, String objectKey) throws IOException {
        S3Object s3Object = s3Client.getObject(bucketName, objectKey);
        try (InputStream inputStream = s3Object.getObjectContent()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return Base64.getEncoder().encodeToString(buffer.toByteArray());
        }
    }
}
