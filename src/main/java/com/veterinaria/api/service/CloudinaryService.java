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

    // Inyectamos el bean de Cloudinary que creaste en la clase de configuración
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String subirArchivo(MultipartFile archivo) throws IOException {
        // Configuramos Cloudinary para que detecte automáticamente el tipo de archivo (imagen, video, pdf, etc.)
        Map<String, Object> opciones = ObjectUtils.asMap(
                "resource_type", "auto"
        );

        // Subimos el archivo pasándole los bytes y las opciones
        Map subida = cloudinary.uploader().upload(archivo.getBytes(), opciones);

        return subida.get("url").toString();
    }
}