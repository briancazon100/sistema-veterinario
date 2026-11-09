package com.veterinaria.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service

public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String subirArchivo(MultipartFile archivo) throws IOException {

        String nombreOriginal = archivo.getOriginalFilename();

        if (nombreOriginal == null || nombreOriginal.isBlank()) {
            nombreOriginal = "archivo";
        }

        // Limpiar posibles rutas y conservar el nombre del archivo.
        nombreOriginal = nombreOriginal.replace("\\", "/");

        nombreOriginal = nombreOriginal.substring(
                nombreOriginal.lastIndexOf("/") + 1
        );

        Map<String, Object> opciones = ObjectUtils.asMap(
                "resource_type", "auto",
                "use_filename", true,
                "unique_filename", true,
                "filename_override", nombreOriginal
        );

        Map subida = cloudinary.uploader().upload(
                archivo.getBytes(),
                opciones
        );

        Object secureUrl = subida.get("secure_url");

        if (secureUrl == null) {
            throw new IOException(
                    "Cloudinary no devolvió una URL segura."
            );
        }

        return secureUrl.toString();
    }
}