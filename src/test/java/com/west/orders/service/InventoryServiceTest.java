package com.west.orders.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.west.orders.dto.response.CupcakeResponseModel;
import com.west.orders.entity.Cupcake;
import com.west.orders.entity.Image;
import com.west.orders.repository.CupcakeRepository;
import com.west.orders.repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private CupcakeRepository cupcakeRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    public void shouldReturnCupcakes() {

        List<Image> images = createImages();
        List<Cupcake> testCupcakes = createCupcakes(images);
        List<CupcakeResponseModel> expectedResponse = createExpectedResponse();

        S3Object s3ChocolateCupcake = new S3Object();
        s3ChocolateCupcake.setObjectContent(new ByteArrayInputStream("chocolateCupcake".getBytes()));

        S3Object s3VanillaCupcake = new S3Object();
        s3VanillaCupcake.setObjectContent(new ByteArrayInputStream("vanillaCupcake".getBytes()));

        when(imageRepository.findAll()).thenReturn(images);
        when(cupcakeRepository.findAll()).thenReturn(testCupcakes);
        when(s3Client.getObject(anyString(), eq(images.get(0).getObjectKey()))).thenReturn(s3ChocolateCupcake);
        when(s3Client.getObject(anyString(), eq(images.get(1).getObjectKey()))).thenReturn(s3VanillaCupcake);

        List<CupcakeResponseModel> cupcakes = inventoryService.getCupcakes();

        assertThat(cupcakes).isNotNull();
        assertThat(cupcakes.size()).isEqualTo(expectedResponse.size());
        assertThat(cupcakes.get(0).getFlavour()).isEqualTo(expectedResponse.get(0).getFlavour());
        assertThat(cupcakes.get(0).getPrice()).isEqualTo(expectedResponse.get(0).getPrice());
        assertThat(cupcakes.get(0).getImage()).isNotBlank();
        assertThat(cupcakes.get(1).getFlavour()).isEqualTo(expectedResponse.get(1).getFlavour());
        assertThat(cupcakes.get(1).getPrice()).isEqualTo(expectedResponse.get(1).getPrice());
        assertThat(cupcakes.get(1).getImage()).isNotBlank();
    }

    @Test
    public void shouldReturnCupcakeWithEmptyImage_ifS3ClientThrowsException() {

        List<Image> images = createImages();
        List<Cupcake> testCupcakes = createCupcakes(images);
        List<CupcakeResponseModel> expectedResponse = createExpectedResponseWithEmptyImage();


        S3Object s3ChocolateCupcake = new S3Object();
        s3ChocolateCupcake.setObjectContent(new ByteArrayInputStream("chocolateCupcake".getBytes()));

        InputStream errorStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Image unable to be retrieved");
            }
        };
        S3Object s3VanillaCupcake = new S3Object();
        s3VanillaCupcake.setObjectContent(errorStream);

        when(imageRepository.findAll()).thenReturn(images);
        when(cupcakeRepository.findAll()).thenReturn(testCupcakes);
        when(s3Client.getObject(anyString(), eq(images.get(0).getObjectKey()))).thenReturn(s3ChocolateCupcake);
        when(s3Client.getObject(anyString(), eq(images.get(1).getObjectKey()))).thenReturn(s3VanillaCupcake);

        List<CupcakeResponseModel> cupcakes = inventoryService.getCupcakes();

        assertThat(cupcakes).isNotNull();
        assertThat(cupcakes.size()).isEqualTo(expectedResponse.size());
        assertThat(cupcakes.get(0).getFlavour()).isEqualTo(expectedResponse.get(0).getFlavour());
        assertThat(cupcakes.get(0).getPrice()).isEqualTo(expectedResponse.get(0).getPrice());
        assertThat(cupcakes.get(0).getImage()).isNotBlank();
        assertThat(cupcakes.get(1).getFlavour()).isEqualTo(expectedResponse.get(1).getFlavour());
        assertThat(cupcakes.get(1).getPrice()).isEqualTo(expectedResponse.get(1).getPrice());
        assertThat(cupcakes.get(1).getImage()).isBlank();
    }

    private List<Image> createImages() {
        Image chocolateCupcakeImage = new Image(1L, "CHOC001", "bucketName", "chocKey");
        Image vanillaCupcakeImage = new Image(2L, "VAN001", "bucketName", "vanKey");
        return List.of(chocolateCupcakeImage, vanillaCupcakeImage);
    }

    private List<Cupcake> createCupcakes(List<Image> images) {
        Cupcake chocolateCupcake = new Cupcake(1L, "CHOC001", Cupcake.Flavour.CHOCOLATE,
                BigDecimal.valueOf(3.50), images.get(0));
        Cupcake vanillaCupcake = new Cupcake(2L, "VAN001", Cupcake.Flavour.VANILLA,
                BigDecimal.valueOf(3.50), images.get(1));
        return List.of(chocolateCupcake, vanillaCupcake);
    }

    private List<CupcakeResponseModel> createExpectedResponse() {
        return List.of(CupcakeResponseModel.builder()
                        .productCode("CHOC001")
                        .flavour(Cupcake.Flavour.CHOCOLATE)
                        .price(BigDecimal.valueOf(3.50))
                        .image("chocolateCupcakeImage")
                        .build(),
                CupcakeResponseModel.builder()
                        .productCode("VAN001")
                        .flavour(Cupcake.Flavour.VANILLA)
                        .price(BigDecimal.valueOf(3.50))
                        .image("vanillaCupcakeImage")
                        .build()
        );
    }

    private List<CupcakeResponseModel> createExpectedResponseWithEmptyImage() {
        return List.of(CupcakeResponseModel.builder()
                        .productCode("CHOC001")
                        .flavour(Cupcake.Flavour.CHOCOLATE)
                        .price(BigDecimal.valueOf(3.50))
                        .image("chocolateCupcakeImage")
                        .build(),
                CupcakeResponseModel.builder()
                        .productCode("VAN001")
                        .flavour(Cupcake.Flavour.VANILLA)
                        .price(BigDecimal.valueOf(3.50))
                        .image("") // Expecting empty image due to IOException
                        .build()
        );
    }
}